package org.lyj.ext.db.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class MapDocumentWrapper {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MapDocument _document;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MapDocumentWrapper() {
        _document = new MapDocument();
    }

    public MapDocumentWrapper(final MapDocument document) {
        _document = document;
    }

    @Override
    public String toString() {
        return _document.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public MapDocument mapDocument() {
        return _document;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject json() {
        return _document.json();
    }

    public boolean has(final String name) {
        return _document.has(name);
    }

    public MapDocumentWrapper put(final String key, final Object value) {
        _document.put(key, value);
        return this;
    }

    public void putAll(final Map<? extends String, ?> item) {
        _document.putAll(item);
    }

    public void putAll(final Map<? extends String, ?> item, final boolean only_existing_fields) {
        _document.putAll(item, only_existing_fields);
    }

    public void putAll(final JSONObject item) {
        _document.putAll(item);
    }

    public void putAll(final JSONObject item, final boolean only_existing_fields) {
        _document.putAll(item, only_existing_fields);
    }

    public MapDocumentWrapper putNotEmpty(final String key, final Object value) {
        _document.putNotEmpty(key, value);
        return this;
    }


    public MapDocumentWrapper putAllNotEmpty(final JSONObject values,
                                             final boolean only_existing_fields,
                                             final String[] exclude_fields) {
        _document.putAllNotEmpty(values, only_existing_fields, exclude_fields);
        return this;
    }

    public String getString(final String name) {
        return getString(name, "");
    }

    public String getString(final String name, final String def_val) {
        return _document.getString(name, def_val);
    }

    public long getLong(final String name) {
        return this.getLong(name, 0L);
    }

    public long getLong(final String name, final long def_val) {
        return _document.getLong(name, def_val);
    }

    public int getInt(final String name) {
        return this.getInt(name, 0);
    }

    public int getInt(final String name, final int def_val) {
        return _document.getInt(name, def_val);
    }

    public double getDouble(final String name) {
        return this.getDouble(name, 0.0);
    }

    public double getDouble(final String name, final double def_val) {
        return _document.getDouble(name, def_val);
    }

    public double getDouble(final String name, final int decimal_place, final double def_val) {
        return _document.getDouble(name, decimal_place, def_val);
    }

    public boolean getBoolean(final String name) {
        return this.getBoolean(name, false);
    }

    public boolean getBoolean(final String name, final boolean def_val) {
        return _document.getBoolean(name, def_val);
    }

    public MapDocument getMap(final String name) {
        return this.getMap(name, true);
    }

    public MapDocument getMap(final String name,
                              final boolean auto_create) {
        return _document.getMap(name, auto_create);
    }

    public JSONObject getJSONObject(final String name) {
        return this.getJSONObject(name, true);
    }

    public JSONObject getJSONObject(final String name,
                                    final boolean auto_create) {
        return _document.getJSONObject(name, auto_create);
    }

    public JSONArray getJSONArray(final String name) {
        return this.getJSONArray(name, true);
    }

    public JSONArray getJSONArray(final String name,
                                  final boolean auto_create) {
        return _document.getJSONArray(name, auto_create);
    }

    public Collection getList(final String name) {
        return this.getList(name, true);
    }

    public Collection getList(final String name,
                              final boolean auto_create) {
        return _document.getList(name, auto_create);
    }


}
