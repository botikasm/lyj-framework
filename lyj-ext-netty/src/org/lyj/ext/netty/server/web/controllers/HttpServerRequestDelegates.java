package org.lyj.ext.netty.server.web.controllers;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * Handlers controller
 */
public class HttpServerRequestDelegates
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final List<AbstractRequestHandler> _instance_handlers;
    private final List<Class<? extends AbstractRequestHandler>> _class_handlers;
    private final HttpServerConfig _config;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServerRequestDelegates(final HttpServerConfig config) {
        _config = config;
        _instance_handlers = new LinkedList<>();
        _class_handlers = new LinkedList<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isEmpty() {
        return _instance_handlers.isEmpty() && _class_handlers.isEmpty();
    }

    public HttpServerRequestDelegates add(final AbstractRequestHandler handler) {
        if (!_instance_handlers.contains(handler)) {
            _instance_handlers.add(handler);
        }
        return this;
    }

    public HttpServerRequestDelegates add(final Class<? extends AbstractRequestHandler> handler) {
        if (!_class_handlers.contains(handler)) {
            _class_handlers.add(handler);
        }
        return this;
    }

    public boolean handle(final HttpServerRequestContext context) {
        final HttpServerRequest request = new HttpServerRequest(context);
        final HttpServerResponse response = new HttpServerResponse(context);

        final boolean handled = this.handle(request, response);

        //if (handled) {

        // time to write response?
        if (request.hasLastHttpContent()) {
            response.flush();
        }

        //}
        return handled;
    }

    public void close() {
        for (final AbstractRequestHandler handler : _instance_handlers) {
            try {
                handler.close();
            } catch (Throwable e) {
                super.error("close", e);
                break;
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean handle(final HttpServerRequest request, final HttpServerResponse response) {
        boolean handled = false;

        // instance handlers
        for (final AbstractRequestHandler handler : _instance_handlers) {
            try {
                handler.handle(request, response);
                if (response.handled()) {
                    handled = true;
                    break;
                }
            } catch (Throwable e) {
                super.error("handle", e);
                break;
            }
        }

        // class handlers
        if (!handled) {
            for (final Class<? extends AbstractRequestHandler> handler : _class_handlers) {
                try {
                    final AbstractRequestHandler instance = AbstractRequestHandler.create(handler, _config);
                    if (null != instance) {
                        instance.handle(request, response);
                        if (response.handled()) {
                            handled = true;
                            break;
                        }
                    }
                } catch (Throwable e) {
                    super.error("handle", e);
                    break;
                }
            }
        }

        return handled;
    }

}
