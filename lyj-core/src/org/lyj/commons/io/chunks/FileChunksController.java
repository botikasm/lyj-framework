package org.lyj.commons.io.chunks;

import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.io.cache.filecache.CacheFiles;
import org.lyj.commons.tokenizers.files.FileTokenizer;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileChunksController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String DEF_ROOT = PathUtils.getTemporaryDirectory("tmp_chunks");
    private static final long CACHE_DURATION_MS = DateUtils.ONE_HOUR_MS;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    private final CacheFiles _cache;

    private final Map<String, FileChunks> _chunks_map;
    private final String _root;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileChunksController() {
        this(DEF_ROOT, CACHE_DURATION_MS);
    }

    public FileChunksController(final String root,
                                final long cache_timeout_ms) {
        _root = PathUtils.getAbsolutePath(root);
        FileUtils.tryMkdirs(_root);

        _chunks_map = Collections.synchronizedMap(new HashMap<>());
        _cache = new CacheFiles(_root, cache_timeout_ms);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public FileChunksController open() {
        if (!_cache.isRunning()) {
            _cache.clear();
            _cache.open();

            this.init();
        }
        return this;
    }

    public void close() {
        if (_cache.isRunning()) {
            _cache.close();
        }
    }

    public void add(final String uid,
                    final int index,
                    final int total,
                    final byte[] data) {
        synchronized (_chunks_map) {
            if (!_chunks_map.containsKey(uid)) {
                _chunks_map.put(uid, new FileChunks(uid));
            }

            // add to cache
            final String cache_key = cacheKey(uid, index);
            _cache.put(cache_key, data);
            // add to chunks
            final FileChunks chunks = _chunks_map.get(uid);
            chunks.add(cache_key, index, total);
        }
    }

    public boolean has(final String uid,
                       final int index) {
        synchronized (_chunks_map) {
            if (!_chunks_map.containsKey(uid)) {
                return false;
            }

            final FileChunks chunks = _chunks_map.get(uid);

            final String cache_key = cacheKey(uid, index);
            return chunks.contains(cache_key);
        }
    }

    public void remove(final String uid) {
        synchronized (_chunks_map) {
            this.remove(_chunks_map.remove(uid));
        }
    }

    public int count(final String uid) {
        synchronized (_chunks_map) {
            if (_chunks_map.containsKey(uid)) {
                return _chunks_map.get(uid).count();
            }
            return 0;
        }
    }

    public void compose(final String uid,
                        final String out_file_name) throws Exception {
        this.compose(uid, new File(out_file_name));
    }

    public void compose(final String uid,
                        final File out) throws Exception {
        synchronized (_chunks_map) {
            if (_chunks_map.containsKey(uid)) {
                this.compose(_chunks_map.get(uid), out);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }

    private void remove(final FileChunks chunks) {
        if (null != chunks) {
            // clear cache
            final FileChunks.ChunkInfo[] data = chunks.data();
            for (final FileChunks.ChunkInfo info : data) {
                final String cache_key = info.cacheKey();
                _cache.remove(cache_key);
            }
            // clear info
            chunks.clear();
        }
    }

    private void compose(final FileChunks chunks,
                         final File out) throws Exception {
        if (null != chunks) {
            final FileChunks.ChunkInfo[] data = chunks.sort();
            for (final FileChunks.ChunkInfo info : data) {
                final String cache_key = info.cacheKey();
                final byte[] bytes = _cache.getBytes(cache_key);
                FileTokenizer.append(bytes, out);
            }
            this.remove(chunks);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static String cacheKey(final String uid, final int index) {
        return MD5.encode(uid + index);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static FileChunksController __instance;

    public static synchronized FileChunksController instance(final String root,
                                                             final long cache_timeout_ms) {
        if (null == __instance) {
            __instance = new FileChunksController(root, cache_timeout_ms);
        }
        return __instance;
    }

    public static synchronized FileChunksController instance() {
        if (null == __instance) {
            __instance = new FileChunksController();
        }
        return __instance;
    }

}
