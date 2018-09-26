package org.lyj.ext.netty.server.web.handlers.impl.upload.resumablejs;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.lyj.commons.async.Locker;
import org.lyj.commons.io.chunks.FileChunksController;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.handlers.impl.upload.UploadFileInfo;

import java.io.File;

/**
 * Resumable.js controller.
 * This controller handle FileChunkController
 */
public class ResumableJs {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final FileChunksController _chunks;
    private final ResumableJsParams _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ResumableJs(final HttpServerRequest request) {
        _chunks = FileChunksController.instance();
        _params = new ResumableJsParams(request);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isValid() {
        return _params.isValid();
    }

    public int totalChunks() {
        return _params.totalChunks();
    }

    public HttpResponseStatus status() {
        this.lock();
        try {
            final String uid = _params.identifier();
            final int index = _params.chunkNumber();
            if (_chunks.has(uid, index)) {
                return HttpResponseStatus.OK;     // 200
            }
            return HttpResponseStatus.NO_CONTENT; // 204
        } finally {
            this.unlock();
        }
    }

    public boolean isComplete() {
        this.lock();
        try {
            final String uid = _params.identifier();

            final int total_chunks = _params.totalChunks();
            final int curr_chunks = _chunks.count(uid);

            return curr_chunks == total_chunks;
        } finally {
            this.unlock();
        }
    }

    public boolean store(final UploadFileInfo file_info) {
        this.lock();
        try {
            final String temp_file = file_info.localeAbsolute();
            try {
                final String uid = _params.identifier();
                final int index = _params.chunkNumber();
                final int total = _params.totalChunks();
                final byte[] bytes = FileUtils.copyToByteArray(new File(temp_file));

                _chunks.add(uid, index, total, bytes);

                // System.out.println("STORE: " + index + "/" + total + " ("+_chunks.count(uid)+")");

                return true;
            } catch (Throwable t) {
                // error reading file in temp
            } finally {
                FileUtils.tryDelete(temp_file);
            }
            return false;
        } finally {
            this.unlock();
        }
    }

    public boolean compose(final UploadFileInfo file_info) {
        this.lock();
        try {
            try {
                final String uid = _params.identifier();
                final String relative_path = _params.relativePath();
                final String full_path = PathUtils.getTemporaryFile(relative_path);

                _chunks.compose(uid, full_path);

                file_info.localeAbsolute(full_path);
                file_info.localeRelative(relative_path);
            } catch (Throwable ignored) {
                return false;
            }
            return true;
        } finally {
            this.unlock();
        }
    }

    public boolean remove(final UploadFileInfo file_info) {
        this.lock();
        try {
            try {
                final String uid = _params.identifier();
                _chunks.remove(uid);
            } catch (Throwable ignored) {
                return false;
            }
            return true;
        } finally {
            this.unlock();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void lock() {
        Locker.instance().lock(_params.identifier());
    }

    private void unlock() {
        Locker.instance().unlock(_params.identifier());
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static boolean isValid(final HttpServerRequest request) {
        try {
            return ResumableJsParams.isResumableRequest(request);
        } catch (Throwable ignored) {
            // ignored
        }
        return false;
    }

}
