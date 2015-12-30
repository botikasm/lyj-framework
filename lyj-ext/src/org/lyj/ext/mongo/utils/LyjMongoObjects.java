package org.lyj.ext.mongo.utils;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.mongo.ILyjMongoConstants;
import org.lyj.ext.mongo.model.LyjGeoJSON;

import java.util.*;

/**
 * Utility methods working on Bson documents
 */
public class LyjMongoObjects {

    private static final String EMPTY_ITEM = "{}"; // "{\"_id\":0}"
    private static final String EMPTY_LIST = "[]"; // "[]"


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

    public static boolean isEmpty(final Bson bson) {
        if (bson instanceof Document) {
            return ((Document) bson).size() == 0;
        } else {
            return true;
        }
    }

    public static Bson toBson(final JSONObject item) {
        return toDocument(item);
    }

    public static Bson toBson(final Map<String, Object> item) {
        final JSONObject json = new JSONObject(item);
        return null != item ? Document.parse(json.toString()) : null;
    }

    public static List<Bson> toBsonList(final JSONArray array) {
        final List<Bson> list = new LinkedList<>();
        CollectionUtils.map(array, (item, index, key) -> {
            if (item instanceof JSONObject) {
                list.add(toBson((JSONObject) item));
            }
            return null;
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

    public static Document toDocument(final Object obj) {
        if (obj instanceof Document) {
            return (Document) obj;
        } else if (obj instanceof JsonWrapper) {
            return Document.parse(((JsonWrapper) obj).getJSONObject().toString());
        } else if (obj instanceof JSONObject) {
            return Document.parse(obj.toString());
        } else if (null != obj) {
            final String sobj = obj.toString();
            if (StringUtils.isJSONObject(sobj)) {
                return Document.parse(sobj);
            }
        }
        return new Document();
    }

    public static String toJson(final Object obj) {
        return toJson(obj, "");
    }

    public static String toJson(final Object obj, final String def) {
        if (null != obj) {
            if (obj instanceof Document) {
                return ((Document) obj).toJson();
            } else if (obj instanceof Collection) {
                final Collection list = (Collection) obj;
                final JSONArray jarray = new JSONArray(list);
                return jarray.toString();
            } else if (obj.getClass().isArray()) {
                return new JSONArray(obj).toString();
            } else {
                return obj.toString();
            }
        } else {
            return null != def ? def : EMPTY_ITEM;
        }
    }

    public static List getArray(final Document document, final String key) {
        return getArray(document, key, false);
    }

    public static List getArray(final Document document, final String key,
                                final boolean addIfNone) {
        final Object value = document.get(key);
        if (null == value || !(value instanceof List)) {
            if (addIfNone) {
                document.put(key, new ArrayList<>());
                return (List) document.get(key);
            } else {
                return new ArrayList<>();
            }
        } else {
            return (List) value;
        }
    }

    public static List<String> getArrayOfString(final Document document, final String key) {
        return getArrayOfString(document, key, false);
    }

    public static List<String> getArrayOfString(final Document document, final String key,
                                                final boolean addIfNone) {
        if (null != document) {
            final Object value = document.get(key);
            if (null == value || !(value instanceof List)) {
                if (addIfNone) {
                    document.put(key, new ArrayList<>());
                    return (List<String>) document.get(key);
                } else {
                    return new ArrayList<>();
                }
            } else {
                return (List<String>) value;
            }
        } else {
            return new ArrayList<>();
        }
    }

    public static List<Document> getArrayOfDocument(final Document document, final String key) {
        return getArrayOfDocument(document, key, false);
    }

    public static List<Document> getArrayOfDocument(final Document document, final String key,
                                                    final boolean addIfNone) {
        if (null != document) {
            final Object value = document.get(key);
            if (null == value || !(value instanceof List)) {
                if (addIfNone) {
                    document.put(key, new ArrayList<>());
                    return (List<Document>) document.get(key);
                } else {
                    return new ArrayList<>();
                }
            } else {
                return (List<Document>) value;
            }
        } else {
            return new ArrayList<>();
        }
    }

    public static int getInteger(final Document document, final String key) {
        return getInteger(document, key, 0);
    }

    public static int getInteger(final Document document, final String key, final int defVal) {
        return null != document ? ConversionUtils.toInteger(document.get(key), defVal) : defVal;
    }

    public static long getLong(final Document document, final String key) {
        return getLong(document, key, 0L);
    }

    public static long getLong(final Document document, final String key, final long defVal) {
        if (null != document) {
            final Object value = document.get(key);
            if (value instanceof Document) {
                return ConversionUtils.toLong(((Document) value).get(ILyjMongoConstants.$NUMBER_LONG), defVal);
            } else {
                return ConversionUtils.toLong(document.get(key), defVal);
            }
        } else {
            return defVal;
        }
    }

    public static double getDouble(final Document document, final String key) {
        return ConversionUtils.toDouble(document.get(key));
    }

    public static double getDouble(final Document document, final String key, final double defVal) {
        return ConversionUtils.toDouble(document.get(key), defVal);
    }

    public static double getDouble(final Document document, final String key, final int decimalPlace, final double defVal) {
        return null != document ? ConversionUtils.toDouble(document.get(key), decimalPlace, defVal) : defVal;
    }

    public static String getString(final Document document, final String key) {
        return getString(document, key, "");
    }

    public static String getString(final Document document, final String key, final String defVal) {
        return null != document ? ConversionUtils.toString(document.get(key), defVal) : defVal;
    }

    public static boolean getBoolean(final Document document, final String key) {
        return ConversionUtils.toBoolean(document.get(key));
    }

    public static boolean getBoolean(final Document document, final String key, final boolean defVal) {
        return null != document ? ConversionUtils.toBoolean(document.get(key), defVal) : defVal;
    }

    public static Document extend(final Document target, final Document source) {
        return extend(target, source, false);
    }

    public static Document extend(final Document target, final Document source, final boolean overwrite) {
        if (null != target && null != source) {
            final Set<String> keys = source.keySet();
            for (final String key : keys) {
                if (overwrite || !target.containsKey(key)) {
                    target.put(key, source.get(key));
                }
            }
        }
        return target;
    }
}
