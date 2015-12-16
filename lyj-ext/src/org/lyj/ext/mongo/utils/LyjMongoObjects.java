package org.lyj.ext.mongo.utils;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.*;

/**
 * Utility methods working on Bson documents
 */
public class LyjMongoObjects {

    /**
     * Returns a list of Bson objects
     *
     * @param args Bson objects
     * @return List of Bson Objects
     */
    public static List<Bson> asList(final Bson... args) {
        return Arrays.asList(args);
    }

    /**
     * Generate a list of Bson objects, each with same key.
     * This method is useful to create a list of values for or condition.
     * Sample:
     * <code>
     * asListOneKey("user_id", "", null, "1234")
     * </code>
     *
     * @param key    Unique key for all objects
     * @param values Values
     * @return List of Bson Objects. All objects have same key
     */
    public static List<Bson> asListOneKey(final String key, final Object... values) {
        final ArrayList<Bson> list = new ArrayList<>();
        for (Object value : values) {
            list.add(new Document(key, value));
        }
        return list;
    }

    /**
     * Sample:
     * <code>
     * asListPairs("key1", value1, "key2", value2, ...)
     * </code>
     *
     * @param pairs a pair list of key-value.
     * @return List of Bson Objects
     */
    public static List<Bson> asListPairs(final Object... pairs) {
        final ArrayList<Bson> list = new ArrayList<>();
        final int len = pairs.length;
        for (int i = 0; i < len - 1; i += 2) {
            String key = StringUtils.toString(pairs[i]);
            Object value = pairs[i + 1];
            if (StringUtils.hasText(key)) {
                list.add(new Document(key, value));
            }
        }
        return list;
    }

    public static Bson toBson(final JSONObject item) {
        return null!=item ? Document.parse(item.toString()) : null;
    }

    public static Bson toBson(final Map<String, Object> item) {
        final JSONObject json = new JSONObject(item);
        return null!=item ? Document.parse(json.toString()) : null;
    }

    public static List<Bson> toBsonList(final JSONArray array) {
        final List<Bson> list = new LinkedList<>();
        CollectionUtils.map(array, new CollectionUtils.IterationResponseCallback() {
            @Override
            public Object handle(Object item, int index, Object key) {
                if (item instanceof JSONObject) {
                    list.add(toBson((JSONObject) item));
                }
                return null;
            }
        });
        return list;
    }

    public static Document asDocument(final Object... pairs) {
        final Document result = new Document();
        final int len = pairs.length;
        for (int i = 0; i < len - 1; i += 2) {
            String key = StringUtils.toString(pairs[i]);
            Object value = pairs[i + 1];
            if (StringUtils.hasText(key)) {
                result.put(key, value);
            }
        }
        return result;
    }

}
