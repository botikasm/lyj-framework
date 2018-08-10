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

    public OSEResponse handle(final OSERequest request) {
        final OSEResponse response = OSERequest.generateResponse(request);
        try {
            this.handleRequest(request, response);
        } catch (Throwable t) {
            response.error(ExceptionUtils.getRealMessage(t));
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected abstract  void  handleRequest(final OSERequest request, final OSEResponse response) throws Exception;


}
