package org.ly.ose.commons.model.messaging;

import org.json.JSONArray;
import org.ly.ose.commons.model.BaseModel;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.JsonConverter;

import java.util.Map;

/**
 * Generic wrapper for a response object
 */
public class OSEResponse
        extends BaseModel {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    private static final String FLD_UID = "uid"; // unique sender ID (sender endpoint)
    private static final String FLD_LANG = "lang";
    private static final String FLD_TYPE = "type";
    private static final String FLD_ERROR = "error";

    private static final String FLD_REQUEST = "request";

    private static final String FLD_SENDER = "sender"; // (object) further sender details
    private static final String FLD_PAYLOAD = "payload"; // (object) further data attached to message

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEResponse() {
        super();
    }

    public OSEResponse(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String uid() {
        return super.getString(FLD_UID);
    }

    public OSEResponse uid(final String value) {
        super.put(FLD_UID, value);
        return this;
    }

    public String lang() {
        return super.getString(FLD_LANG);
    }

    public OSEResponse lang(final String value) {
        super.put(FLD_LANG, value);
        return this;
    }

    public String type() {
        return super.getString(FLD_TYPE);
    }

    public OSEResponse type(final String value) {
        super.put(FLD_TYPE, value);
        return this;
    }

    public OSERequest request() {
        return new OSERequest(super.getMap(FLD_REQUEST));
    }

    public OSEResponse request(final OSERequest value) {
        super.put(FLD_REQUEST, value);
        return this;
    }

    public String error() {
        return super.getString(FLD_ERROR);
    }

    public OSEResponse error(final String value) {
        super.put(FLD_ERROR, value);
        return this;
    }

    public Map<String, Object> sender() {
        return super.getMap(FLD_SENDER, true);
    }

    public JSONArray payload() {
        return super.getJSONArray(FLD_PAYLOAD, true);
    }

    public OSEResponse payload(final JSONArray value) {
        super.put(FLD_PAYLOAD, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean hasPayload() {
        return super.has(FLD_PAYLOAD);
    }

    public boolean hasError() {
        return super.has(FLD_ERROR) && StringUtils.hasText(this.error());
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
