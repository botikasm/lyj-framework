package org.ly.ose.server.application.programming.tools;

import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.server.application.programming.OSEProgram;

public abstract class OSEProgramToolRequest
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private OSERequest _request;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEProgramToolRequest(final String name,
                                 final OSEProgram program) {
        super(name, program);
    }

    @Override
    public void close() {
        _request = null;
    }

    // ------------------------------------------------------------------------
    //                      setter and getter
    // ------------------------------------------------------------------------


    public void set_native_request_value(final OSERequest request) {
        _request = request;
    }

    protected OSERequest get_native_request_value() {
        return _request;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean hasRequest() {
        return null != _request;
    }

    public String clientId() {
        if (null != _request) {
            return _request.clientId();
        }
        return "";
    }

    public String getClient_id(){
        return this.clientId();
    }

    public String getLang(){
        if (null != _request) {
            return _request.lang();
        }
       return "";
    }


}
