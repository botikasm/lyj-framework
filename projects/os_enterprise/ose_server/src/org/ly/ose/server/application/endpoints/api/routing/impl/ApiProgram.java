package org.ly.ose.server.application.endpoints.api.routing.impl;


import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.Launcher;
import org.ly.ose.server.application.controllers.messaging.MessageManager;
import org.ly.ose.server.application.endpoints.api.ApiHelper;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerContext;

public class ApiProgram {

    public static void invoke(final HttpServerContext context) {
        final OSERequest request = new OSERequest();
        final OSEPayloadProgram payload = new OSEPayloadProgram(ApiHelper.getParams(context));

        // add payload
        request.payload().putAll(payload.map());

        // complete request
        request.source(IConstants.CHANNEL_API);
        if (!StringUtils.hasText(request.type())) {
            request.type(IConstants.TYPE_PROGRAM);
        }
        if (!StringUtils.hasText(request.lang())) {
            request.lang(context.getLang());
        }
        request.clientId(context.request().uuid());
        request.address(context.fullUrl());

        // invoke handler
        final OSEResponse response = MessageManager.instance().handle(request);
        response.uid(Launcher.configApi().uri());

        ApiHelper.writeJSON(context, response.json());
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
