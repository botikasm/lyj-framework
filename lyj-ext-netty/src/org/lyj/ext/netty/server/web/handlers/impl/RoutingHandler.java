package org.lyj.ext.netty.server.web.handlers.impl;

import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.controllers.routing.IRoute;
import org.lyj.ext.netty.server.web.controllers.routing.IRouter;
import org.lyj.ext.netty.server.web.controllers.routing.Router;
import org.lyj.ext.netty.server.web.controllers.routing.RoutingContext;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

/**
 *
 */
public class RoutingHandler
        extends AbstractRequestHandler
        implements IRouter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Router _router;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected RoutingHandler(final HttpServerConfig config) {
        super(config);
        _router = new Router(config.encoding());
    }


    @Override
    public void handle(final HttpServerRequest request,
                       final HttpServerResponse response) {
        _router.handle(new RoutingContext(super.config(), request, response));
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public IRoute all(final String path) {
        return _router.all(path);
    }

    @Override
    public IRoute get(final String path) {
        return _router.get(path);
    }

    @Override
    public IRoute post(final String path) {
        return _router.post(path);
    }

    @Override
    public IRoute delete(String path) {
        return _router.delete(path);
    }

    @Override
    public IRoute put(String path) {
        return _router.put(path);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static RoutingHandler create(final HttpServerConfig config) {
        return new RoutingHandler(config);
    }


}
