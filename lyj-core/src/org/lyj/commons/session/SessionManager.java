package org.lyj.commons.session;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Set;
import java.util.TreeMap;

/**
 * Manage and store session like Treehasmap
 */
public final class SessionManager
        extends AbstractLogEmitter
        implements ISessionMonitor {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final TreeMap<String, Session> _sessions;

    private int _session_idle_timeout;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SessionManager() {
        _sessions = new TreeMap<>();

        _session_idle_timeout = 60000; // 1 minute session
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public SessionManager idleTimeOut(final int value) {
        _session_idle_timeout = value;
        return this;
    }

    public int idleTimeOut() {
        return _session_idle_timeout;
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

    /**
     * Returns a fake session to use just as a JSON item.
     *
     * @return fake session.
     */
    public Session getFake() {
        return new Session();
    }

    public Session get() {
        return this.get(null);
    }

    public Session get(final String opt_key) {
        synchronized (_sessions) {
            if (StringUtils.hasText(opt_key)) {
                if (_sessions.containsKey(opt_key)) {
                    final Session session = _sessions.get(opt_key);
                    if (null != session) {
                        if (!session.closed()) {
                            session.wakeUp();
                            return session;
                        }
                    }
                    return this.create(opt_key);
                } else {
                    return this.create(opt_key);
                }
            } else {
                return this.create(null);
            }
        }
    }

    public Session remove(final String key) {
        synchronized (_sessions) {
            if (StringUtils.hasText(key)) {
                return _sessions.remove(key);
            }
            return null;
        }
    }

    public SessionManager clear() {
        synchronized (_sessions) {
            // close all sessions
            for (final Session session : _sessions.values()) {
                session.close();
            }
            // clear list and free memory
            _sessions.clear();
            return this;
        }
    }

    // ------------------------------------------------------------------------
    //                      ISessionMonitor
    // ------------------------------------------------------------------------

    @Override
    public void notifySessionExpired(final String session_id) {
        // this session has notified is expired
        if (StringUtils.hasText(session_id)) {
            this.remove(session_id);
        } else {
            super.error("notifySessionExpired", "Session has empty id");
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Session create(final String opt_key) {
        synchronized (_sessions) {
            final String key = StringUtils.hasText(opt_key) ? opt_key : RandomUtils.randomUUID();
            final Session item = new Session(key, _session_idle_timeout, this);
            _sessions.put(key, item);
            return item;
        }
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static SessionManager __instance;

    public static synchronized SessionManager instance() {
        if (null == __instance) {
            __instance = new SessionManager();
        }
        return __instance;
    }

}
