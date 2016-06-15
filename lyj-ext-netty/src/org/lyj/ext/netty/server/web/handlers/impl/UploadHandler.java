package org.lyj.ext.netty.server.web.handlers.impl;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.*;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.*;
import org.lyj.ext.netty.server.web.controllers.routing.RouteUrl;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handle Multipart request for upload of file.
 */
public class UploadHandler
        extends AbstractRequestHandler {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PARAM_FILE_NAME = "file_name";
    private static final String PARAM_DIRECTORY = "directory";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpDataFactory _factory; // Disk if size exceed
    private final String _root;
    private HttpPostRequestDecoder _decoder;
    private RouteUrl _route;
    private boolean _reading_chunks;

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
        }
    }

    @Override
    public void handle(final HttpServerRequest request,
                       final HttpServerResponse response) {
        if (canHandle(request)) {

            try {
                // initialize decoder
                if (request.isHttpRequest()) {
                    _decoder = request.createDecoder(_factory);
                    if (null != _decoder) {
                        _reading_chunks = request.isTransferEncodingChunked();
                    }
                }
                // read content
                if (request.isHttpContent()) {
                    if (null != _decoder) {
                        // New chunk is received
                        HttpContent chunk = request.nativeHttpContent();
                        _decoder.offer(chunk);

                        readHttpDataChunkByChunk(new HttpServerContext(config(), request, response));
                        // example of reading only if at the end
                        if (chunk instanceof LastHttpContent) {
                            //writeResponse(ctx.channel());
                            _reading_chunks = false;

                            reset();
                        }
                    }
                }

            } catch (Throwable t) {
                response.writeErrorINTERNAL_SERVER_ERROR(t);
            }
            response.handled(true); // exit chain
        } else {
            response.handled(false);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean canHandle(final HttpServerRequest request) {
        // only POST method is supported for upload
        if (IHttpConstants.METHOD_POST.equalsIgnoreCase(request.method())) {
            // is it a valid upload route?
            final String uri = request.uri();
            if (_route.parse(uri).matchTemplate()) {

                return true;
            }
        }
        return false;
    }

    private void reset() {
        _reading_chunks = false;
        // destroy the decoder to release all resources
        _decoder.destroy();
        _decoder = null;
    }

    /**
     * Example of reading request by chunk and getting values from chunk to chunk
     */
    private void readHttpDataChunkByChunk(final HttpServerContext context) {
        try {
            while (_decoder.hasNext()) {
                InterfaceHttpData data = _decoder.next();
                if (data != null) {
                    try {
                        // new value
                        this.writeHttpData(context, data);
                    } finally {
                        data.release();
                    }
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
            // end
            //responseContent.append("\r\n\r\nEND OF CONTENT CHUNK BY CHUNK\r\n\r\n");

        } catch (Throwable t) {

        }
    }

    private void writeHttpData(final HttpServerContext context,
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
                return;
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

                    final String file_name = this.write(context.params(), fileUpload);
                    context.writeJson(file_name);

                } else {
                    //responseContent.append("\tFile to be continued but should not!\r\n");
                }
            }
        }
    }

    private String write(final HttpParams http_params,
                         final FileUpload fileUpload) throws Exception {
        try {

            // fileUpload.isInMemory();// tells if the file is in Memory or on File
            // fileUpload.renameTo(dest); // enable to move into another File dest
            // decoder.removeFileUploadFromClean(fileUpload); //remove the File of to delete file
            final Map<String, String> params = this.validateParams(http_params, fileUpload);
            final String file_name = params.get(PARAM_FILE_NAME);
            final String directory = params.get(PARAM_DIRECTORY);
            final String content_type = fileUpload.getContentType();
            final long lenght = fileUpload.length();

            final String destination = PathUtils.combine(_root, directory);
            final String destination_file = PathUtils.combine(destination, file_name);
            FileUtils.mkdirs(destination);
            fileUpload.renameTo(new File(destination_file));

            return destination_file;

        } catch (Exception err) {
            throw err;
        }
    }

    private Map<String, String> validateParams(final HttpParams params,
                                               final FileUpload fileUpload) {
        final Map<String, String> response = new HashMap<>();
        response.put(PARAM_DIRECTORY, params.containsKey(PARAM_DIRECTORY) ? params.getString(PARAM_DIRECTORY) : "generic");
        response.put(PARAM_FILE_NAME, params.containsKey(PARAM_FILE_NAME) ? params.getString(PARAM_FILE_NAME) : "");
        try {
            if (!fileUpload.isInMemory() && !StringUtils.hasText(response.get(PARAM_FILE_NAME))) {
                response.put(PARAM_FILE_NAME, fileUpload.getFile().getName());
            }
        } catch (Throwable t) {
        }
        // file name is required
        if(!StringUtils.hasText(response.get(PARAM_FILE_NAME))){
           response.put(PARAM_FILE_NAME, RandomUtils.randomUUID()+".jpg");
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static UploadHandler create(final HttpServerConfig config) {
        return new UploadHandler(config);
    }


}
