package org.lyj.ext.netty.server.web.handlers.impl.upload;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.*;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.*;
import org.lyj.ext.netty.server.web.*;
import org.lyj.ext.netty.server.web.controllers.routing.RouteUrl;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;
import org.lyj.ext.netty.server.web.handlers.impl.upload.resumablejs.ResumableJs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handle Multipart request for upload of file.
 */
public class UploadHandlerOld
        extends AbstractRequestHandler {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpDataFactory _factory; // Disk if size exceed
    private final String _root;

    private HttpPostRequestDecoder _decoder;
    private RouteUrl _route;
    private boolean _reading_chunks;
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

    private UploadHandlerOld(final HttpServerConfig config) {
        super(config);

        _factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed
        _route = new RouteUrl(config.uploadRoute(), config.encoding());
        _root = PathUtils.getAbsolutePath(config().uploadDir());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void close() {
        if (null != _decoder) {
            _decoder.cleanFiles();
            this.resetPostRequestDecoder(null);
        }
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
            final UploadFileInfo file_info = readFile(context);
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


    private UploadFileInfo readFile(final HttpServerContext context) throws Exception {
        try {
            final HttpServerRequest request = context.request();

            // initialize decoder
            this.initRequestDecoder(request);

            final UploadFileInfo file_info = readFileInfo(request, context.params());

            final ResumableJs resumable = new ResumableJs(request);
            if (resumable.isValid() && resumable.totalChunks() > 1) {
                // resumable must store data into FileChunkController
                if (resumable.store(file_info)) {
                    if (resumable.isComplete()) {

                        // all chunks are ready
                        if (resumable.compose(file_info)) {
                            return file_info;
                        }
                    }
                } else {
                    // error storing file.
                    // may be file does not exists
                }
                return null;
            } else {
                return file_info;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void initRequestDecoder(final HttpServerRequest request) throws Exception {
        // initialize decoder
        try {
            if (request.isHttpRequest()) {
                _decoder = request.createDecoder(_factory);
                if (null != _decoder) {
                    _reading_chunks = request.isTransferEncodingChunked();
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void resetPostRequestDecoder(final HttpContent chunk) {
        _reading_chunks = false;
        // destroy the decoder to release all resources
        try {
            _decoder.destroy();
        } catch (Throwable ignored) {
            // ignored
        }
        _decoder = null;
        try {
            if (null != chunk) {
                chunk.content().release();
            }
        } catch (Throwable ignored) {
            // ignored
        }
    }

    private UploadFileInfo readFileInfo(final HttpServerRequest request,
                                  final HttpParams http_params) throws Exception{
        try {
            // read content
            if (request.isHttpContent()) {
                if (null != _decoder) {
                    // New chunk is received
                    HttpContent chunk = request.nativeHttpContent();
                    _decoder.offer(chunk);

                    final UploadFileInfo file_info = readHttpDataChunkByChunk(http_params);

                    // example of reading only if at the end
                    if (chunk instanceof LastHttpContent) {
                        //writeResponse(ctx.channel());
                        _reading_chunks = false;

                        resetPostRequestDecoder(chunk);
                    }
                    return file_info;
                }
            }
            return null;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    /**
     * Example of reading request by chunk and getting values from chunk to chunk
     */
    private UploadFileInfo readHttpDataChunkByChunk(final HttpParams params) {
        try {

            while (_decoder.hasNext()) {
                InterfaceHttpData data = _decoder.next();
                if (data != null) {
                    try {
                        // new value
                        final UploadFileInfo file_info = this.writeHttpData(params, data);
                        if (null != file_info) {
                            return file_info;
                        }
                    } finally {
                        data.release();
                    }
                }
            }

        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
            // end
            //responseContent.append("\r\n\r\nEND OF CONTENT CHUNK BY CHUNK\r\n\r\n");
        } catch (Throwable ignored) {
            // ignored
        }
        return null;
    }

    private UploadFileInfo writeHttpData(final HttpParams params,
                                   final InterfaceHttpData data) throws Exception {
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value;
            try {
                value = attribute.getValue();
            } catch (IOException e1) {
                // Error while reading data from File, only print name and error
                super.error("writeHttpData", e1);
                //responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ": "+ attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n");
                return null;
            }
            if (value.length() > 100) {
                //responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ": "+ attribute.getName() + " data too long\r\n");
            } else {
                //responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ": "+ attribute + "\r\n");
            }
        } else {
            //responseContent.append("\r\nBODY FileUpload: " + data.getHttpDataType().name() + ": " + data + "\r\n");
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                FileUpload fileUpload = (FileUpload) data;
                if (fileUpload.isCompleted()) {

                    return this.write(params.initialize(), fileUpload);

                } else {
                    //responseContent.append("\tFile to be continued but should not!\r\n");
                }
            }
        }
        return null;
    }

    private UploadFileInfo write(final HttpParams http_params,
                           final FileUpload fileUpload) throws Exception {
        try {

            // fileUpload.isInMemory();// tells if the file is in Memory or on File
            // fileUpload.renameTo(dest); // enable to move into another File dest
            // decoder.removeFileUploadFromClean(fileUpload); //remove the File of to delete file
            final Params params = this.validateParams(http_params, fileUpload);
            final String file_name = params.fileName();
            final String directory = params.directory();

            final String local_relative = PathUtils.concat(directory, PathUtils.isFile(file_name) ? file_name : file_name.concat(".upload"));
            final String local_full = PathUtils.concat(_root, local_relative);
            final String download_url = PathUtils.concat(config().downloadRoot(), local_relative);
            final String content_type = fileUpload.getContentType();
            final long content_length = fileUpload.length();

            // move file to destination
            final UploadFileInfo response = new UploadFileInfo();
            if (content_length > 0) {
                FileUtils.mkdirs(local_full);
                fileUpload.renameTo(new File(local_full));
                // paths on server
                response.localeRoot(_root);
                response.localeRelative(local_relative);
                response.localeAbsolute(local_full);

            }

            // other patameters
            response.contentType(content_type);
            response.contentLength(content_length + "");

            // all custom params returned to request sender
            response.putAll(params.getCustom());

            return response;
        } catch (Exception err) {
            throw err;
        }
    }

    private Params validateParams(final HttpParams params,
                                  final FileUpload fileUpload) {
        final Params response = new Params(params);

        // file name is required
        try {
            if (!StringUtils.hasText(response.fileName())) {
                if (StringUtils.hasText(fileUpload.getFilename())) {
                    final String file_name = fileUpload.getFilename(); // fileUpload.getFile().getName();
                    final String ext = PathUtils.getFilenameExtension(file_name, true);
                    response.fileName(StringUtils.hasText(ext)
                            ? file_name
                            : file_name.concat(".upload"));
                }
            }
        } catch (Throwable ignored) {
            // ignored
        }

        // add default filename
        if (!StringUtils.hasText(response.fileName())) {
            response.fileName(RandomUtils.randomUUID() + ".jpg");
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static UploadHandlerOld create(final HttpServerConfig config) {
        return new UploadHandlerOld(config);
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * Url or form parameters
     */
    private static class Params
            extends HashMap<String, String> {


        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String PARAM_FILE_NAME = "file_name";
        private static final String PARAM_DIRECTORY = "directory";

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public Params() {
            super();
            this.init();
        }

        public Params(final HttpParams params) {
            super();
            this.init(params);
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public String directory() {
            return super.get(PARAM_DIRECTORY);
        }

        public String fileName() {
            return super.get(PARAM_FILE_NAME);
        }

        public void fileName(final String value) {
            super.put(PARAM_FILE_NAME, value);
        }

        public Map<String, String> getCustom() {
            final Map<String, String> response = new HashMap<>();
            super.forEach((key, value) -> {
                if (!key.equalsIgnoreCase(PARAM_FILE_NAME) && !key.equalsIgnoreCase(PARAM_DIRECTORY)) {
                    response.put(key, value);
                }
            });
            return response;
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private void init(final HttpParams params) {
            params.toMap().forEach((key, value) -> {
                super.put(key, value.toString());
            });
            this.init();
        }

        private void init() {
            if (!super.containsKey(PARAM_DIRECTORY)) {
                super.put(PARAM_DIRECTORY, "generic");
            }
            if (!super.containsKey(PARAM_FILE_NAME)) {
                super.put(PARAM_FILE_NAME, "");
            }
        }

    }


}
