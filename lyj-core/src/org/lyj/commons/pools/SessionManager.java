package org.lyj.commons.pools;

import org.json.JSONObject;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Manage and store session like Treehasmap
 */
public final class SessionManager {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final TreeMap<String, Session> _sessions;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private SessionManager() {
        _sessions = new TreeMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public SessionManager clear() {
        _sessions.clear();
        return this;
    }

    public boolean contains(final String key) {
        return _sessions.containsKey(key);
    }

    public int size() {
        return _sessions.size();
    }

    public Set<String> keys() {
        return _sessions.keySet();
    }

    public Session get() {
        return this.get(null);
    }

    public Session get(final String opt_key) {
        if (StringUtils.hasText(opt_key)) {
            if (_sessions.containsKey(opt_key)) {
                return _sessions.get(opt_key);
            } else {
                return this.create(opt_key);
            }
        } else {
            return this.create(null);
        }
    }

    public Session remove(final String key) {
        if (StringUtils.hasText(key)) {
            return _sessions.remove(key);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Session create(final String opt_key) {
        final String key = StringUtils.hasText(opt_key) ? opt_key : RandomUtils.randomUUID();
        final Session item = new Session(key);
        _sessions.put(key, item);
        return item;
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static SessionManager __instance;

    public static SessionManager instance() {
        if (null == __instance) {
            __instance = new SessionManager();
        }
        return __instance;
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class Session {

        private static final String ID = "_session_id";

        private final String _id;
        private final TreeMap<String, Object> _data;

        public Session(final String id) {
            _id = id;
            _data = new TreeMap<>();
            _data.put(ID, id);
        }

        public String toString() {
            return _data.toString();
        }

        public String toJson() {
            return new JSONObject(_data).toString();
        }

        public Session clear() {
            _data.clear();
            return this;
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

        public Object remove(final String key) {
            return _data.remove(key);
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
            data.remove(ID);
            _data.putAll(data);
            return this;
        }

        public Session put(final String key,
                           final Object value) {
            if (null != key && !ID.equals(key)) {
                _data.put(key, value);
            }
            return this;
        }


    }

}
