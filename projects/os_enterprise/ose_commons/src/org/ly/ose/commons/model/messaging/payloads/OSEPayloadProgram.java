package org.ly.ose.commons.model.messaging.payloads;

import org.json.JSONArray;
import org.ly.ose.commons.IConstants;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class OSEPayloadProgram extends OSEPayload {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_APP_TOKEN = "app_token"; // security token
    private static final String FLD_NAMESPACE = "namespace"; // "system.utils"
    private static final String FLD_FUNCTION = "function"; // format.date
    private static final String FLD_PARAMS = "params"; // [1500998882992, it]
    private static final String FLD_SESSION_TIMEOUT = "session_timeout";
    private static final String FLD_CLIENT_ID = "client_id"; // optional (override request client id)

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

    public String appToken() {
        return super.map().getString(FLD_APP_TOKEN);
    }

    public OSEPayloadProgram appToken(final String value) {
        super.map().put(FLD_APP_TOKEN, value);
        return this;
    }

    public String clientId() {
        return super.map().getString(FLD_CLIENT_ID);
    }

    public OSEPayloadProgram clientId(final String value) {
        super.map().put(FLD_CLIENT_ID, value);
        return this;
    }

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

    public List<Object> params() {
        if (null == super.map().get(FLD_PARAMS)) {
            super.map().put(FLD_PARAMS, new LinkedList<>());
        }
        return (List<Object>) super.map().get(FLD_PARAMS);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        this.sessionTimeout(-1); // not setted (use default system timeout)
        if (!super.map().has(FLD_PARAMS)) {
            super.map().put(FLD_PARAMS, new LinkedList<>());
        } else {
            final Object params = super.map().get(FLD_PARAMS);
            if (!(params instanceof Collection)) {

                if (StringUtils.isJSONArray(params)) {
                    final JSONArray array = new JSONArray(params);
                    super.map().put(FLD_PARAMS, JsonWrapper.toListOfString(array));
                } else if (params instanceof String && !IConstants.STR_NULL.equalsIgnoreCase((String)params)) {
                    final String[] array = StringUtils.split((String) params, ",;", true);
                    super.map().put(FLD_PARAMS, Arrays.asList(array));
                }

            }
        }
    }

}
