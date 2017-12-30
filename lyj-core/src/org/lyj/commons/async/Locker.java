package org.lyj.commons.async;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Lock/Unlock a key (string)
 * Useful to lock access to a file or to something we need is accessed only from a single thread.
 */
public class Locker {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Object> _locks;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private Locker() {
        _locks = Collections.synchronizedMap(new HashMap<>());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isLocked(final String key) {
        return _locks.containsKey(key);
    }

    public Object getLocked(final String key) {
        return _locks.get(key);
    }

    public void lock(final String key) {
        this.lock(key, key);
    }

    public void lock(final String key, final Object obj) {
        this.await(key);
        this.add(key, obj);
    }

    public Object unlock(final String key) {
        return this.remove(key);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void await(final String key) {
        try {
            while (_locks.containsKey(key)) {
                Thread.sleep(100);
            }
        } catch (Throwable t) {

        }
    }

    private void add(final String key, final Object obj) {
        synchronized (_locks) {
            if (!_locks.containsKey(key)) {
                _locks.put(key, obj);
            }
        }
    }

    private Object remove(final String key) {
        synchronized (_locks) {
            if (_locks.containsKey(key)) {
                return _locks.remove(key);
            }
            return null;
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static Locker __instance;

    public static synchronized Locker instance() {
        if (null == __instance) {
            __instance = new Locker();
        }
        return __instance;
    }

}
