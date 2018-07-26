package org.ly.ose.server.application.controllers.messaging;

import org.json.JSONArray;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.OSEProgramInfo;
import org.ly.ose.server.application.programming.OSEProgramSessions;
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
            // choose handler for each message type supported
            if (TYPE_PROGRAM.equalsIgnoreCase(request.type())) {
                this.handleRequestProgram(response);
            } else {
                // unsupported message type
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
            final long session_timeout = payload.sessionTimeout();
            final String client_session_id = response.request().channel();

            final OSEProgram program;
            if (StringUtils.hasText(client_session_id)) {
                if (OSEProgramSessions.instance().containsKey(client_session_id)) {
                    program = OSEProgramSessions.instance().get(client_session_id);
                } else {
                    // new program
                    program = ProgramsManager.instance().getNew(namespace);
                    // add some advanced info to program
                    program.info().data().put(OSEProgramInfo.FLD_SESSION_ID, client_session_id);

                    // add program to session manager and initialize
                    OSEProgramSessions.instance().put(client_session_id, program, session_timeout);
                }

            } else {
                program = ProgramsManager.instance().getNew(namespace);
            }

            // call required function
            final Object result = program.callMember(function);
            final JSONArray json_result = Converter.toJsonArray(result);
            response.payload(json_result);

            // close program if not in session
            if (!StringUtils.hasText(client_session_id)
                    || !OSEProgramSessions.instance().containsKey(client_session_id)) {
                program.close();
            }
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
