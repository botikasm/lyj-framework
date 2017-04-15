package org.lyj.ext.db.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.*;
import org.lyj.commons.util.converters.JsonConverter;
import org.lyj.commons.util.converters.MapConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class MapDocument
        extends HashMap<String, Object> {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MapDocument() {
        super();
        this.init();
    }

    public MapDocument(final Object value) {
        if (value instanceof MapDocument) {
            this.putAll((MapDocument) value);
        } else if (value instanceof Map) {
            this.putAll((Map) value);
        } else {
            final JsonItem jitem = new JsonItem(value);
            merge(jitem, this);
        }
        this.init();
    }

    public MapDocument(final String value) {
        if (StringUtils.isJSONObject(value)) {
            final JsonItem jitem = new JsonItem(value);
            merge(jitem, this);
        }
        this.init();
    }

    @Override
    public String toString() {
        return this.json().toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject json() {
        return toJson(this);
    }

    public boolean has(final String name) {
        return super.containsKey(name);
    }

    public MapDocument put(final String key, final Object value) {
        super.put(key, MapConverter.convert(value));
        return this;
    }

    @Override
    public void putAll(final Map<? extends String, ?> item) {
        final Set<? extends String> keys = item.keySet();
        for (final String key : keys) {
            this.put(key, item.get(key));
        }
    }

    public void putAll(final Map<? extends String, ?> item, final boolean only_existing_fields) {
        final Set<? extends String> keys = item.keySet();
        for (final String key : keys) {
            if (!only_existing_fields || this.has(key)) {
                this.put(key, item.get(key));
            }
        }
    }

    public void putAll(final JSONObject item) {
        final Set<String> keys = item.keySet();
        for (final String key : keys) {
            this.put(key, item);
        }
    }

    public void putAll(final JSONObject item, final boolean only_existing_fields) {
        final Set<String> keys = item.keySet();
        for (final String key : keys) {
            if (!only_existing_fields || this.has(key)) {
                this.put(key, item.get(key));
            }
        }
    }

    public MapDocument putNotEmpty(final String key, final Object value) {
        if (null != value && StringUtils.hasText(value.toString())) {
            this.put(key, value);
        }
        return this;
    }


    public MapDocument putAllNotEmpty(final JSONObject values,
                                      final boolean only_existing_fields,
                                      final String[] exclude_fields) {
        final Set<String> keys = values.keySet();
        for (final String key : keys) {
            if (!only_existing_fields || this.has(key)) {
                if (!CollectionUtils.contains(exclude_fields, key)) {
                    final Object value = values.opt(key);
                    if (null != value) {
                        this.put(key, value);
                    }
                }
            }
        }
        return this;
    }

    public String getString(final String name) {
        return getString(name, "");
    }

    public String getString(final String name, final String def_val) {
        final String response = StringUtils.toString(super.get(name));
        return StringUtils.hasText(response) ? response : def_val;
    }

    public long getLong(final String name) {
        return this.getLong(name, 0L);
    }

    public long getLong(final String name, final long def_val) {
        return ConversionUtils.toLong(super.get(name), def_val);
    }

    public int getInt(final String name) {
        return this.getInt(name, 0);
    }

    public int getInt(final String name, final int def_val) {
        return ConversionUtils.toInteger(super.get(name), def_val);
    }

    public double getDouble(final String name) {
        return this.getDouble(name, 0.0);
    }

    public double getDouble(final String name, final double def_val) {
        return ConversionUtils.toDouble(super.get(name), 3, def_val);
    }

    public double getDouble(final String name, final int decimal_place, final double def_val) {
        return ConversionUtils.toDouble(super.get(name), decimal_place, def_val);
    }

    public boolean getBoolean(final String name) {
        return this.getBoolean(name, true);
    }

    public boolean getBoolean(final String name, final boolean def_val) {
        return ConversionUtils.toBoolean(super.get(name), def_val);
    }

    public MapDocument getMap(final String name) {
        return this.getMap(name, true);
    }

    public MapDocument getMap(final String name,
                              final boolean auto_create) {
        final Object response = super.get(name);
        final MapDocument item;
        if (null != response) {
            if(response instanceof MapDocument){
                item = (MapDocument)response;
            }else if (response instanceof Map) {
                item = new MapDocument(response);
            } else {
                // convert to json document
                super.put(name, new MapDocument(response));
                item = (MapDocument) super.get(name);
            }
        } else if (auto_create) {
            super.put(name, new MapDocument());
            item = (MapDocument) super.get(name);
        } else {
            return null;
        }
        super.put(name, item);
        
        return item;
    }

    public JSONObject getJSONObject(final String name) {
        return this.getJSONObject(name, true);
    }

    public JSONObject getJSONObject(final String name,
                                    final boolean auto_create) {
        return JsonConverter.toObject(this.getMap(name, auto_create));
    }

    public JSONArray getJSONArray(final String name) {
        return this.getJSONArray(name, true);
    }

    public JSONArray getJSONArray(final String name,
                                  final boolean auto_create) {
        final Collection response = this.getList(name, auto_create);
        return JsonConverter.toArray(response);
    }

    public Collection getList(final String name) {
        return this.getList(name, true);
    }

    public Collection getList(final String name,
                              final boolean auto_create) {
        final Object response = super.get(name);
        final MapList list;

        if (null != response) {
            list = new MapList(MapConverter.toList(response));
        } else if (auto_create) {
            super.put(name, new MapList());
            list = (MapList) super.get(name);
        } else {
            return null;
        }

        // replace list with new instance
        super.put(name, list);

        return list;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static String uuid() {
        return RandomUtils.randomUUID(true);
    }

    public static JSONObject toJson(final MapDocument document) {
        return new JSONObject(document);
    }

    public static void merge(final MapDocument source,
                             final JsonItem target) {
        merge(source, target.json());
    }

    public static void merge(final MapDocument source,
                             final JSONObject target) {
        source.forEach(target::put);
    }

    public static void merge(final JsonItem source,
                             final MapDocument target) {
        final Set<String> keys = source.keys();
        for (final String name : keys) {
            final Object value = source.get(name);
            if (null != value) {
                target.put(name, value);
            }
        }
    }


}
