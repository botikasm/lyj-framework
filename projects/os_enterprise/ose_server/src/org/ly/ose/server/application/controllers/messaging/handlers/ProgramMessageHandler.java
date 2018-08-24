package org.ly.ose.server.application.controllers.messaging.handlers;

import org.json.JSONArray;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.application.endpoints.TokenController;
import org.ly.ose.server.application.programming.*;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.utils.Converter;

import java.io.File;
import java.util.List;

public class ProgramMessageHandler
        extends AbstractMessageHandler {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final Object SYNCHRONIZER = new Object(); // empty static object

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ProgramMessageHandler() {

    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    @Override
    protected Object handleRequest(final OSERequest request) throws Exception {
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


                    final OSEProgramInfo info = ProgramsManager.instance().getInfo(namespace);
                    if (null != info) {

                        /**
                        OSEProgramInvoker.instance().callMember(
                                request,
                                client_session_id,
                                session_timeout,
                                info,
                                function,
                                payload_params);
                        **/
                        
                        // get or create the program
                        final OSEProgram program = OSEProgramInvoker.instance().get(
                                info,
                                client_session_id,
                                session_timeout);

                        if (null != program) {
                            final String program_session_id = (String) program.info().data().get(OSEProgramInfo.FLD_SESSION_ID);

                            // add/refresh context data
                            if (!info.singleton()) {
                                program.request(request);
                            }

                            // call required function
                            try {
                                final Object program_result = callMember(program, function, payload_params);
                                if (program_result instanceof JSONArray) {
                                    final OSEResponse response = OSERequest.generateResponse(request);
                                    response.payload((JSONArray) program_result);
                                    return response;
                                } else {
                                    // special response
                                    return program_result;
                                }
                            } finally {
                                // close program if not in session
                                if (!StringUtils.hasText(program_session_id)
                                        || !OSEProgramSessions.instance().containsKey(program_session_id)) {
                                    program.close();
                                }
                            }
                        } else {
                            throw new Exception(FormatUtils.format("Invalid Program Exception: Namespace '%s' not found!",
                                    namespace));
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
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Object callMember(final OSEProgram program,
                              final String function,
                              final List<Object> payload_params) throws Exception {
        final Object[] params = CollectionUtils.isEmpty(payload_params)
                ? null
                : payload_params.toArray(new Object[0]);
        return callMember(program, function, params);
    }

    private Object callMember(final OSEProgram program,
                              final String function,
                              final Object[] params) throws Exception {
        if (null != program) {
            if (program.info().singleton()) {
                synchronized (SYNCHRONIZER) {
                    final Object result = null != params
                            ? program.callMember(function, params)
                            : program.callMember(function);
                    return convert(result);
                }
            } else {
                final Object result = null != params
                        ? program.callMember(function, params)
                        : program.callMember(function);
                return convert(result);
            }
        }
        return new JSONArray();
    }

    private static Object convert(final Object result) throws Exception {
        if (result instanceof File) {
            return result;
        } else if (result instanceof Exception) {
            throw (Exception) result;
        }
        return Converter.toJsonArray(result);
    }

}
