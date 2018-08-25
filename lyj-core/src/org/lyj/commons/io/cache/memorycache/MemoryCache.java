package org.lyj.commons.io.cache.memorycache;

import java.util.Set;
import java.util.TreeMap;

/**
 * Cache items in memory.
 * To refresh item status just put the item again in cache with same key, ore invoke wakeUp method.
 * Both (put again or invoking wakeUp) get same result.
 */
public class MemoryCache<T> {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final TreeMap<String, MemoryCacheItem<T>> _cache;

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

    public Set<String> keys() {
        synchronized (_cache) {
            return _cache.keySet();
        }
    }

    public void clear() {
        synchronized (_cache) {
            _cache.clear();
        }
    }

    public MemoryCacheItem<T> put(final String key,
                                  final MemoryCacheItem<T> item) {
        synchronized (_cache) {
            if (!_cache.containsKey(key)) {
                // insert
                _cache.put(key, item);
            } else {
                // update
                _cache.get(key).wakeUp();
            }
            return _cache.get(key);
        }
    }

    public MemoryCacheItem<T> put(final String key,
                                  final T item) {
        return this.put(key, item, true);
    }

    public MemoryCacheItem<T> put(final String key,
                                  final T item,
                                  final boolean wakeup) {
        synchronized (_cache) {
            if (!_cache.containsKey(key)) {
                // insert
                _cache.put(key, new MemoryCacheItem<T>().item(item, wakeup));
            } else {
                // update
                _cache.get(key).item(item);
            }
            return _cache.get(key);
        }
    }

    public MemoryCacheItem<T> remove(final String key) {
        synchronized (_cache) {
            return _cache.remove(key);
        }
    }

    public MemoryCacheItem<T> get(final String key) {
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
            return !_cache.containsKey(key) || (null != _cache.get(key) && _cache.get(key).expired());
        }
    }

    public MemoryCacheItem<T> wakeUp(final String key) {
        synchronized (_cache) {
            if (_cache.containsKey(key)) {
                return _cache.get(key).wakeUp();
            }
            return null;
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
