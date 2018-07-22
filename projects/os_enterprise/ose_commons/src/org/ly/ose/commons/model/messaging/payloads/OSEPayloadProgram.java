package org.ly.ose.commons.model.messaging.payloads;

import org.lyj.ext.db.model.MapDocument;

public class OSEPayloadProgram extends OSEPayload{

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

    public OSEPayloadProgram() {
        super();
    }

    public OSEPayloadProgram(final Object item) {
        super(item);
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

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------




}
