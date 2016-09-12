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
                return toArray(item);
            } else {
                return toObject(item);
            }
        }
        return null;
    }

    public static JSONArray forceJSONArray(final ScriptObjectMirror item) {
        if (null != item) {
            if (item.isArray()) {
                return toArray(item);
            } else {
                final JSONArray array = new JSONArray();
                array.put(toObject(item));
                return array;
            }
        }
        return new JSONArray();
    }

    public static JSONObject forceJSONObject(final ScriptObjectMirror item) {
        if (null != item) {
            if (!item.isArray()) {
                return toObject(item);
            }
        }
        return new JSONObject();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static JSONObject toObject(final ScriptObjectMirror item) {
        final JSONObject result = new JSONObject();
        final Set<String> keys = item.keySet();
        for (final String key : keys) {
            final Object value = item.get(key);
            if (value instanceof ScriptObjectMirror) {
                final ScriptObjectMirror svalue = (ScriptObjectMirror) value;
                if (svalue.isArray()) {
                    result.put(key, toArray(svalue));
                } else {
                    result.put(key, toObject(svalue));
                }
            } else {
                // primitive value
                result.put(key, value);
            }
        }
        return result;
    }

    private static JSONArray toArray(final ScriptObjectMirror item) {
        final JSONArray result = new JSONArray();
        final Set<String> keys = item.keySet();
        for (final String key : keys) {
            final Object value = item.get(key);
            if (value instanceof ScriptObjectMirror) {
                final ScriptObjectMirror svalue = (ScriptObjectMirror) value;
                if (svalue.isArray()) {
                    result.put(toArray(svalue));
                } else {
                    result.put(toObject(svalue));
                }
            } else {
                // primitive value
                result.put(value);
            }
        }
        return result;
    }
}
