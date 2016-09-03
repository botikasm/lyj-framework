package org.lyj.ext.script.program.engines.javascript.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

/**
 * Conversion utility
 */
public class JavascriptConverter {


    public static Object toJSON(final ScriptObjectMirror item) {
        if (null != item) {
            if (item.isArray()) {
                return toJSONArray(item);
            } else {
                return toJSONObject(item);
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static JSONObject toJSONObject(final ScriptObjectMirror item) {
        final JSONObject result = new JSONObject();
        final Set<String> keys = item.keySet();
        for (final String key : keys) {
            final Object value = item.get(key);
            if (value instanceof ScriptObjectMirror) {
                final ScriptObjectMirror svalue = (ScriptObjectMirror) value;
                if (svalue.isArray()) {
                    result.put(key, toJSONArray(svalue));
                } else {
                    result.put(key, toJSONObject(svalue));
                }
            } else {
                // primitive value
                result.put(key, value);
            }
        }
        return result;
    }

    private static JSONArray toJSONArray(final ScriptObjectMirror item) {
        final JSONArray result = new JSONArray();
        final Set<String> keys = item.keySet();
        for (final String key : keys) {
            final Object value = item.get(key);
            if (value instanceof ScriptObjectMirror) {
                final ScriptObjectMirror svalue = (ScriptObjectMirror) value;
                if (svalue.isArray()) {
                    result.put(toJSONArray(svalue));
                } else {
                    result.put(toJSONObject(svalue));
                }
            } else {
                // primitive value
                result.put(value);
            }
        }
        return result;
    }
}
