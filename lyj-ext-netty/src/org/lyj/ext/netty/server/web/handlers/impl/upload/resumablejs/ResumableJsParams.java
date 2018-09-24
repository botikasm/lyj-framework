package org.lyj.ext.netty.server.web.handlers.impl.upload.resumablejs;

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.ext.netty.server.web.HttpServerRequest;

import java.util.Collection;
import java.util.Map;

//https://github.com/23/resumable.js
public class ResumableJsParams {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_CHUNK_NUMBER = "resumableChunkNumber";
    private static final String FLD_TOTAL_CHUNKS = "resumableTotalChunks";
    private static final String FLD_CHUNK_SIZE = "resumableChunkSize";
    private static final String FLD_TOTAL_SIZE = "resumableTotalSize";
    private static final String FLD_IDENTIFIER = "resumableIdentifier";
    private static final String FLD_FILENAME = "resumableFilename";
    private static final String FLD_RELATIVE_PATH = "resumableRelativePath";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private int _chunkNumber;
    private int _totalChunks;
    private int _chunkSize;
    private int _totalSize;
    private String _identifier;
    private String _filename;
    private String _relative_path;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    ResumableJsParams(final HttpServerRequest request) {

        this.init(request);

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isValid() {
        return _totalChunks > 0;
    }

    public int chunkNumber() {
        return _chunkNumber;
    }

    public int totalChunks() {
        return _totalChunks;
    }

    public int chunkSize() {
        return _chunkSize;
    }

    public int totalSize() {
        return _totalSize;
    }

    public String identifier() {
        return _identifier;
    }

    public String filename() {
        return _filename;
    }

    public String relativePath() {
        return _relative_path;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final HttpServerRequest request) {
        if (isResumableRequest(request)) {
            final Map<String, Object> params = request.params();

            _chunkNumber = this.getInt(params, FLD_CHUNK_NUMBER);
            _totalChunks = this.getInt(params, FLD_TOTAL_CHUNKS);
            _chunkSize = this.getInt(params, FLD_CHUNK_SIZE);
            _totalSize = this.getInt(params, FLD_TOTAL_SIZE);
            _identifier = this.getString(params, FLD_IDENTIFIER);
            _filename = this.getString(params, FLD_FILENAME);
            _relative_path = this.getString(params, FLD_RELATIVE_PATH);

        }
    }

    private String getString(final Map<String, Object> params, final String key) {
        if (params.containsKey(key)) {
            final Object value = params.get(key);
            if (null != value) {
                if (value.getClass().isArray()) {
                    return CollectionUtils.get((String[]) value, 0);
                } else if (value instanceof Collection) {
                    return CollectionUtils.get((Collection) value, 0, "");
                } else {
                    return value.toString();
                }
            }
        }
        return "";
    }

    private int getInt(final Map<String, Object> params, final String key) {
        return ConversionUtils.toInteger(this.getString(params, key));
    }
    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


    public static boolean isResumableRequest(final HttpServerRequest request) {
        try {
            final Map<String, Object> params = request.params();
            return params.containsKey("resumableTotalChunks");
        } catch (Throwable ignored) {
            // ignored
        }
        return false;
    }


}
