package org.lyj.ext.netty.server.websocket.impl.sessions;

import io.netty.channel.Channel;
import org.lyj.commons.Delegates;
import org.lyj.ext.netty.server.web.HttpServerConfig;

import java.util.*;

/**
 * Singleton Session Controller
 */
public class SessionController {


    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Map<String, SessionClient> _clients;
    private final List<Delegates.Callback<String>> _listeners;

    private HttpServerConfig _config;


    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    private SessionController() {
        _clients = new HashMap<>();
        _listeners = new ArrayList<>();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void config(final HttpServerConfig config) {
        _config = config;
    }

    public void listener(final Delegates.Callback<String> callback) {
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

    public SessionClient open(final String uid) {
        if (!_clients.containsKey(uid)) {
            _clients.put(uid, new SessionClient(uid, _config, _listeners));
        }
        return _clients.get(uid);
    }

    public SessionClient close(final String uid, final Channel channel) {
        final SessionClient client = _clients.remove(uid);
        if (null != client) {
            client.remove(channel);
        }
        return client;
    }


    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static SessionController __instance;

    public static SessionController instance() {
        if (null == __instance) {
            __instance = new SessionController();
        }
        return __instance;
    }
}
