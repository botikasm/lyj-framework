package org.lyj.commons.io.memorycache;

import java.util.TreeMap;

public class MemoryCache {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final TreeMap<String, MemoryCacheItem> _cache;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MemoryCache() {
        _cache = new TreeMap<>();
    }

    @Override
    public String toString() {
        return _cache.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int size() {
        return _cache.size();
    }

    public void clear() {
        synchronized (_cache) {
            _cache.clear();
        }
    }

    public MemoryCacheItem put(final String key,
                               final Object item) {
        synchronized (_cache) {
            if (!_cache.containsKey(key)) {
                // insert
                if (item instanceof MemoryCacheItem) {
                    _cache.put(key, (MemoryCacheItem) item);
                } else {
                    _cache.put(key, new MemoryCacheItem().item(item));
                }

            } else {
                // update
                _cache.get(key).item(item);
            }
            return _cache.get(key);
        }
    }

    public MemoryCacheItem remove(final String key) {
        synchronized (_cache) {
            return _cache.remove(key);
        }
    }

    public MemoryCacheItem get(final String key) {
        synchronized (_cache) {
            return _cache.get(key);
        }
    }

    public boolean containsKey(final String key) {
        synchronized (_cache) {
            return _cache.containsKey(key);
        }
    }

    public boolean isExpired(final String key) {
        synchronized (_cache) {
            return !_cache.containsKey(key) || _cache.get(key).expired();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static MemoryCache __instance;

    public static synchronized MemoryCache singleton() {
        if (null == __instance) {
            __instance = new MemoryCache();
        }
        return __instance;
    }

}
