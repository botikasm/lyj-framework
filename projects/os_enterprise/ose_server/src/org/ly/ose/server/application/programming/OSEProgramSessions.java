package org.ly.ose.server.application.programming;

import org.ly.ose.server.IConstants;
import org.lyj.commons.async.future.Loop;
import org.lyj.commons.io.cache.memorycache.MemoryCache;
import org.lyj.commons.io.cache.memorycache.MemoryCacheItem;

import java.util.Set;

/**
 * SessionManager for OSE Programs.
 * All programs with a session ID are stored into this memory cache
 */
public class OSEProgramSessions {

    // ------------------------------------------------------------------------
    //                     c o n s t
    // ------------------------------------------------------------------------

    private static final int SESSION_MONITOR_MS = 2 * 1000;
    private static final long SESSION_TIMEOUT_MS = IConstants.SESSION_TIMEOUT_MS;

    // ------------------------------------------------------------------------
    //                     f i e l d s
    // ------------------------------------------------------------------------

    private final MemoryCache<OSEProgram> _cache;
    private final Loop _loop;

    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private OSEProgramSessions() {
        _cache = new MemoryCache<>();
        _loop = new Loop(SESSION_MONITOR_MS);
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    public void start() {
        this.clear();
        _loop.start(this::onCicle);
    }

    public void stop() {
        _loop.interrupt();
        this.clear();
    }

    public boolean containsKey(final String key) {
        synchronized (_cache) {
            return _cache.containsKey(key);
        }
    }

    public void put(final String key,
                    final OSEProgram program) throws Exception {
        this.put(key, program, SESSION_TIMEOUT_MS);
    }

    public void put(final String key,
                    final OSEProgram program,
                    final long duration_ms) throws Exception {
        synchronized (_cache) {
            if (!_cache.containsKey(key)) {
                // init program
                this.sessionInitialize(program);
                // save to cache
                final MemoryCacheItem<OSEProgram> item = new MemoryCacheItem<>();
                item.item(program);
                item.duration(duration_ms > -1 ? duration_ms : program.info().sessionTimeout());
                _cache.put(key, item);
            } else {
                _cache.wakeUp(key);
            }
        }
    }

    public OSEProgram get(final String key) {
        synchronized (_cache) {
            if (_cache.containsKey(key)) {
                final MemoryCacheItem<OSEProgram> item = _cache.get(key);
                item.wakeUp();
                return item.item();
            }
            return null;
        }
    }

    public OSEProgram remove(final String key) {
        synchronized (_cache) {
            if (_cache.containsKey(key)) {
                final MemoryCacheItem<OSEProgram> item = _cache.remove(key);
                return item.item();
            }
            return null;
        }
    }


    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private void clear() {
        synchronized (_cache) {
            _cache.clear();
        }
    }

    private void sessionInitialize(final OSEProgram program) throws Exception {
        if (null != program) {
            final Object init_response = program.open();
            // TODO: do I need to do something with this response?
        }
    }


    private void sessionExpired(final OSEProgram program) throws Exception {
        if (null != program) {
            final Object expire_response = program.onExpire();
            program.close();
            // TODO: do I need to do something with this response?
        }
    }

    /**
     * Check if program session is expired or is still valid.
     */
    private void onCicle(final Loop.LoopInterruptor interruptor) {
        synchronized (_cache) {
            try {
                final Set<String> keys = _cache.keys();
                for (final String key : keys) {
                    if (_cache.isExpired(key)) {
                        this.sessionExpired(_cache.remove(key).item());
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

    private static OSEProgramSessions __instance;

    public static synchronized OSEProgramSessions instance() {
        if (null == __instance) {
            __instance = new OSEProgramSessions();
        }
        return __instance;
    }


}
