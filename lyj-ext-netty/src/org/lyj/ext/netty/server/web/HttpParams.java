package org.lyj.ext.netty.server.web;

import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Params helper class
 */
public class HttpParams
        extends LinkedHashMap<String, Object> {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpParams() {

    }

    public HttpParams(final Map<String, Object> params) {
        super(params);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String getString(final String name) {
        return StringUtils.toString(this.get(name));
    }

    public int getInteger(final String name) {
        return ConversionUtils.toInteger(this.get(name));
    }

    public boolean getBoolean(final String name) {
        return ConversionUtils.toBoolean(this.get(name));
    }

    public double getDouble(final String name) {
        return ConversionUtils.toDouble(this.get(name));
    }

    public long getLong(final String name) {
        return ConversionUtils.toLong(this.get(name));
    }


}
