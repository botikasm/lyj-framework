package org.ly.ose.server.application.programming;

import org.lyj.commons.async.future.Loop;
import org.lyj.commons.io.cache.memorycache.MemoryCache;

import java.util.Set;

public class OSEProgramInvokerMonitor {

    // ------------------------------------------------------------------------
    //                     c o n s t
    // ------------------------------------------------------------------------

    private static final int MONITOR_MS = 2 * 1000;

    // ------------------------------------------------------------------------
    //                     f i e l d s
    // ------------------------------------------------------------------------

    private final MemoryCache<OSEProgram> _cache;
    private final Loop _loop;

    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private OSEProgramInvokerMonitor() {
        _cache = new MemoryCache<>();
        _loop = new Loop(MONITOR_MS);
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        this.clear();
        _loop.start(this::onCicle);
    }

    public void close() {
        _loop.interrupt();
        this.clear();
    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private void clear() {
        synchronized (_cache) {
            _cache.clear();
        }
    }

    private void onCicle(final Loop.LoopInterruptor interruptor) {
        synchronized (_cache) {
            try {
                final Set<String> keys = _cache.keys();
                for (final String key : keys) {
                    if (_cache.isExpired(key)) {
                        //this.sessionExpired(_cache.remove(key).item());
                    }
                }
            } catch (Throwable ignored) {
                // ignored
            }
        }
    }


    // ------------------------------------------------------------------------
    //                     S I N G L E T O N
    // ------------------------------------------------------------------------

    private static OSEProgramInvokerMonitor __instance;

    public static synchronized OSEProgramInvokerMonitor instance() {
        if (null == __instance) {
            __instance = new OSEProgramInvokerMonitor();
        }
        return __instance;
    }

}
