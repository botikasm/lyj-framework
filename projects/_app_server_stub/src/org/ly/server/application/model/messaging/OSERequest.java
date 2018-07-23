package org.ly.server.application.model.messaging;

import org.ly.server.application.model.BaseModel;

import java.util.Map;

/**
 * Generic wrapper for a request object to send at server endpoint using:
 * - http, https
 * - ws, wss
 * <p>
 * field "_key" is unique and generated automatically for each new request.
 */
public class OSERequest
        extends BaseModel {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String TYPE_PROGRAM = "program"; // request a program to execute (run custom program on server)
    public static final String TYPE_SERVICE = "service"; // request a service to execute (usually access to database)

    private static final String FLD_UID = "uid"; // unique client ID
    private static final String FLD_LANG = "lang";
    private static final String FLD_TYPE = "type";
    private static final String FLD_CHANNEL = "channel"; // session ID on server (usually websocket has a channel/session_id for responses)
    private static final String FLD_ADDRESS = "address"; // client address (usually added from server)

    private static final String FLD_SENDER = "sender"; // (object) further sender details
    private static final String FLD_PAYLOAD = "payload"; // (object) further data attached to message

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSERequest() {
        super();
    }

    public OSERequest(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String uid() {
        return super.getString(FLD_UID);
    }

    public OSERequest uid(final String value) {
        super.put(FLD_UID, value);
        return this;
    }

    public String lang() {
        return super.getString(FLD_LANG);
    }

    public OSERequest lang(final String value) {
        super.put(FLD_LANG, value);
        return this;
    }

    public String type() {
        return super.getString(FLD_TYPE);
    }

    public OSERequest type(final String value) {
        super.put(FLD_TYPE, value);
        return this;
    }

    public String address() {
        return super.getString(FLD_ADDRESS);
    }

    public OSERequest address(final String value) {
        super.put(FLD_ADDRESS, value);
        return this;
    }

    /**
     * Returns channel, the session ID required to send a callback to request sender
     */
    public String channel() {
        return super.getString(FLD_CHANNEL);
    }

    public OSERequest channel(final String value) {
        super.put(FLD_CHANNEL, value);
        return this;
    }

    public Map<String, Object> sender() {
        return super.getMap(FLD_SENDER, true);
    }

    public Map<String, Object> payload() {
        return super.getMap(FLD_PAYLOAD, true);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean hasPayload() {
        return super.has(FLD_PAYLOAD);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
