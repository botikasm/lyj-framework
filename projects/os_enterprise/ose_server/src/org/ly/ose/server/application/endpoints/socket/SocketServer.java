package org.ly.ose.server.application.endpoints.socket;

import org.ly.ose.server.deploy.config.ConfigHelper;
import org.lyj.commons.async.Async;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.websocket.WebSocketServer;
import org.lyj.ext.netty.server.websocket.impl.sessions.SessionClientController;

/**
 *
 */
public class SocketServer
        extends WebSocketServer {

    // ------------------------------------------------------------------------
    //                      c o n s t 
    // ------------------------------------------------------------------------

    private static final boolean USE_SSL = ConfigHelper.instance().socketUseSSL();

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketServer() {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public SocketServer start() {
        super.start();
        super.logger().info("Socket Server Started on port: " + super.config().port());
        return this;
    }

    @Override
    public SocketServer stop() {
        super.stop();
        super.logger().info("Socket Server stopped.");
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // init configuration
        final int port = ConfigHelper.instance().socketPort();
        super.config().port(port).portAutodetect(false).websocketPath("/websocket")
                //.corsAllowOrigin("*")
                .useSsl(USE_SSL).sslPassKey("");

        // init socket listener
        SessionClientController.instance().listener(this::onSocketRequest);
    }

    private void onSocketRequest(final String session_id,
                                 final Object data) {
        if (StringUtils.hasText(session_id) && null != data) {

            // handle socket request
            Async.invoke((args) -> {
                SocketController.instance().notifyRequest((String) args[0], args[1]);
            }, session_id, data);

        }
    }

}
