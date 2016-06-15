package org.lyj.ext.netty.server.web.controllers.routing;

import org.lyj.commons.Delegates;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Rule for a routing path.
 */
public class Route
        implements IRoute {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _id;
    private final String _path;

    private Delegates.Callback<HttpServerContext> _handler;

    private final Set<String> _methods;
    private final RouteUrl _url;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Route(final String path, final String encoding) {
        _path = path;
        _id = id(path);
        _methods = new HashSet<>();
        _url = new RouteUrl(path, encoding);
    }

    @Override
    public String toString() {
        return _path;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String id() {
        return _id;
    }

    public String path() {
        return _path;
    }

    public Route handler(final Delegates.Callback<HttpServerContext> handler) {
        _handler = handler;
        return this;
    }

    public Route method(final String method) {
        if (StringUtils.hasText(method)) {
            _methods.add(method.toUpperCase());
        }
        return this;
    }

    public RouteParsedPath match(final String method,
                         final String path) {
        if (_methods.contains(method) || _methods.contains("*")) {
            return _url.parse(path);
        }
        return null;
    }

    void handle(final HttpServerContext context) throws Throwable {
        if(null!=_handler){
            try{
                _handler.handle(context);
            } catch(Throwable t){
                throw t;
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static String id(final String path) {
        return MD5.encode(path.toLowerCase());
    }


}
