package org.lyj.ext.netty.server.websocket.impl.sessions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.*;
import org.lyj.commons.util.converters.JsonConverter;

/**
 * Standard socket message
 */
public class SessionMessage
        extends JsonItem {


    // --------------------------------------------------------------------
    //               c o n s t
    // --------------------------------------------------------------------

    private static final String FLD_ID = "id";
    private static final String FLD_SESSION_ID = "session_id";

    private static final String FLD_DATA = "data";
    private static final String FLD_ERROR = "error";

    private static final String FLD_ERROR_CODE = "error_code";
    private static final String FLD_ERROR_MESSAGE = "error_message";

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public SessionMessage(final Object item) {
        super(item);
    }

    public SessionMessage(final String session_id) {
        super();
        this.sessionId(session_id);
        this.id(RandomUtils.randomUUID(true));
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public boolean isError() {
        return null != this.error();
    }

    public SessionMessage error(final String error_message) {
        return this.error(500, error_message);
    }

    public SessionMessage error(final Throwable error) {
        final String msg = ExceptionUtils.getRealMessage(error);
        return this.error(500, StringUtils.hasText(msg) ? msg : error.toString());
    }

    public SessionMessage error(final int error_code,
                                final String error_message) {
        final JSONObject error = new JSONObject();
        error.put(FLD_ERROR_CODE, error_code);
        error.put(FLD_ERROR_MESSAGE, error_message);
        this.error(error);
        return this;
    }


    // --------------------------------------------------------------------
    //               p r o p e r t i e s
    // --------------------------------------------------------------------

    public SessionMessage id(final String value) {
        super.put(FLD_ID, value);
        return this;
    }

    public String id() {
        return super.getString(FLD_ID);
    }

    public SessionMessage sessionId(final String value) {
        super.put(FLD_SESSION_ID, value);
        return this;
    }

    public String sessionId() {
        return super.getString(FLD_SESSION_ID);
    }

    public SessionMessage data(final Object value) {
        super.put(FLD_DATA, toJsonCompatible(value));
        return this;
    }

    public Object data() {
        return super.get(FLD_DATA);
    }

    public SessionMessage error(final JSONObject value) {
        super.put(FLD_ERROR, value);
        return this;
    }

    public JSONObject error() {
        return super.getJSONObject(FLD_ERROR);
    }

    // --------------------------------------------------------------------
    //               data conversion
    // --------------------------------------------------------------------

    public byte[] asBytes() {
        final Object data = this.data();
        if (JsonConverter.isBinary(data)) {
            return (byte[]) JsonConverter.decodeIfBinary(data);
        }
        return new byte[0];
    }

    public String asText() {
        final Object data = this.data();
        return null != data ? data.toString() : "";
    }

    public int asInteger() {
        return ConversionUtils.toInteger(this.data());
    }

    public double asDouble() {
        return ConversionUtils.toDouble(this.data());
    }

    public boolean asBoolean() {
        return ConversionUtils.toBoolean(this.data());
    }

    public JSONObject asJSONObject() {
        final Object data = this.data();
        return data instanceof JSONObject ? (JSONObject) data : null;
    }

    public JSONArray asJSONArray() {
        final Object data = this.data();
        return data instanceof JSONArray ? (JSONArray) data : null;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static Object toJsonCompatible(final Object value) {
        final Object converted = JsonConverter.toJsonCompatible(value);
        return null != converted ? converted : value.toString();
    }

    // --------------------------------------------------------------------
    //               F A C T O R Y
    // --------------------------------------------------------------------

    public static SessionMessage create(final Object item) {
        return new SessionMessage(item);
    }

    public static SessionMessage create(final String session_id) {
        return new SessionMessage(session_id);
    }

}
