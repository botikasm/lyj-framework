package org.lyj.ext.netty.server.web.handlers.impl.upload;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.*;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpParams;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerContext;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.handlers.impl.upload.resumablejs.ResumableJs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadReader
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerConfig _config;
    private final String _root;

    private final HttpDataFactory _factory; // Disk if size exceed
    private HttpPostRequestDecoder _decoder;
    private boolean _reading_chunks;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public UploadReader(final HttpServerConfig config) {
        _config = config;
        _root = PathUtils.getAbsolutePath(_config.uploadDir());

        _factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public UploadFileInfo read(final HttpServerContext context) throws Exception {
        final UploadFileInfo response = this.readFile(context);
        this.close();
        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void close() {
        this.resetPostRequestDecoder(null);
    }

    private HttpServerConfig config() {
        return _config;
    }

    private UploadFileInfo readFile(final HttpServerContext context) throws Exception {
        try {
            final HttpServerRequest request = context.request();

            // initialize decoder
            this.initRequestDecoder(request);

            // get file_info. Never null.
            // Status:
            //  HttpResponseStatus.CREATED:     not initialized
            //  HttpResponseStatus.OK:          file is found
            //  HttpResponseStatus.NO_CONTENT:  content not found
            final UploadFileInfo file_info = readFileInfo(request, context.params());

            final ResumableJs resumable = new ResumableJs(request);
            if (resumable.isValid() && resumable.totalChunks() > 1) {

                // resumable must store data into FileChunkController
                if (resumable.store(file_info)) {
                    if (resumable.isComplete()) {

                        // all chunks are ready
                        if (resumable.compose(file_info)) {
                            // ready
                            file_info.status(HttpResponseStatus.OK);
                        } else {
                            // next
                            file_info.status(HttpResponseStatus.NOT_FOUND);  // cannot compose file
                        }

                    } else {
                        file_info.status(HttpResponseStatus.CONTINUE); // next chunk
                    }
                } else {
                    file_info.status(HttpResponseStatus.NO_CONTENT); // retry
                }
            } else {
                file_info.status(HttpResponseStatus.OK);
            }
            // no resumable response
            return file_info;
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

        if (null != _decoder) {
            try {
                _decoder.cleanFiles();
            } catch (Throwable ignored) {
                // ignored
            }
            try {
                _decoder.destroy();
            } catch (Throwable ignored) {
                // ignored
            }
        }
        _decoder = null;

        if (null != chunk) {
            try {
                chunk.content().release();
            } catch (Throwable ignored) {
                // ignored
            }
        }
    }

    private UploadFileInfo readFileInfo(final HttpServerRequest request,
                                        final HttpParams http_params) {
        UploadFileInfo file_info = null;

        // read content
        if (request.isHttpContent()) {
            if (null != _decoder) {
                // New chunk is received
                HttpContent chunk = request.nativeHttpContent();
                _decoder.offer(chunk);

                file_info = readHttpDataChunkByChunk(http_params); // HttpResponseStatus.CREATED by default

                // example of reading only if at the end
                if (chunk instanceof LastHttpContent) {
                    //writeResponse(ctx.channel());
                    _reading_chunks = false;

                    resetPostRequestDecoder(chunk);

                    file_info.status(HttpResponseStatus.OK);
                }
            }
        }

        return null != file_info ? file_info : new UploadFileInfo();
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
