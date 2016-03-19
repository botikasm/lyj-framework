package org.lyj.desktopfences.app.client.api;

import org.json.JSONObject;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.desktopfences.app.IConstants;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.controllers.routing.RoutingContext;

/**
 * Utility client API
 */
public class ApiUtils {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String CONTENT_JSON = "application/json; charset=utf-8";
    private static final String CONTENT_TEXT = "text/plain; charset=utf-8";

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static void version (final RoutingContext context) {
        context.writeJson(IConstants.VERSION);
    }



}
