package org.lyj.commons.util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public MapBuilder(){
        _map = new LinkedHashMap<>();
    }

    @Override
    public String toString() {
        return _map.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Map<K, V> toMap(){
        return _map;
    }

    public JSONObject toJSON(){
        return new JSONObject(_map);
    }

    public MapBuilder append(final K key, final V value){
        _map.put(key, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static <K,V> MapBuilder<K,V> create(){
        return new MapBuilder<K,V>();
    }

}
