package org.lyj.ext.netty.server.web.controllers;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Handlers controller
 */
public class HttpServerRequestHandlers
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final List<AbstractRequestHandler> _handlers;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServerRequestHandlers() {
        _handlers = new ArrayList<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isEmpty() {
        return _handlers.isEmpty();
    }

    public int size() {
        return _handlers.size();
    }

    public HttpServerRequestHandlers add(final AbstractRequestHandler handler) {
        if (!_handlers.contains(handler)) {
            _handlers.add(handler);
        }
        return this;
    }

    public void handle(final HttpServerRequestContext context) {
        final HttpServerRequest request = new HttpServerRequest(context);
        final HttpServerResponse response = new HttpServerResponse(context);

        for(final AbstractRequestHandler handler:_handlers){
            try {
                handler.handle(request, response);
                if(response.handled()){
                    break;
                }
            }catch(Throwable e){
                super.error("handle", e);
                break;
            }
        }

        // time to write response?
        if(request.hasLastHttpContent()){
            response.flush();
        }
    }

    public void close(){
        for(final AbstractRequestHandler handler:_handlers){
            try {
                handler.close();
            }catch(Throwable e){
                super.error("close", e);
                break;
            }
        }
    }

}
