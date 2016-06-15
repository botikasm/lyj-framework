package org.lyj.ext.netty.server.web.controllers.routing;

import org.lyj.commons.Delegates;
import org.lyj.ext.netty.server.web.HttpServerContext;

/**
 * Route
 */
public interface IRoute {

    IRoute handler(final Delegates.Callback<HttpServerContext> callback);


}
