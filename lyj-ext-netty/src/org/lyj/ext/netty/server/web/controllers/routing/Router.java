package org.lyj.ext.netty.server.web.controllers.routing;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.netty.server.web.IHttpConstants;

import java.util.Collection;


/**
 * Router
 */
public class Router
        extends AbstractLogEmitter
        implements IRouter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final RouteMap _routes;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Router(final String encoding) {
        _routes = new RouteMap(encoding);
    }

    // ------------------------------------------------------------------------
    //                      IRouter
    // ------------------------------------------------------------------------

    @Override
    public IRoute all(final String path) {
        return this.addRoute(path, "*");
    }

    @Override
    public IRoute get(final String path) {
        return this.addRoute(path, IHttpConstants.METHOD_GET);
    }

    @Override
    public IRoute post(final String path) {
        return this.addRoute(path, IHttpConstants.METHOD_POST);
    }

    @Override
    public IRoute delete(final String path) {
        return this.addRoute(path, IHttpConstants.METHOD_DELETE);
    }

    @Override
    public IRoute put(final String path) {
        return this.addRoute(path, IHttpConstants.METHOD_PUT);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void handle(final RoutingContext context) {
        final String uri = context.uri();
        final String method = context.method();
        final Collection<Route> routes = _routes.routes();
        for (final Route route : routes) {
            final RouteParsedPath match = route.match(method, uri);
            if (null!=match && match.matchTemplate()) {

                // add url REST parameters to contest
                if (match.params().size()>0) {
                    context.addParams(match.params());
                }

                try {
                    route.handle(context);
                    context.handled(true);
                } catch (Throwable t) {
                    super.error("handle", t);
                }
                // handled: must break chain
                break;
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private IRoute addRoute(final String path, final String method) {
        return _routes.put(method, path).get(path);
    }


}
