package org.lyj.ext.netty.client.web;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.JsonItem;

import java.nio.charset.Charset;

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


}
