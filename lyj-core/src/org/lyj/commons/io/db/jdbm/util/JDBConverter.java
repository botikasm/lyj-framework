package org.lyj.commons.io.db.jdbm.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.BeanUtils;
import org.lyj.commons.util.ClassLoaderUtils;

import java.util.*;

/**
 * Conversion utility
 */
public class JDBConverter {

    public static Map<String, Object> toMap(final JSONObject item) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        final Iterator keys = item.keys();
        while (keys.hasNext()) {
            final String name = keys.next().toString();
            if (null != name) {
                final Object value = item.opt(name);
                if (value instanceof JSONObject) {
                    result.put(name, toMap((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    result.put(name, toList((JSONArray) value));
                } else {
                    result.put(name, null != value ? value : "");
                }
            }
        }
        return result;
    }

    public static List<Object> toList(final JSONArray array) {
        final List<Object> result = new LinkedList<>();
        for (int i = 0; i < array.length(); i++) {
            final Object value = array.get(i);
            if (value instanceof Map) {
                result.add(value);
            } else if (value instanceof JSONObject) {
                result.add(toMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                result.add(toList((JSONArray) value));
            } else {
                // is primitive?
                if(BeanUtils.isPrimitiveClass(value)) {
                    result.add(value);
                } else {
                    // this value is not serializable

                }
            }
        }
        return result;
    }

}
