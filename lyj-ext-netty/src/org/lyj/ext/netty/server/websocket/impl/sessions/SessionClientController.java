package org.lyj.ext.netty.server.websocket.impl.sessions;

import io.netty.channel.Channel;
import org.lyj.commons.Delegates;
import org.lyj.ext.netty.server.web.HttpServerConfig;

import java.util.*;

/**
 * Singleton Session Controller.
 * Use this class to access all client sessions
 */
public class SessionClientController {


    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Map<String, SessionClient> _clients;
    private final List<Delegates.CallbackEntry<String, Object>> _listeners;

    private HttpServerConfig _config;


    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    private SessionClientController() {
        _clients = new HashMap<>();
        _listeners = new ArrayList<>();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void config(final HttpServerConfig config) {
        _config = config;
    }

    public void listener(final Delegates.CallbackEntry<String, Object> callback) {
        if (!_listeners.contains(callback)) {
            _listeners.add(callback);
        }
    }

    // --------------------------------------------------------------------
    //               s e s s i o n s
    // --------------------------------------------------------------------

    public int count() {
        return _clients.size();
    }

    public Set<String> keys() {
        return _clients.keySet();
    }

    public boolean exists(final String uid) {
        synchronized (_clients) {
            return _clients.containsKey(uid);
        }
    }

    public String addressOf(final String uid) {
        synchronized (_clients) {
            if (_clients.containsKey(uid)) {
                return _clients.get(uid).address();
            }
            return "";
        }
    }

    public SessionClient open(final String uid) {
        synchronized (_clients) {
            if (!_clients.containsKey(uid)) {
                _clients.put(uid, new SessionClient(uid, _config, _listeners));
            }
            return _clients.get(uid);
        }
    }

    public void close() {
        synchronized (_clients) {

            // remove listeners
            _listeners.clear();

            // close all channels
            _clients.forEach((uid, client) -> {
                try {
                    client.close();
                } catch (Throwable ignored) {
                }
            });
            _clients.clear();

        }
    }

    public SessionClient close(final String uid) {
        synchronized (_clients) {
            final SessionClient client = _clients.remove(uid);
            if (null != client) {
                try {
                    client.close();
                } catch (Throwable ignored) {
                }
            }
            return client;
        }
    }

    public SessionClient close(final String uid,
                               final Channel channel) {
        synchronized (_clients) {
            final SessionClient client = _clients.remove(uid);
            if (null != client) {
                try {
                    client.remove(channel);
                } catch (Throwable ignored) {
                }
            }
            return client;
        }
    }


    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static SessionClientController __instance;

    public static SessionClientController instance() {
        if (null == __instance) {
            __instance = new SessionClientController();
        }
        return __instance;
    }


}
