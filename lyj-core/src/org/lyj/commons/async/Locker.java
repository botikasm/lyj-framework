package org.lyj.commons.async;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Lock/Unlock a key (string)
 * Useful to lock access to a file or to something we need is accessed only from a single thread.
 */
public class Locker {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Set<String> _locks;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private Locker() {
        _locks = Collections.synchronizedSet(new HashSet<>());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean locked(final String key) {
        return _locks.contains(key);
    }

    public void lock(final String key) {
        this.await(key);
        this.add(key);
    }

    public void unlock(final String key) {
        this.remove(key);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private synchronized void await(final String key) {
        try {
            while (_locks.contains(key)) {
                Thread.sleep(100);
            }
        } catch (Throwable t) {

        }
    }

    private void add(final String key) {
        synchronized (_locks) {
            if (!_locks.contains(key)) {
                _locks.add(key);
            } else {

            }
        }
    }

    private void remove(final String key) {
        synchronized (_locks) {
            if (_locks.contains(key)) {
                _locks.remove(key);
            }
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
