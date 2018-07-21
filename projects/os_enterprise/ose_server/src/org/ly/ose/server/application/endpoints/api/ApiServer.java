package org.ly.ose.server.application.endpoints.api;

import org.ly.ose.server.application.endpoints.api.routing.RouterSys;
import org.ly.ose.server.deploy.config.ConfigHelper;
import org.lyj.ext.netty.server.web.HttpServer;

/**
 *
 */
public class ApiServer
        extends HttpServer {

    private static final boolean USE_SSL = ConfigHelper.instance().apiUseSSL();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ApiServer() {
        final int port = ConfigHelper.instance().apiPort();
        super.config().port(port).portAutodetect(false).corsAllowOrigin("*")
                .useSsl(USE_SSL).sslPassKey("");

        // #1 - add router as first handler
        super.handler(RouterSys.create(super.config()));

        //super.handler(RouterHooks.create(super.config()));

        // #2 - add basic http resource server (serve text and images)
        //super.handler(ResourceHandler.create(this.config()));

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public ApiServer start() {
        super.start();
        super.logger().info("Api Server Started.");
        return this;
    }

    @Override
    public ApiServer stop() {
        super.stop();
        super.logger().info("Api Server stopped.");
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
