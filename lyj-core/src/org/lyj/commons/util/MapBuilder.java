package org.lyj.commons.util;

import org.json.JSONObject;

import java.util.*;

/**
 *
 */
public class MapBuilder<K, V> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private Map<K, V> _map;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MapBuilder() {
        _map = new LinkedHashMap<>();
    }

    public MapBuilder(final Map<K, V> data) {
        _map = new LinkedHashMap<>(data);
    }

    @Override
    public String toString() {
        return _map.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Map<K, V> toMap() {
        return _map;
    }

    public JSONObject toJSON() {
        return new JSONObject(_map);
    }

    public boolean has(final K key) {
        return _map.containsKey(key);
    }

    public MapBuilder<K, V> append(final K key, final V value) {
        return this.put(key, value);
    }

    public MapBuilder<K, V> put(final K key, final V value) {
        _map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putAll(final Map<K, V> map) {
        _map.putAll(map);
        return this;
    }

    public V get(final K key) {
        return _map.get(key);
    }

    public String getString(final K key) {
        return StringUtils.toString(_map.get(key));
    }

    public int getInteger(final K key) {
        return ConversionUtils.toInteger(_map.get(key));
    }

    public long getLong(final K key) {
        return ConversionUtils.toLong(_map.get(key));
    }

    public double getDouble(final K key) {
        return ConversionUtils.toDouble(_map.get(key));
    }

    public boolean getBoolean(final K key) {
        return ConversionUtils.toBoolean(_map.get(key));
    }

    public <T> Set<T> getSet(final K key) {
        final V value = _map.get(key);
        if (value instanceof Set) {
            return (Set<T>) value;
        }
        return new HashSet<>();
    }

    public <T> List<T> getList(final K key) {
        final V value = _map.get(key);
        if (value instanceof List) {
            return (List<T>) value;
        }
        return new ArrayList<>();
    }

    public <T> Collection<T> getCollection(final K key) {
        final V value = _map.get(key);
        if (value instanceof Collection) {
            return (Collection<T>) value;
        }
        return new ArrayList<>();
    }

    public <KK, VV> Map<KK, VV> getMap(final K key) {
        final V value = _map.get(key);
        if (value instanceof Map) {
            return (Map<KK, VV>) value;
        }
        return new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static <K, V> MapBuilder<K, V> create() {
        return new MapBuilder<K, V>();
    }

    public static <K, V> MapBuilder<K, V> create(final Map<K, V> data) {
        return new MapBuilder<K, V>(data);
    }

    public static <K, V> MapBuilder<K, V> create(final Class<K> keyClass, final Class<V> valueClass) {
        return new MapBuilder<K, V>();
    }

    public static MapBuilder<String, Object> createSO() {
        return new MapBuilder<String, Object>();
    }

    public static MapBuilder<String, String> createSS() {
        return new MapBuilder<String, String>();
    }

    public static MapBuilder<Integer, String> createIS() {
        return new MapBuilder<Integer, String>();
    }

}
