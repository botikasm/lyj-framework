package org.lyj.ext.netty.server.web;

import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Params helper class
 */
public class HttpParams
        extends LinkedHashMap<String, Object> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private HttpServerRequest _request;  // access to request is late initialized to avoid read of all params
    private boolean _initialized;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpParams() {
        _initialized = true;
    }

    public HttpParams(final Map<String, Object> params) {
        super(params);
        _initialized = true;
    }

    public HttpParams(final HttpServerRequest request) {
        _request = request;
        _initialized = false;
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    @Override
    public boolean containsKey(Object key) {
        this.init();
        return super.containsKey(key);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpParams initialize(){
        this.init();
        return this;
    }

    public boolean initialized(){
        return _initialized;
    }

    public String toQueryString() {
        final StringBuilder result = new StringBuilder();
        final Set<String> keys = this.keySet();
        for (final String key : keys) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(key).append("=").append(this.getString(key));
        }
        return result.toString();
    }

    public Map<String, Object> toMap(){
        final Map<String, Object> result = new HashMap<>();
        final Set<String> keys = this.keySet();
        for (final String key : keys) {
            result.put(key, this.getString(key));
        }
        return result;
    }

    public String getString(final String name) {
        this.init();
        return StringUtils.toString(this.get(name));
    }

    public int getInteger(final String name) {
        this.init();
        return ConversionUtils.toInteger(this.get(name));
    }

    public boolean getBoolean(final String name) {
        this.init();
        return ConversionUtils.toBoolean(this.get(name));
    }

    public double getDouble(final String name) {
        this.init();
        return ConversionUtils.toDouble(this.get(name));
    }

    public long getLong(final String name) {
        this.init();
        return ConversionUtils.toLong(this.get(name));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // access request only when needed
        if (null != _request && !_initialized) {
            _initialized = true;
            super.putAll(_request.params());
        }
    }

}


