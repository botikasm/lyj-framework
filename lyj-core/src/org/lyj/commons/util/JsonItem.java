package org.lyj.commons.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lyj.commons.logging.AbstractLogEmitter;

import java.util.*;

/**
 * JSON wrapped object
 */
public class JsonItem
        extends AbstractLogEmitter
        implements Cloneable {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonWrapper _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JsonItem() {
        _data = new JsonWrapper(new JSONObject());
    }

    public JsonItem(final String json_text) {
        _data = StringUtils.isJSONObject(json_text) ? new JsonWrapper(json_text) : new JsonWrapper(new JSONObject());
    }

    public JsonItem(final JSONObject item) {
        _data = new JsonWrapper(null != item ? item : new JSONObject());
    }

    public JsonItem(final JsonItem item) {
        _data = (null != item) ? item._data : new JsonWrapper(new JSONObject());
    }

    public JsonItem(final Map<String, ?> item) {
        _data = null != item ? new JsonWrapper(new JSONObject(item)) : new JsonWrapper(new JSONObject());
    }

    public JsonItem(final Properties item) {
        _data = null != item ? new JsonWrapper(new JSONObject(item)) : new JsonWrapper(new JSONObject());
    }

    public JsonItem(final Object item) {
        if (item instanceof String) {
            _data = StringUtils.isJSONObject((String) item) ? new JsonWrapper((String) item) : new JsonWrapper(new JSONObject());
        } else if (item instanceof JSONObject) {
            _data = new JsonWrapper((JSONObject) item);
        } else if (item instanceof JsonItem) {
            _data = ((JsonItem) item)._data;
        } else if (item instanceof Properties ){
            _data = new JsonWrapper(new JSONObject((Properties)item));
        } else {
            _data = new JsonWrapper(new JSONObject());
        }
    }

    @Override
    public String toString() {
        return _data.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public JsonItem clone() {
        return new JsonItem(this.toString());
    }

    public Set<String> keys() {
        return _data.keys();
    }

    public JSONObject json() {
        return _data.getJSONObject();
    }

    public Map<String, Object> map() {
        return _data.toMap();
    }

    public boolean has(final String key) {
        return _data.has(key);
    }

    public boolean isEmpty() {
        return _data.isEmpty();
    }

    public int lenght() {
        return _data.length();
    }

    /**
     * Put a value. If key is a path (ex: "prop1.subprop") the deepPut is used and children are created.
     *
     * @param key   Key or Path
     * @param value A value.
     */
    public JsonItem put(final String key, final Object value) {
        if (this.isPath(key)) {
            _data.putDeep(key, value);
        } else {
            _data.put(key, value);
        }
        return this;
    }

    /**
     * Put a value using raw key. This is not deep put.
     *
     * @param key   simple key or path
     * @param value value
     */
    public JsonItem putValue(final String key, final Object value) {
        _data.put(key, value);
        return this;
    }

    public JsonItem putNotEmpty(final String key, final Object value) {
        if (null != value && StringUtils.hasText(value.toString())) {
            this.put(key, value);
        }
        return this;
    }

    public JsonItem putValueNotEmpty(final String key, final Object value) {
        if (null != value && StringUtils.hasText(value.toString())) {
            this.putValue(key, value);
        }
        return this;
    }

    public JsonItem putAll(final JsonItem values) {
        return this.putAll(values.json());
    }

    public JsonItem putAll(final Map<String, ? extends Object> values) {
        return this.putAll(new JSONObject(values));
    }

    public JsonItem putAll(final JSONObject values) {
        final Set<String> keys = values.keySet();
        for (final String key : keys) {
            this.putValue(key, values.get(key));
        }
        return this;
    }

    public Object get(final String key) throws JSONException {
        if (this.isPath(key)) {
            return _data.deep(key);
        } else {
            return _data.opt(key);
        }
    }

    public String getString(final String key) throws JSONException {
        if (this.isPath(key)) {
            return _data.deepString(key);
        } else {
            return _data.optString(key);
        }
    }

    public String getString(final String key, final String defaultValue) throws JSONException {
        final String value = this.getString(key);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    public boolean getBoolean(final String key) throws JSONException {
        if (this.isPath(key)) {
            return _data.deepBoolean(key);
        } else {
            return _data.optBoolean(key);
        }
    }

    public double getDouble(final String key) throws JSONException {
        if (this.isPath(key)) {
            return _data.deepDouble(key);
        } else {
            return _data.optDouble(key);
        }
    }

    public int getInt(final String key) throws JSONException {
        if (this.isPath(key)) {
            return _data.deepInteger(key);
        } else {
            return _data.optInt(key);
        }
    }

    public long getLong(final String key) throws JSONException {
        if (this.isPath(key)) {
            return _data.deepLong(key);
        } else {
            return _data.optLong(key);
        }
    }

    public Date getDate(final String key) throws JSONException {
        if (StringUtils.hasText(key)) {
            final String date = this.getString(key);
            final DateWrapper dw = DateWrapper.parse(date);
            return null != dw ? dw.getDateTime() : DateUtils.zero();
        }
        return DateUtils.zero();
    }

    public Date getDate(final String key, final Locale locale) throws JSONException {
        if (StringUtils.hasText(key)) {
            final String date = this.getString(key);
            final DateWrapper dw = DateWrapper.parse(date, locale);
            return null != dw ? dw.getDateTime() : DateUtils.zero();
        }
        return DateUtils.zero();
    }

    public JSONArray getJSONArray(final String key) throws JSONException {
        if (this.isPath(key)) {
            return _data.deepJSONArray(key);
        } else {
            return _data.optJSONArray(key);
        }
    }

    public JSONObject getJSONObject(final String key) throws JSONException {
        if (this.isPath(key)) {
            return _data.deepJSONObject(key);
        } else {
            return _data.optJSONObject(key);
        }
    }

    public Object remove(final String key) {
        if (this.has(key)) {
            return _data.remove(key);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean isPath(final String key) {
        return key.contains(".");
    }

}
