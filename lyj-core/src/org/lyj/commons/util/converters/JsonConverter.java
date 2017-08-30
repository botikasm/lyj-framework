package org.lyj.commons.util.converters;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.lang.Base64;
import org.lyj.commons.util.BeanUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * JSON conversion utility methods.
 * <p>
 * byte[] (array of bytes like images of binary data) are converted in BASE64 and the value is prefixed with "data:".
 */
public abstract class JsonConverter {

    // ------------------------------------------------------------------------
    //               c o n s t
    // ------------------------------------------------------------------------

    public static final String PREFIX_DATA = "data:"; // add this prefix to byte[] converted in base64

    // ------------------------------------------------------------------------
    //               c o n s t r u c t or
    // ------------------------------------------------------------------------

    private JsonConverter() {
    }

    // ------------------------------------------------------------------------
    //               p u b l i c
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
                        return toObject((String) compatible);
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
     * Return a JSON compatible value: Primitive, JSONObject, JSONArray
     *
     * @param value Value to convert
     * @return JSON compatible value
     */
    public static Object toJsonCompatible(final Object value) {
        if (null != value) {
            if (value instanceof JSONArray || value instanceof JSONObject) {
                return value;
            } else if (value instanceof JsonItem) {
                return ((JsonItem) value).json();
            } else if (value.getClass().isArray()) {
                return fromArray(value);
            } else if (value instanceof Collection) {
                return toArray((Collection) value);
            } else if (value instanceof Map) {
                return toObject((Map) value);
            } else if (value instanceof String) {
                try {
                    if (StringUtils.isJSONArray(value)) {
                        return new JSONArray(value.toString());
                    } else if (StringUtils.isJSONObject(value)) {
                        return new JSONObject(value.toString());
                    }
                } catch (Throwable ignored) {
                    // malformed json like string
                }
                return value;
            } else if (BeanUtils.PrimitiveClasses.isPrimitive(value.getClass())) {
                return value;
            }
        }

        return null; // not compatible
    }

    public static JSONArray toArray(final Object[] values) {
        return fromObjectArray(values);
    }

    public static JSONArray toArray(final Collection values) {
        final JSONArray result = new JSONArray();
        for (final Object obj : values) {
            final Object item = toJsonCompatible(obj);
            if (null != item) {
                result.put(item);
            }
        }
        return result;
    }

    public static JSONObject toObject(final Map<?, ?> map) {
        final JSONObject result = new JSONObject();
        map.forEach((key, value) -> {
            final Object item = toJsonCompatible(value);
            if (null != item) {
                result.put(key.toString(), item);
            }
        });
        return result;
    }

    public static JSONObject toObject(final String value) {
        final JSONObject result = new JSONObject();
        if (StringUtils.isJSONObject(value)) {
            try {
                // try with json directly
                return new JSONObject(value);
            } catch (Throwable ignored) {
                // may be a map
                return toObject(CollectionUtils.stringToMap(value.substring(1, value.length()-1)));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    //               binary converted data
    // ------------------------------------------------------------------------

    public static boolean isBinary(final Object value) {
        return (value instanceof String) && ((String) value).startsWith(PREFIX_DATA);
    }

    public static Object decodeIfBinary(final Object value) {
        if (isBinary(value)) {
            final String base64 = StringUtils.replace((String) value, PREFIX_DATA, "");
            return Base64.decode(base64);
        }
        return value;
    }

    // ------------------------------------------------------------------------
    //               p r i v a t e
    // ------------------------------------------------------------------------

    private static boolean isByteArray(final Object array) {
        return array.getClass().equals(byte[].class);
    }

    private static Object fromArray(final Object array) {
        return isByteArray(array) ? fromByteArray((byte[]) array) : fromObjectArray((Object[]) array);
    }

    private static String fromByteArray(final byte[] bytes) {
        return PREFIX_DATA + Base64.encodeBytes(bytes);
    }

    private static JSONArray fromObjectArray(final Object[] array) {
        final JSONArray result = new JSONArray();
        for (final Object obj : array) {
            final Object item = toJsonCompatible(obj);
            if (null != item) {
                result.put(item);
            }
        }
        return result;
    }

}
