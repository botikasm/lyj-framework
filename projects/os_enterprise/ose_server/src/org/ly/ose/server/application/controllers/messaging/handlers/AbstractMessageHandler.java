package org.ly.ose.server.application.controllers.messaging.handlers;

import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.lyj.commons.util.ExceptionUtils;

public abstract class AbstractMessageHandler {



    public AbstractMessageHandler(){

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Object handle(final OSERequest request) {
        try {
            return this.handleRequest(request);
        } catch (Throwable t) {
            final OSEResponse response = OSERequest.generateResponse(request);
            response.error(ExceptionUtils.getRealMessage(t));
            return response;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected abstract  Object  handleRequest(final OSERequest request) throws Exception;


}
