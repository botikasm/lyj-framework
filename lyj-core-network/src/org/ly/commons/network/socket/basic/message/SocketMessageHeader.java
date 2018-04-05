package org.ly.commons.network.socket.basic.message;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.json.JsonItem;

import java.io.Serializable;
import java.util.Map;

public class SocketMessageHeader
        implements Serializable {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_CHARSET = "charset";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonItem _item;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessageHeader() {
        _item = new JsonItem();
        this.init();
    }

    public SocketMessageHeader(final Object item) {
        _item = new JsonItem(item);
        this.init();
    }

    @Override
    public String toString() {
        return _item.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String charset() {
        return _item.getString(FLD_CHARSET);
    }

    public SocketMessageHeader charset(final String value) {
        _item.put(FLD_CHARSET, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int length() {
        return this.getBytes().length;
    }

    public byte[] getBytes() {
        return this.getBytes(this.charset());
    }

    public byte[] getBytes(final String charset) {
        try {
            return _item.toString().getBytes(charset);
        } catch (Throwable ignored) {
            return this.getBytes();
        }
    }

    public void putAll(final Map<String, Object> values){
       _item.putAll(values);
    }

    public void putAll(final JSONObject values){
        _item.putAll(values);
    }

    public void putAll(final JsonItem values){
        _item.putAll(values);
    }

    public void put(final String key, final Object value) {
        _item.put(key, value);
    }

    public Object get(final String key) {
        return _item.get(key);
    }

    public String getString(final String key) {
        return _item.getString(key);
    }

    public int getInt(final String key) {
        return _item.getInt(key);
    }

    public boolean getBoolean(final String key) {
        return _item.getBoolean(key);
    }

    public long getLong(final String key) {
        return _item.getLong(key);
    }

    public double getDouble(final String key) {
        return _item.getDouble(key);
    }

    public JSONArray getJSONArray(final String key) {
        return _item.getJSONArray(key);
    }

    public JSONObject getJSONObject(final String key) {
        return _item.getJSONObject(key);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!_item.has(FLD_CHARSET)) {
            _item.put(FLD_CHARSET, CharEncoding.UTF_8);
        }
    }


}
