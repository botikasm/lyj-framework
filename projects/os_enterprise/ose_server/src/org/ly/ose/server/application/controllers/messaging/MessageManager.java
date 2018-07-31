package org.ly.ose.server.application.controllers.messaging;

import org.json.JSONArray;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.endpoints.TokenController;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.OSEProgramInfo;
import org.ly.ose.server.application.programming.OSEProgramSessions;
import org.ly.ose.server.application.programming.ProgramsManager;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.utils.Converter;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        // remove "null" values
        removeNulls(request);

        // validate request localization
        if (!StringUtils.hasText(request.lang())) {
            request.lang(IConstants.DEF_LANG);
        }

        final OSEResponse response = new OSEResponse();
        response.clientId(request.clientId());
        response.lang(request.lang());
        response.type(request.type());
        try {
            // choose handler for each message type supported
            if (TYPE_PROGRAM.equalsIgnoreCase(request.type())) {
                this.handleRequestProgram(request, response);
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

    private void handleRequestProgram(final OSERequest request,
                                      final OSEResponse response) throws Exception {
        final OSEPayloadProgram payload = new OSEPayloadProgram(request.payload());
        if (StringUtils.hasText(payload.namespace())
                && StringUtils.hasText(payload.function())) {

            final String app_token = payload.appToken();
            // security check
            if (StringUtils.hasText(app_token)) {
                if (TokenController.instance().auth(app_token)) {

                    final String namespace = payload.namespace();
                    final String function = payload.function();
                    final long session_timeout = payload.sessionTimeout();
                    final List<Object> payload_params = payload.params();
                    final String client_session_id = StringUtils.hasText(payload.clientId())
                            ? payload.clientId()
                            : request.clientId();

                    final OSEProgram program;
                    if (StringUtils.hasText(client_session_id)) {
                        if (OSEProgramSessions.instance().containsKey(client_session_id)) {
                            program = OSEProgramSessions.instance().get(client_session_id);
                        } else {
                            // new program
                            program = ProgramsManager.instance().getNew(namespace);
                            if (null != program) {
                                // add some advanced info to program
                                program.info().data().put(OSEProgramInfo.FLD_SESSION_ID, client_session_id);

                                // add program to session manager and initialize
                                OSEProgramSessions.instance().put(client_session_id, program, session_timeout);
                            }
                        }

                    } else {
                        program = ProgramsManager.instance().getNew(namespace);
                    }

                    if (null != program) {

                        // add/refresh context data
                        program.request(request);

                        // call required function
                        final Object[] params = CollectionUtils.isEmpty(payload_params) ? null : payload_params.toArray(new Object[0]);
                        final Object result = null != params
                                ? program.callMember(function, params)
                                : program.callMember(function);
                        final JSONArray json_result = Converter.toJsonArray(result);
                        response.payload(json_result);

                        // close program if not in session
                        if (!StringUtils.hasText(client_session_id)
                                || !OSEProgramSessions.instance().containsKey(client_session_id)) {
                            program.close();
                        }
                    } else {
                        throw new Exception(FormatUtils.format("Invalid Program Exception: Namespace '%s' not found!",
                                namespace));
                    }
                } else {
                    throw new Exception("Security Exception: Unauthorized request!");
                }
            } else {
                throw new Exception("Security Exception: Missing Application Token!");
            }
        } else {
            // invalid request, missing some parameters
            throw new Exception("Malformer payload. Missing 'namespace' or 'function' fields");
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static void removeNulls(final Map<String, Object> map) {
        if (null != map) {
            final Set<String> keys = map.keySet();
            for (final String key : keys) {
                final Object value = map.get(key);
                if (null != value) {
                    if (value instanceof String && value.toString().equalsIgnoreCase(IConstants.STR_NULL)) {
                        map.put(key, "");
                    } else if (value instanceof Map) {
                        removeNulls((Map) value);
                    }
                }
            }
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
