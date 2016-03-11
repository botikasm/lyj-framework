package org.lyj.desktopgap.app.http;

import org.lyj.Lyj;
import org.lyj.desktopgap.deploy.htdocs.HtdocsDeployer;
import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.web.handlers.impl.ResourceHandler;

/**
 *
 */
public class WebServer
        extends HttpServer {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebServer() {
        this.config().port(4000).portAutodetect(true).root(Lyj.getAbsolutePath(HtdocsDeployer.PATH));
        this.handler(new ResourceHandler(this.config()));
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
