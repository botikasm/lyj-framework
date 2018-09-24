package org.lyj.ext.netty.server.web.handlers.impl.upload;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.ext.netty.server.web.*;
import org.lyj.ext.netty.server.web.controllers.routing.RouteUrl;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;
import org.lyj.ext.netty.server.web.handlers.impl.upload.resumablejs.ResumableJs;

/**
 * Handle Multipart request for upload of file.
 */
public class UploadHandler
        extends AbstractRequestHandler {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;

    private RouteUrl _route;
    private Delegates.FunctionArg<UploadFileInfo, String> _callback;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskFileUpload.baseDirectory = PathUtils.getTemporaryDirectory("upload");// null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskAttribute.baseDirectory = PathUtils.getTemporaryDirectory("upload");
    }

    private UploadHandler(final HttpServerConfig config) {
        super(config);

        _route = new RouteUrl(config.uploadRoute(), config.encoding());
        _root = PathUtils.getAbsolutePath(config().uploadDir());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void close() {

    }

    @Override
    public void handle(final HttpServerRequest request,
                       final HttpServerResponse response) {
        if (canHandle(request)) {
            final String method = request.method();
            if (IHttpConstants.METHOD_POST.equalsIgnoreCase(method)) {
                handlePOST(request, response);
            } else if (IHttpConstants.METHOD_GET.equalsIgnoreCase(method)) {
                handleGET(request, response);
            } else if (IHttpConstants.METHOD_OPTIONS.equalsIgnoreCase(method)) {
                handleOPTIONS(request, response);
            }

            response.handled(true); // exit chain
        } else {
            response.handled(false);
        }
    }

    public void onFileUpload(final Delegates.FunctionArg<UploadFileInfo, String> callback) {
        _callback = callback;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean canHandle(final HttpServerRequest request) {
        final String method = request.method();
        final String uri = request.uri();
        // only POST method is supported for upload
        if (IHttpConstants.METHOD_POST.equalsIgnoreCase(method)) {
            // is it a valid upload route?
            if (_route.parse(uri).matchTemplate()) {

                return true;
            }
        } else if (IHttpConstants.METHOD_GET.equalsIgnoreCase(method)) {
            // GET method is invoked for multichunk
            if (_route.parse(uri).matchTemplate()) {

                return true;
            }
        } else if (IHttpConstants.METHOD_OPTIONS.equalsIgnoreCase(method)) {
            // OPTIONS method is invoked for multichunk
            if (_route.parse(uri).matchTemplate()) {

                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    private void handlePOST(final HttpServerRequest request,
                            final HttpServerResponse response) {
        final HttpServerContext context = new HttpServerContext(config(), request, response);
        try {
            final UploadReader reader = new UploadReader(config());
            final UploadFileInfo file_info = reader.read(context);
            // write response
            if (null != file_info) {

                // callback to handler
                if (null != _callback) {
                    try {
                        final String error_message = Delegates.invoke(_callback, file_info);
                        file_info.errorMessage(error_message);
                    } catch (Throwable t) {
                        file_info.errorMessage(ExceptionUtils.getRealMessage(t));
                    }
                }

                context.writeJson(file_info);
            } else {
                context.writeStatus(HttpResponseStatus.OK);
            }

        } catch (Throwable t) {
            context.writeInternalServerError(t);
        }
    }

    private void handleGET(final HttpServerRequest request,
                           final HttpServerResponse response) {
        if (ResumableJs.isValid(request)) {
            final ResumableJs resumable = new ResumableJs(request);
            if (resumable.totalChunks() > 1) {
                final HttpServerContext context = new HttpServerContext(config(), request, response);
                context.writeStatus(resumable.status());
            } else {
                final HttpServerContext context = new HttpServerContext(config(), request, response);
                context.writeStatus(HttpResponseStatus.NO_CONTENT);
            }
        } else {
            final HttpServerContext context = new HttpServerContext(config(), request, response);
            context.writeStatus(HttpResponseStatus.OK);
        }
    }

    private void handleOPTIONS(final HttpServerRequest request,
                               final HttpServerResponse response) {
        //response.write200OK();
        final HttpServerContext context = new HttpServerContext(config(), request, response);
        context.writeStatus(HttpResponseStatus.OK);
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static UploadHandler create(final HttpServerConfig config) {
        return new UploadHandler(config);
    }


}
