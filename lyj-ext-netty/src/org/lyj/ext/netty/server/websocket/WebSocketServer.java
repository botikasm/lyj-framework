package org.lyj.ext.netty.server.websocket;

import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.websocket.impl.sessions.SessionController;

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
        SessionController.instance().config(super.config());
    }


}
