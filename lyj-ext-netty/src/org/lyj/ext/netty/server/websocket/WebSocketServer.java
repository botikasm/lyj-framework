package org.lyj.ext.netty.server.websocket;

import org.lyj.commons.Delegates;
import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.websocket.impl.sessions.SessionClientController;

/**
 * Websocket server
 */
public class WebSocketServer
        extends HttpServer {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebSocketServer() {
        // remove webserver initializer
        super.channels().clear();

        // add websocket initializer
        super.channels().add(new WebSocketServerInitializer(this));

        // set configuration to Session manager
        SessionClientController.instance().config(super.config());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    @Override
    public HttpServer start() {
        return super.start();
    }

    @Override
    public HttpServer stop() {
        try {
            SessionClientController.instance().close();
        } catch (Throwable ignored) {
        }
        return super.stop();
    }

    public void listener(final Delegates.CallbackEntry<String, Object> callback) {
        SessionClientController.instance().listener(callback);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------



}
