package org.lyj.ext.netty.server.web.controllers.routing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class RouteMap {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _encoding;
    private final Map<String, Route> _routes;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RouteMap(final String encoding) {
        _routes = new HashMap<>();
        _encoding = encoding;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean containsKey(final String key) {
        return _routes.containsKey(key);
    }

    public RouteMap put(final String method, final String path) {
        this.addNewRoute(method, path);
        return this;
    }

    public IRoute get(final String path) {
        final String key = Route.id(path);
        return _routes.get(key);
    }

    public Collection<Route> routes() {
        return _routes.values();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void addNewRoute(final String method, final String path) {
        final String key = Route.id(path);
        if (!_routes.containsKey(key)) {
            final Route route = new Route(path, _encoding);
            route.method(method);
            _routes.put(key, route);
        } else {
            final Route route = _routes.get(key);
            route.method(method);
        }
    }




}
