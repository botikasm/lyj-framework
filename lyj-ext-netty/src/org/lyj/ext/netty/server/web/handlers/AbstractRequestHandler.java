package org.lyj.ext.netty.server.web.handlers;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 *
 */
public abstract class AbstractRequestHandler
        extends AbstractLogEmitter {

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

    public abstract void close();

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected HttpServerConfig config() {
        return _config;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static AbstractRequestHandler create(final Class<? extends AbstractRequestHandler> aclass,
                                                final HttpServerConfig config)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null != aclass) {
            final Constructor<? extends AbstractRequestHandler> ctr = aclass.getConstructor(HttpServerConfig.class);
            if (null != ctr) {
                return ctr.newInstance(config);
            }
        }
        return null;
    }


}
