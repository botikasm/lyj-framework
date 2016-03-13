package org.lyj.ext.netty.server.web.controllers.routing;

import org.lyj.commons.lang.CharEncoding;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * sample path:
 * /api/name/:param1/:param2
 * /api/name
 * /api/*
 */
public class RouteUrl {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final RouteParsedPath _url;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RouteUrl(final String path, final String encoding) {
        _url = new RouteParsedPath(path,
                CharEncoding.isSupported(encoding) ? encoding : CharEncoding.getDefault());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String path() {
        return _url.path();
    }

    public RouteParsedPath parse(final String path) {
        return new RouteParsedPath(path, _url);
    }

    public Map<String, String> params(final String url_path) {
        final Map<String, String> response = new LinkedHashMap<>();


        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
