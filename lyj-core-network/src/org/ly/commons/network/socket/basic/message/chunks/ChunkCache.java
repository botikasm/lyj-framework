package org.ly.commons.network.socket.basic.message.chunks;

import org.lyj.commons.io.filecache.CacheFiles;
import org.lyj.commons.util.PathUtils;

import java.io.InputStream;

/**
 * FS cache to store temp chunks
 */
public class ChunkCache
        extends CacheFiles {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String CHUNK_CACHE_ROOT = "./tmp_chunks";
    private static final int DURATION_MS = 60 * 10 * 1000; // 10 minutes

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ChunkCache() {
        this(PathUtils.getAbsolutePath(CHUNK_CACHE_ROOT));
    }

    public ChunkCache(final String root) {
        super(root, DURATION_MS);
        super.pathDetail(4); // yyyy/mm/dd/hh/

        // clear existing
        super.clear();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void open() {
        super.open();
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean isRunning() {
        return super.isRunning();
    }

    @Override
    public void put(final String key,
                    final byte[] content) {
        super.put(key, content);
    }

    @Override
    public void put(final String key,
                    final InputStream content) {
        super.put(key, content);
    }

    @Override
    public boolean remove(final String key) {
        return super.remove(key);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
