package org.ly.ose.server.application.programming.tools.ose;

import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.OSEProgramInvoker;
import org.ly.ose.server.application.programming.OSEProgramInvokerMonitor;
import org.ly.ose.server.application.programming.tools.OSEProgramToolRequest;
import org.lyj.commons.util.converters.MapConverter;

import java.util.Collection;

/**
 * OS-Enterprise cross-invocation utility
 */
public class Tool_ose
        extends OSEProgramToolRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "ose"; // used as $ose.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _package_name;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_ose(final OSEProgram program) {
        super(NAME, program);

        _package_name = super.info().fullName();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    /**
     * Invoke a function declared in another program.
     * NOTE: Each program works in a different virtual machine.
     * @param namespace ex: "com.drillio.server"
     * @param function  ex: "version"
     * @param raw_params Optional parameters
     */
    public Object callMember(final String namespace,
                             final String function,
                             final Object... raw_params) throws Exception {
        final OSERequest request = this.request();
        if (!request.hasPayload()) {
            final OSEPayloadProgram payload = new OSEPayloadProgram();
            payload.clientId(request.clientId());
            payload.namespace(namespace);
            payload.function(function);
            request.payload().putAll(payload.map());
        }
        // security check for infinite recursive loops
        OSEProgramInvokerMonitor.instance().validate(request);

        final String client_session_id = request.clientId();
        final Collection<Object> params = MapConverter.toList(raw_params);
        final Object response = OSEProgramInvoker.instance().callMember(request, client_session_id, -1,
                namespace, function, params);
        if (response instanceof OSEResponse) {
            return ((OSEResponse) response).payload();
        } else {
            return response;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private OSERequest request() throws Exception {
        final OSERequest request = super.get_native_request_value();
        if (null == request) {
            throw new Exception("NULL request is not a valid request.");
        }
        return request;
    }

}
