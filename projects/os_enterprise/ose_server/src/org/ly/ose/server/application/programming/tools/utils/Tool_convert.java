package org.ly.ose.server.application.programming.tools.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.program.engines.javascript.EngineJavascript;
import org.lyj.ext.script.program.engines.javascript.utils.JavascriptConverter;
import org.lyj.ext.script.program.engines.javascript.utils.JavascriptUtils;
import org.lyj.ext.script.utils.Converter;

/**
 *
 */
public class Tool_convert
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "convert"; // used as $convert.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_convert(final OSEProgram program) {
        super(NAME, program);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    public int toInt(final Object value) {
        return this.toInt(value, 0);
    }

    public int toInt(final Object value, final Object default_value) {
        return ConversionUtils.toInteger(value, ConversionUtils.toInteger(default_value));
    }

    public long toLong(final Object value) {
        return this.toLong(value, 0L);
    }

    public long toLong(final Object value, final Object default_value) {
        return ConversionUtils.toLong(value, ConversionUtils.toLong(default_value));
    }

    public double toDouble(final Object value) {
        return this.toDouble(value, 0.0);
    }

    public double toDouble(final Object value, final Object default_value) {
        return ConversionUtils.toDouble(value, ConversionUtils.toDouble(default_value));
    }

    public boolean toBoolean(final Object value) {
        return this.toBoolean(value, false);
    }

    public boolean toBoolean(final Object value, final Object default_value) {
        return ConversionUtils.toBoolean(value, ConversionUtils.toBoolean(default_value));
    }

    public Object toArray(final Object value) {
        if (null != value && !JavascriptUtils.isUndefined(value)) {
            final JSONArray array = Converter.toJsonArray(value);
            return eval(array.toString());
        }
        return eval("[]");
    }

    public Object toObject(final Object value) {
        if (null != value) {
            final JSONObject object = Converter.toJsonObject(value);
            return eval(object.toString());
        }
        return value;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Object eval(final String text) {
        try {
            if (StringUtils.isJSONArray(text)) {
                return EngineJavascript.engine(true).eval(text.trim());
            }
            return JavascriptConverter.toScriptObject(text);
        } catch (Throwable ignored) {
            // ignored
        }
        return false;
    }


}
