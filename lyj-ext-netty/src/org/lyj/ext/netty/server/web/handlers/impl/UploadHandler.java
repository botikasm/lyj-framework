package org.lyj.ext.netty.server.web.handlers.impl;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.*;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.*;
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


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpDataFactory _factory; // Disk if size exceed
    private final String _root;

    private HttpPostRequestDecoder _decoder;
    private RouteUrl _route;
    private boolean _reading_chunks;
    private Delegates.FunctionArg<FileInfo, String> _callback;

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
            this.reset();
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

    public void onFileUpload(final Delegates.FunctionArg<FileInfo, String> callback) {
        _callback = callback;
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

                    final FileInfo file_info = this.write(context.params().initialize(), fileUpload);
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
                    //responseContent.append("\tFile to be continued but should not!\r\n");
                }
            }
        }
    }

    private FileInfo write(final HttpParams http_params,
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
            final FileInfo response = new FileInfo();
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

    public static UploadHandler create(final HttpServerConfig config) {
        return new UploadHandler(config);
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

    /**
     * Wrap response information about file uploaded from a request
     */
    public static class FileInfo extends HashMap<String, String> {

        private static final String FLD_LOCALE_ROOT = "local_root";
        private static final String FLD_LOCALE_RELATIVE = "local_relative";
        private static final String FLD_LOCALE_ABSOLUTE = "local_full";
        private static final String FLD_CONTENT_TYPE = "content_type";
        private static final String FLD_CONTENT_LENGTH = "content_length";
        private static final String FLD_ERROR_MESSAGE = "error_message";

        public String localeRoot() {
            return super.get(FLD_LOCALE_ROOT);
        }

        public FileInfo localeRoot(final String value) {
            super.put(FLD_LOCALE_ROOT, value);
            return this;
        }

        public String localeRelative() {
            return super.get(FLD_LOCALE_RELATIVE);
        }

        public FileInfo localeRelative(final String value) {
            super.put(FLD_LOCALE_RELATIVE, value);
            return this;
        }

        public String localeAbsolute() {
            return super.get(FLD_LOCALE_ABSOLUTE);
        }

        public FileInfo localeAbsolute(final String value) {
            super.put(FLD_LOCALE_ABSOLUTE, value);
            return this;
        }

        public String contentType() {
            return super.get(FLD_CONTENT_TYPE);
        }

        public FileInfo contentType(final String value) {
            super.put(FLD_CONTENT_TYPE, value);
            return this;
        }

        public long contentLength() {
            return ConversionUtils.toLong(super.get(FLD_CONTENT_LENGTH), 0L);
        }

        public FileInfo contentLength(final String value) {
            super.put(FLD_CONTENT_LENGTH, value);
            return this;
        }

        public String errorMessage() {
            return super.get(FLD_ERROR_MESSAGE);
        }

        public FileInfo errorMessage(final String value) {
            super.put(FLD_ERROR_MESSAGE, value);
            return this;
        }
    }
}
