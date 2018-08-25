package org.ly.ose.server.application.programming;

import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.application.programming.exceptions.ImproperUseException;
import org.ly.ose.server.application.programming.exceptions.InfiniteLoopException;
import org.lyj.commons.async.future.Loop;
import org.lyj.commons.io.cache.memorycache.MemoryCache;
import org.lyj.commons.io.cache.memorycache.MemoryCacheItem;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Set;

public class OSEProgramInvokerMonitor {

    // ------------------------------------------------------------------------
    //                     c o n s t
    // ------------------------------------------------------------------------

    private static final int MONITOR_MS = 1 * 1000;

    private static final String FLD_EXEC_TIMESTAMP = "_exec_timestamp";
    private static final String FLD_EXEC_COUNT = "_exec_count";
    private static final String FLD_EXEC_KEY = "_exec_key";

    // ------------------------------------------------------------------------
    //                     f i e l d s
    // ------------------------------------------------------------------------

    private final MemoryCache<OSERequest> _cache;
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

    /**
     * Validate a request and trace execution to avoid infinite recursive loops that
     * should crash the system
     *
     * @param request Request to check for infinite loop
     * @throws Exception Infinite loop error....
     */
    public void validate(final OSERequest request) throws Exception {
        synchronized (_cache) {
            final String client_id = request.clientId(); // each request has a client with a unique id
            if (!_cache.containsKey(client_id)) {
                // add a copy of this request to cache
                final MemoryCacheItem<OSERequest> item = new MemoryCacheItem<>();
                item.item(clone(request));
                item.duration(MONITOR_MS);
                _cache.put(client_id, item);
            } else {
                final OSERequest cached = _cache.get(client_id).item();
                this.checkForInfiniteLoop(request, cached);
                this.checkForImproperUse(request, cached);
                _cache.put(client_id, cached, false);
            }
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

    private OSERequest clone(final OSERequest request) {
        final OSERequest clone = new OSERequest(request.toString());
        clone.put(FLD_EXEC_TIMESTAMP, DateUtils.timestamp());
        clone.put(FLD_EXEC_KEY, request.key());
        clone.put(FLD_EXEC_COUNT, 1);
        return clone;
    }

    private void checkForInfiniteLoop(final OSERequest original,
                                      final OSERequest cached) throws InfiniteLoopException {
        if (null != original && null != cached) {

            final int count = cached.getInt(FLD_EXEC_COUNT);
            final long timestamp = cached.getLong(FLD_EXEC_TIMESTAMP);
            final String last_key = cached.getString(FLD_EXEC_KEY);
            final boolean equals = original.equals(cached) || original.key().equals(last_key);

            // update cached
            cached.put(FLD_EXEC_COUNT, count + 1);
            cached.put(FLD_EXEC_KEY, original.key());
            cached.put(FLD_EXEC_TIMESTAMP, DateUtils.timestamp());

            if (equals) {
                // same request
                if (count > 100) {
                    throw new InfiniteLoopException();
                }
            } else {
                // same client id

            }
        }
    }

    private void checkForImproperUse(final OSERequest original,
                                     final OSERequest cached) throws ImproperUseException {
        final int count = cached.getInt(FLD_EXEC_COUNT);
        final long timestamp = cached.getLong(FLD_EXEC_TIMESTAMP);
        final String last_key = cached.getString(FLD_EXEC_KEY);
        final boolean equals = original.equals(cached) || original.key().equals(last_key);

        if (!original.hasPayload()) {
            throw new ImproperUseException();
        } else {
            final OSEPayloadProgram payload = new OSEPayloadProgram(original.payload());
            final String app_token = payload.appToken();
            if(!StringUtils.hasText(app_token)){
                throw new ImproperUseException();
            } else {
                
            }
        }
    }

    /**
     * Cache thread callback
     */
    private void onCicle(final Loop.LoopInterruptor interruptor) {
        synchronized (_cache) {
            try {
                final Set<String> keys = _cache.keys();
                for (final String key : keys) {
                    if (_cache.isExpired(key)) {
                        //this.sessionExpired(_cache.remove(key).item());
                        _cache.remove(key); // clear cache
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
