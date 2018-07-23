package org.ly.server.application.model.messaging.payloads;

public class PayloadProgram extends Payload {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_NAMESPACE = "namespace";
    private static final String FLD_FUNCTION = "function";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public PayloadProgram() {
        super();
    }

    public PayloadProgram(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String namespace() {
        return super.map().getString(FLD_NAMESPACE);
    }

    public PayloadProgram namespace(final String value) {
        super.map().put(FLD_NAMESPACE, value);
        return this;
    }

    public String function() {
        return super.map().getString(FLD_FUNCTION);
    }

    public PayloadProgram function(final String value) {
        super.map().put(FLD_FUNCTION, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------




}
