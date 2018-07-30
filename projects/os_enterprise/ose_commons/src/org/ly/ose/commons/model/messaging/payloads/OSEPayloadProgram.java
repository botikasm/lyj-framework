package org.ly.ose.commons.model.messaging.payloads;

public class OSEPayloadProgram extends OSEPayload {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_NAMESPACE = "namespace";
    private static final String FLD_FUNCTION = "function";
    private static final String FLD_PARAMS = "params";
    private static final String FLD_SESSION_TIMEOUT = "session_timeout";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEPayloadProgram() {
        super();
        this.init();
    }

    public OSEPayloadProgram(final Object item) {
        super(item);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String namespace() {
        return super.map().getString(FLD_NAMESPACE);
    }

    public OSEPayloadProgram namespace(final String value) {
        super.map().put(FLD_NAMESPACE, value);
        return this;
    }

    public String function() {
        return super.map().getString(FLD_FUNCTION);
    }

    public OSEPayloadProgram function(final String value) {
        super.map().put(FLD_FUNCTION, value);
        return this;
    }

    public long sessionTimeout() {
        return super.map().getLong(FLD_SESSION_TIMEOUT);
    }

    public OSEPayloadProgram sessionTimeout(final long value) {
        super.map().put(FLD_SESSION_TIMEOUT, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        this.sessionTimeout(-1); // not setted (use default system timeout)
    }

}
