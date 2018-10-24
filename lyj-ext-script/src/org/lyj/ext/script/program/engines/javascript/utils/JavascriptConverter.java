package org.lyj.ext.script.program.engines.javascript.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.StringEscapeUtils;
import org.lyj.ext.script.program.engines.javascript.EngineJavascript;

import java.util.Map;
import java.util.Set;

/**
 * Conversion utility
 */
public class JavascriptConverter {

    public static Object toJSON(final Object item) {
        if (item instanceof ScriptObjectMirror) {
            return toJSON((ScriptObjectMirror) item);
        }
        return null;
    }

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

    public static JSONArray forceJSONArray(final Object item) {
        if (item instanceof ScriptObjectMirror) {
            return forceJSONArray((ScriptObjectMirror) item);
        }
        return new JSONArray();
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

    public static Object toScriptObject(final Object item) throws Exception {
        if (null != item) {
            final String raw_text = item.toString();
            final String text = StringEscapeUtils.escapeJavaScript(raw_text);
            final Object response = EngineJavascript.engine(true).eval("JSON.parse(\"" + text + "\")");
            return null != response ? response : false;
        }
        return false;
    }

    public static String toString(final Object item) throws Exception {
        if (null != item) {
            final Object json = toJSON(item);
            return null != json ? json.toString() : null;
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static JSONObject toObject(final ScriptObjectMirror item) {
        final JSONObject result = new JSONObject();
        if (!JavascriptUtils.isError(item)) {
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
        } else {
            // error
            result.put("type", "error");
            result.put("message", item.get("message"));
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
