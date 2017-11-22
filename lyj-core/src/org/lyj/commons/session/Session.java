package org.lyj.commons.session;

import org.json.JSONObject;
import org.lyj.commons.async.future.Loop;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import java.util.*;

/**
 * Session item.
 */
public class Session {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ID = "_session_id";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Set<ISessionMonitor> _monitors;
    private final Loop _loop;
    private final TreeMap<String, Object> _data;

    private int _idle_timeout;
    private long _expiration_time;
    private boolean _closed;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * Fake session constructor.
     * This session has no internal thread and cannot be added to session manager.
     */
    Session() {
        _data = new TreeMap<>();
        _loop = null;
        _monitors = null;

        _expiration_time = System.currentTimeMillis();
        _closed = false;
    }

    public Session(final String id,
                   final int idle_timeout,
                   final ISessionMonitor monitor) {
        _data = new TreeMap<>();
        _data.put(ID, id);
        _monitors = new HashSet<>();
        _idle_timeout = idle_timeout;
        _expiration_time = System.currentTimeMillis() + _idle_timeout;
        _loop = new Loop(idle_timeout);
        _closed = false;

        _monitors.add(monitor);

        // open session
        this.init();
    }

    public String toJson() {
        return new JSONObject(_data).toString();
    }

    @Override
    public String toString() {
        return _data.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String id() {
        return StringUtils.toString(this.get(ID));
    }

    public Session idleTimeout(final int value) {
        _idle_timeout = value;
        _expiration_time = System.currentTimeMillis() + _idle_timeout;
        // update loop
        if (null != _loop) {
            _loop.runInterval(_idle_timeout);
        }
        return this;
    }

    public int idleTimeout() {
        return _idle_timeout;
    }

    public boolean expired() {
        return _expiration_time <= System.currentTimeMillis();
    }

    public boolean closed() {
        return _closed;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void addMonitor(final ISessionMonitor monitor) {
        if (null!=_monitors && null != monitor) {
            _monitors.add(monitor);
        }
    }

    public void close() {
        _closed = true;
    }

    public int size() {
        return _data.size();
    }

    public Set<String> keys() {
        return _data.keySet();
    }

    public Collection<Object> values() {
        return _data.values();
    }

    public boolean has(final String key) {
        return _data.containsKey(key);
    }

    public Object get(final String key) {
        return _data.get(key);
    }

    public Session put(final String json) {
        if (StringUtils.isJSONObject(json)) {
            return this.put(new JSONObject(json));
        }
        return this;
    }

    public Session put(final JSONObject data) {
        return this.put(JsonWrapper.toMap(data));
    }

    public Session put(final Map<String, Object> data) {
        synchronized (_data) {
            data.remove(ID);
            _data.putAll(data);
            this.wakeUp();
            return this;
        }
    }

    public Session put(final String key,
                       final Object value) {
        synchronized (_data) {
            if (!_closed && null != key && !ID.equals(key)) {
                _data.put(key, value);
                this.wakeUp();
            }
            return this;
        }
    }

    public Object remove(final String key) {
        synchronized (_data) {
            this.wakeUp();
            return _data.remove(key);
        }
    }

    public Session clear() {
        synchronized (_data) {
            _data.clear();
            return this;
        }
    }

    public void wakeUp() {
        _expiration_time = System.currentTimeMillis() + _idle_timeout;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (null != _loop) {
            _loop.priority(Loop.MIN_PRIORITY);
            _loop.start((interruptor) -> {
                try {
                    if (_closed) {
                        interruptor.stop();
                        return;
                    }
                    if (expired() && null != _monitors) {
                        _closed = true;

                        final String id = id();
                        if (StringUtils.hasText(id)) {
                            // notify session if expired
                            for (final ISessionMonitor monitor : _monitors) {
                                monitor.notifySessionExpired(id);
                            }
                        }

                        // stop thread
                        interruptor.stop();
                    }
                } catch (Throwable ignored) {
                    _closed = true;
                }
            });
        }
    }


}
