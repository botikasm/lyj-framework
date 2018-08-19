package org.ly.ose.commons.model.messaging.payloads;

import org.json.JSONObject;
import org.ly.ose.commons.IConstants;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.MapConverter;
import org.lyj.commons.util.json.JsonWrapper;

import java.util.HashMap;
import java.util.Map;

public class OSEPayloadDatabase
        extends OSEPayload {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_APP_TOKEN = "app_token"; // security token
    private static final String FLD_CLIENT_ID = "client_id"; // optional (override request client id)

    private static final String FLD_DATABASE = "database"; // "my_data", "ai_nlp_ventis"
    private static final String FLD_COLLECTION = "collection"; // "users"
    private static final String FLD_QUERY = "query"; // "FOR u IN users LET friends = u.friends FILTER u.name == @name RETURN { "name" : u.name, "friends" : friends }"
    private static final String FLD_PARAMS = "params"; // { "name" : "Mario" }


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEPayloadDatabase() {
        super();
        this.init();
    }

    public OSEPayloadDatabase(final Object item) {
        super(item);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String appToken() {
        return super.map().getString(FLD_APP_TOKEN);
    }

    public OSEPayloadDatabase appToken(final String value) {
        super.map().put(FLD_APP_TOKEN, value);
        return this;
    }

    public String clientId() {
        return super.map().getString(FLD_CLIENT_ID);
    }

    public OSEPayloadDatabase clientId(final String value) {
        super.map().put(FLD_CLIENT_ID, value);
        return this;
    }

    public String database() {
        return super.map().getString(FLD_DATABASE);
    }

    public OSEPayloadDatabase database(final String value) {
        super.map().put(FLD_DATABASE, value);
        return this;
    }

    public String collection() {
        return super.map().getString(FLD_COLLECTION);
    }

    public OSEPayloadDatabase collection(final String value) {
        super.map().put(FLD_COLLECTION, value);
        return this;
    }

    public String query() {
        return super.map().getString(FLD_QUERY);
    }

    public OSEPayloadDatabase query(final String value) {
        super.map().put(FLD_QUERY, value);
        return this;
    }


    public Map<String, Object> params() {
        if (null == super.map().get(FLD_PARAMS)) {
            super.map().put(FLD_PARAMS, new HashMap<>());
        }
        return (Map<String, Object>) super.map().get(FLD_PARAMS);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!super.map().has(FLD_PARAMS)) {
            super.map().put(FLD_PARAMS, new HashMap<>());
        } else {
            final Object params = super.map().get(FLD_PARAMS);
            if (!(params instanceof Map)) {

                if (params instanceof String) {
                    final String s_params = (String) params;
                    if (StringUtils.isJSONObject(s_params)) {
                        final JSONObject item = new JSONObject(s_params);
                        super.map().put(FLD_PARAMS, JsonWrapper.toMap(item));
                    } else if (!IConstants.STR_NULL.equalsIgnoreCase(s_params)) {
                        super.map().put(FLD_PARAMS, MapConverter.toMap(s_params));
                    }
                }
            }
        }
    }

}
