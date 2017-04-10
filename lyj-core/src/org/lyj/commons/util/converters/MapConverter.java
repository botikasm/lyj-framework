package org.lyj.commons.util.converters;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.BeanUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.JsonItem;

import java.util.*;

/**
 * Convert to map
 */
public abstract class MapConverter {

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Convert passed value into compatible value
     * @param value Value to convert
     * @return Primitive, String, Map or Collection
     */
    public static Object convert(final Object value) {
        if (null == value) {
            return "";
        }

        // check type
        if (BeanUtils.PrimitiveClasses.isPrimitive(value.getClass()) || value instanceof String) {
            return value;
        } else {
            // try conversion
            Object response = toMap(value);
            if (null == response) {
                response = toList(value);
            }
            if (null != response) {
                return response;
            }
        }

        return value.toString();
    }

    public static Map<String, Object> toMap(final Object value) {
        if (value instanceof Map) {
            // avoid map with invalid values
            final Map map = (Map) value;
            final Map<String, Object> response = new HashMap<>();
            map.forEach((key, val) -> {
                response.put(key.toString(), convert(val));
            });
            return response;
        } else if (value instanceof JSONObject) {
            return toMap((JSONObject) value);
        } else if (value instanceof JsonItem) {
            return toMap((JsonItem) value);
        }
        return null;
    }

    /**
     * Return list of Map or Primitive
     * @param value Value to convert
     * @return List of Primitive, String or Map
     */
    public static Collection toList(final Object value) {
        if (value instanceof JSONArray) {
            return toList((JSONArray) value);
        } else if (value instanceof Collection) {
            return toList((Collection) value);
        } else if (value.getClass().isArray()) {
            return toList((Object[]) value);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static Map<String, Object> toMap(final JsonItem json) {
        return toMap(json.json());
    }

    private static Map<String, Object> toMap(final JSONObject json) {
        final Map<String, Object> response = new LinkedHashMap<>();
        final Iterator keys = json.keys();
        while (keys.hasNext()) {
            final String name = keys.next().toString();
            if (null != name) {
                final Object value = json.opt(name);
                if (value instanceof JSONObject) {
                    response.put(name, toMap((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    response.put(name, toList((JSONArray) value));
                } else {
                    response.put(name, null != value ? value : "");
                }
            }
        }
        return response;
    }

    private static Collection toList(final JSONArray array) {
        final Collection response = new LinkedList();
        CollectionUtils.forEach(array, (item) -> {
            if (null != item) {
                response.add(convert(item));
            }
        });
        return response;
    }

    private static Collection toList(final Object[] array) {
        final Collection response = new LinkedList();
        for (final Object item : array) {
            if (null != item) {
                response.add(convert(item));
            }
        }
        return response;
    }

    private static Collection toList(final Collection array) {
        final Collection response = new LinkedList();
        array.forEach((item) -> {
            if (null != item) {
                response.add(convert(item));
            }
        });
        return response;
    }


}
