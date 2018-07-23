package org.ly.server.application.endpoints.web;

import org.ly.server.deploy.config.ConfigHelper;
import org.ly.server.deploy.config.ConfigHelper;
import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.web.handlers.impl.ResourceHandler;

/**
 *
 */
public class WebServer
        extends HttpServer {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final boolean USE_SSL = ConfigHelper.instance().webUseSSL();

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebServer() {
        final int port = ConfigHelper.instance().webPort();
        final String not_found = ConfigHelper.instance().web404();
        super.config().port(port).portAutodetect(false).root(ConfigHelper.instance().webRoot())
                .notFound404(not_found)
                //.corsAllowOrigin("*")
                .useSsl(USE_SSL).sslPassKey("");

        // #1 - add router as first handler
        //super.handler(ApiRouter.create(super.config()));

        // #2 - add basic http resource server (serve text and images)
        super.handler(ResourceHandler.create(super.config()));

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public WebServer start() {
        super.start();
        super.logger().info("Web Server Started.");
        return this;
    }

    @Override
    public WebServer stop() {
        super.stop();
        super.logger().info("Web Server stopped.");
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
