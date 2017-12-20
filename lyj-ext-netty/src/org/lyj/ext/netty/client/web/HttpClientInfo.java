package org.lyj.ext.netty.client.web;

import org.json.JSONObject;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.ext.netty.server.web.IHeaderNames;
import org.lyj.ext.netty.server.web.IHeaderValues;

import java.util.Map;
import java.util.Set;

/**
 * Configuration for http client
 */
public class HttpClientInfo
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_METHOD = "method";
    private static final String FLD_URL = "url";
    private static final String FLD_ENCODING = "encoding";
    private static final String FLD_BODY = "body";
    private static final String FLD_HEADERS = "headers";


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClientInfo() {
        super();
        this.encoding(CharEncoding.UTF_8);
    }

    public HttpClientInfo(final String item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String encoding() {
        return super.getString(FLD_ENCODING);
    }

    public HttpClientInfo encoding(final String value) {
        super.put(FLD_ENCODING, value);
        return this;
    }

    public String method() {
        return super.getString(FLD_METHOD);
    }

    public HttpClientInfo method(final String value) {
        super.put(FLD_METHOD, value);
        return this;
    }

    public String url() {
        return super.getString(FLD_URL);
    }

    public HttpClientInfo url(final String value) {
        super.put(FLD_URL, value);
        return this;
    }


    public Object body() {
        return super.get(FLD_BODY);
    }

    public HttpClientInfo body(final Object value) {
        super.put(FLD_BODY, value);

        if (StringUtils.isJSON(value)) {
            this.headers().put(IHeaderNames.CONTENT_TYPE, IHeaderValues.APPLICATION_JSON);
        }

        return this;
    }

    public JSONObject headers() {
        if (!super.has(FLD_HEADERS)) {
            super.put(FLD_HEADERS, new JSONObject());
        }
        return super.getJSONObject(FLD_HEADERS);
    }

    public HttpClientInfo headers(final Map<String, ?> values) {
        if(null!=values && !values.isEmpty()){
            final Set<String> keys = values.keySet();
            for (final String key : keys) {
                this.headers().put(key, values.get(key));
            }
        }
        return this;
    }

}
