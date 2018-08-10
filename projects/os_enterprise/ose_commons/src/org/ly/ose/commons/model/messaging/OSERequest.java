package org.ly.ose.commons.model.messaging;

import org.ly.ose.commons.IConstants;
import org.ly.ose.commons.model.BaseModel;
import org.lyj.commons.util.StringUtils;

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

    public static final String TYPE_PROGRAM = IConstants.TYPE_PROGRAM; // request a program to execute (run custom program on server)
    public static final String TYPE_DATABASE = IConstants.TYPE_DATABASE; // request a service to execute (usually access to database)
    public static final String TYPE_ERROR = IConstants.TYPE_ERROR;

    private static final String FLD_UID = "uid"; // unique client ID
    private static final String FLD_LANG = "lang";
    private static final String FLD_TYPE = "type";
    private static final String FLD_SOURCE = "source"; // api, socket, facebook, telegram....
    private static final String FLD_CLIENT_ID = "client_id"; // session ID on server (usually websocket has a channel/session_id for responses)
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

    public String source() {
        return super.getString(FLD_SOURCE);
    }

    public OSERequest source(final String value) {
        super.put(FLD_SOURCE, value);
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
    public String clientId() {
        if (StringUtils.hasText((String) this.payload().get(FLD_CLIENT_ID))) {
            return (String) this.payload().get(FLD_CLIENT_ID);
        }
        return super.getString(FLD_CLIENT_ID);
    }

    public OSERequest clientId(final String value) {
        super.put(FLD_CLIENT_ID, value);
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

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static OSEResponse generateResponse(final OSERequest request){
        final OSEResponse response = new OSEResponse();
        response.clientId(request.clientId());
        response.lang(request.lang());
        response.type(request.type());

        return response;
    }

}


