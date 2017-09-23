package org.lyj.ext.script.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.lang.Base64;
import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.JsonConverter;
import org.lyj.ext.script.program.engines.javascript.utils.JavascriptConverter;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 */
public class Converter {

    private static final String PREFIX_BASE_64 = "base64:";

    // ------------------------------------------------------------------------
    //                      f i l e s
    // ------------------------------------------------------------------------

    public static String toFileName(final String text) {
        return StringUtils.replace(text.toLowerCase(), " ", "_");
    }


    // ------------------------------------------------------------------------
    //                      b a s e 6 4
    // ------------------------------------------------------------------------

    public static String toBase64(final String value) {
        return toBase64(value, false);
    }

    public static String toBase64(final String value,
                                  final boolean force) {
        if (StringUtils.hasText(value)) {
            try {
                if (!value.startsWith(PREFIX_BASE_64) || force) {
                    return PREFIX_BASE_64 + Base64.encodeBytes(value.getBytes());
                }
            } catch (Throwable ignored) {

            }
        }
        return value;
    }

    public static String fromBase64(final String value) {
        return fromBase64(value, false);
    }

    public static String fromBase64(final String value,
                                    final boolean force) {
        if (StringUtils.hasText(value)) {
            try {
                if (value.startsWith(PREFIX_BASE_64) || force) {
                    final String clean = value.replace(PREFIX_BASE_64, "");
                    return new String(Base64.decode(clean));
                }
            } catch (Throwable ignored) {

            }
        }
        return value;
    }

    // ------------------------------------------------------------------------
    //                      j s o n i t e m
    // ------------------------------------------------------------------------

    /**
     * Always return json object or json array
     *
     * @param value an object
     * @return JSONObject or JSONArray
     */
    public static Object toJson(final Object value) {
        if (value instanceof JsonItem) {
            return ((JsonItem) value).json();
        } else {
            final Object compatible = toJsonCompatible(value);
            if (compatible instanceof JSONArray) {
                return compatible;
            } else if (compatible instanceof JSONObject) {
                return compatible;
            } else if (compatible instanceof String) {
                if (StringUtils.isJSON(compatible)) {
                    if (StringUtils.isJSONObject(compatible)) {
                        return new JSONObject((String) compatible);
                    } else {
                        return new JSONArray((String) compatible);
                    }
                }
            }
            // return array wrapping object
            final JSONArray array = new JSONArray();
            array.put(compatible);
            return array;
        }
    }

    /**
     * Always return JSONArray
     *
     * @param value Object to convert
     * @return JSONArray
     */
    public static JSONArray toJsonArray(final Object value) {
        final Object json = toJson(value);
        if (null != json) {
            if (json instanceof JSONArray) {
                return (JSONArray) json;
            } else if (value instanceof ScriptObjectMirror) {
                // recursive
                return toJsonArray(JavascriptConverter.toJSON((ScriptObjectMirror) value));
            } else {
                final JSONArray result = new JSONArray();
                result.put(json);
                return result;
            }
        }
        return new JSONArray();
    }

    public static JsonItem toJsonItem(final Object value) {
        if (value instanceof JsonItem) {
            return (JsonItem) value;
        } else if (value instanceof ScriptObjectMirror) {
            return new JsonItem(JavascriptConverter.forceJSONObject((ScriptObjectMirror) value));
        } else {
            return new JsonItem(value);
        }
    }

    public static Collection<JsonItem> toJsonItemCollection(final Object value) {
        final Collection<JsonItem> result = new LinkedList<>();
        if (value instanceof ScriptObjectMirror) {
            final JSONArray array = JavascriptConverter.forceJSONArray((ScriptObjectMirror) value);
            for (int i = 0; i < array.length(); i++) {
                result.add(toJsonItem(array.get(i)));
            }
        } else if (value instanceof Collection) {
            for (final Object item : (Collection) value) {
                result.add(toJsonItem(item));
            }
        }
        return result;
    }

    public static Object toJsonCompatible(final Object value) {
        Object result = JsonConverter.toJsonCompatible(value);

        if (null == result) {
            // try convert other types
            if (value instanceof ScriptObjectMirror) {
                result = JavascriptConverter.toJSON((ScriptObjectMirror) value);
            }
        }

        return result;
    }

    public static String[] toStringArray(final Object value) {
        try {
            final JSONArray array = toJsonArray(value);
            return null != array ? JsonWrapper.toArrayOfString(array) : new String[0];
        } catch (Throwable ignored) {

        }
        return new String[0];
    }

}
