package org.ly.ose.server.application.endpoints.api.routing.impl;


import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadDatabase;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.Launcher;
import org.ly.ose.server.application.controllers.messaging.MessageManager;
import org.ly.ose.server.application.endpoints.api.ApiHelper;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerContext;

import java.util.Map;

public class ApiDatabase {

    public static void invoke(final HttpServerContext context) {
        final OSERequest request = new OSERequest();
        final Map<String, Object> request_params = ApiHelper.getParams(context);
        final OSEPayloadDatabase payload = new OSEPayloadDatabase(request_params);

        // add payload
        request.payload().putAll(payload.map());

        // complete request
        request.source(IConstants.CHANNEL_API);
        if (!StringUtils.hasText(request.type())) {
            request.type(IConstants.TYPE_DATABASE);
        }
        if (!StringUtils.hasText(request.lang())) {
            request.lang(context.getLang());
        }
        request.clientId(context.request().uuid());
        request.address(context.fullUrl());

        // invoke handler
        final Object response = MessageManager.instance().handle(request);
        if (response instanceof OSEResponse) {
            ((OSEResponse) response).uid(Launcher.configApi().uri());
            ApiHelper.writeJSON(context, ((OSEResponse) response).json());
        }else {
            ApiHelper.writeError(context, "output not supported: " + response.getClass().getName());
        }

    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
