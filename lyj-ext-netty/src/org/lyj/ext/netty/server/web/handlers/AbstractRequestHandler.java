package org.lyj.ext.netty.server.web.handlers;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;


/**
 *
 */
public abstract class AbstractRequestHandler extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerConfig _config;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractRequestHandler(final HttpServerConfig config) {
        _config = config;
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    public abstract void handle(final HttpServerRequest request, final HttpServerResponse response);

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected HttpServerConfig config() {
        return _config;
    }

}
