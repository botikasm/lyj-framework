package org.lyj.desktopgap.app.server;

import org.lyj.Lyj;
import org.lyj.desktopgap.app.server.api.ApiRouter;
import org.lyj.desktopgap.deploy.htdocs.HtdocsDeployer;
import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.web.controllers.routing.IRouter;
import org.lyj.ext.netty.server.web.handlers.impl.ResourceHandler;
import org.lyj.ext.netty.server.web.handlers.impl.RoutingHandler;

/**
 *
 */
public class WebServer
        extends HttpServer {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final RoutingHandler _router; // exposed router

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebServer() {
        super.config().port(4000).portAutodetect(true).root(Lyj.getAbsolutePath(HtdocsDeployer.PATH));

        _router = RoutingHandler.create(super.config());

        // #1 - add router as first handler
        super.handler(ApiRouter.create(super.config()));
        super.handler(_router);

        // #2 - add basic http resource server (serve text and images)
        super.handler(ResourceHandler.create(this.config()));

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

    public IRouter router(){
        return _router;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------



}
