package org.lyj.commons.io.chunks;

import java.util.*;

public class FileChunks {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Set<ChunkInfo> _chunks;

    private final String _uid;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileChunks(final String uid) {
        _chunks = new HashSet<>();
        _uid = uid;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uid() {
        return _uid;
    }

    public int count() {
        return _chunks.size();
    }

    public void clear() {
        _chunks.clear();
    }

    public ChunkInfo add(final String cache_key,
                         final int index,
                         final int total) {
        final ChunkInfo info = new ChunkInfo(cache_key, index, total);
        if (!_chunks.contains(info)) {
            _chunks.add(info);
        }
        return info;
    }

    public boolean contains(final String cache_key) {
        final ChunkInfo info = new ChunkInfo(cache_key);
        return _chunks.contains(info);
    }

    public ChunkInfo remove(final String cache_key) {
        final ChunkInfo info = new ChunkInfo(cache_key);
        if (_chunks.remove(info)) {
            return info;
        }
        return null;
    }

    public ChunkInfo[] sort() {
        final List<ChunkInfo> list = new ArrayList<>(_chunks);
        list.sort(Comparator.comparingInt(ChunkInfo::index));
        return list.toArray(new ChunkInfo[0]);
    }

    public ChunkInfo[] data() {
        return _chunks.toArray(new ChunkInfo[0]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      N E S T E D
    // ------------------------------------------------------------------------

    public class ChunkInfo {

        private final String _cache_key;
        private int _index;
        private int _total;

        public ChunkInfo(final String cache_key,
                         final int index,
                         final int total) {
            _cache_key = cache_key;
            _index = index;
            _total = total;
        }

        public ChunkInfo(final String cache_key) {
            _cache_key = cache_key;
            _index = 0;
            _total = 0;
        }

        @Override
        public int hashCode() {
            final int hash = 31 * _cache_key.hashCode();
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            return (this.hashCode() == obj.hashCode());
        }

        public String cacheKey() {
            return _cache_key;
        }

        public int index() {
            return _index;
        }

        public ChunkInfo index(final int value) {
            _index = value;
            return this;
        }

        public int total() {
            return _total;
        }

        public ChunkInfo total(final int value) {
            _total = value;
            return this;
        }

    }


}
