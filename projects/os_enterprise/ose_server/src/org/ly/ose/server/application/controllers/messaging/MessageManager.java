package org.ly.ose.server.application.controllers.messaging;

import org.json.JSONArray;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.ProgramsManager;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.utils.Converter;

/**
 * Main message manager.
 * Process OSERequests and returns OSEResponse
 */
public class MessageManager {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String TYPE_PROGRAM = OSERequest.TYPE_PROGRAM;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private MessageManager() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public OSEResponse handle(final OSERequest request) {
        final OSEResponse response = new OSEResponse();
        response.request(request); // client response
        response.lang(request.lang());
        response.type(request.type());
        try {
            if (TYPE_PROGRAM.equalsIgnoreCase(request.type())) {
                this.handleRequestProgram(response);
            } else {
                response.error("Unhandled request type: " + request.type() + ". value=" + request.toString());
            }
        } catch (Throwable t) {
            response.error(ExceptionUtils.getRealMessage(t));
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handleRequestProgram(final OSEResponse response) throws Exception {
        final OSEPayloadProgram payload = new OSEPayloadProgram(response.request().payload());
        if (StringUtils.hasText(payload.namespace())
                && StringUtils.hasText(payload.function())) {
            final String namespace = payload.namespace();
            final String function = payload.function();

            // invoke new program (no session) TODO: add sessions for programs
            final OSEProgram program = ProgramsManager.instance().getNew(namespace);
            final Object init_response = program.open();
            final Object result = program.callMember(function);
            final JSONArray json_result = Converter.toJsonArray(result);
            response.payload(json_result);
            
        } else {
            // invalid request, missing some parameters
            throw new Exception("Malformer payload. Missing 'namespace' or 'function' fields");
        }
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static MessageManager __instance;

    public static synchronized MessageManager instance() {
        if (null == __instance) {
            __instance = new MessageManager();
        }
        return __instance;
    }

}
