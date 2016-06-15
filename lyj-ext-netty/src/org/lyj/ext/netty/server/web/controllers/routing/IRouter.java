package org.lyj.ext.netty.server.web.controllers.routing;

/**
 * Router interface
 */
public interface IRouter {

    IRoute get(final String path);
    IRoute post(final String path);
    IRoute delete (final String path);
    IRoute put(final String path);

    /**
     * All methods
     * @param path
     * @return
     */
    IRoute all(final String path);

}
