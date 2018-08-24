package org.ly.ose.server.application.controllers.messaging.handlers;

import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.application.endpoints.TokenController;
import org.ly.ose.server.application.programming.OSEProgramInfo;
import org.ly.ose.server.application.programming.OSEProgramInvoker;
import org.ly.ose.server.application.programming.ProgramsManager;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;

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

                        // invoke program
                        return OSEProgramInvoker.instance().callMember(
                                request,
                                client_session_id,
                                session_timeout,
                                info,
                                function,
                                payload_params);

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


}
