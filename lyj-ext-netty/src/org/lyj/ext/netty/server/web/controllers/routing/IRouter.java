package org.lyj.ext.netty.server.web.controllers.routing;

import org.lyj.commons.Delegates;

/**
 * Router interface
 */
public interface IRouter {

    IRoute get(final String path);
    IRoute post(final String path);
    IRoute delete (final String path);
    IRoute put(final String path);

}
