package org.lyj.ext.netty.server.web.controllers.routing;

import org.lyj.commons.Delegates;

import java.util.Map;

/**
 * Route
 */
public interface IRoute {

    IRoute handler(final Delegates.Callback<RoutingContext> callback);


}
