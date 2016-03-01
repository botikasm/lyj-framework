/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lyj.commons.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;

import java.util.*;

/**
 * Wrapper for generic JSON objects and arrays.
 */
public final class JsonWrapper implements Cloneable {

    private JSONObject _object;
    private JSONArray _array;
    private Throwable _parse_error;

    public JsonWrapper(final String text) {
        this.parse(text);
    }

    public JsonWrapper(final JSONObject object) {
        _array = null;
        _object = object;
        _parse_error = null;
    }

    public JsonWrapper(final JSONArray array) {
        _array = array;
        _object = null;
        _parse_error = null;
    }

    public JsonWrapper(final Object data) {
        _array = null;
        _object = null;
        _parse_error = null;
        if (null != data) {
            if (data instanceof String) {
                this.parse((String) data);
            } else if (data instanceof JSONArray) {
                _array = (JSONArray) data;
            } else if (data instanceof JSONObject) {
                _object = (JSONObject) data;
            } else {
                this.parse(data.toString());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this.isJSONArray()) {
            return _array.equals(obj);
        }
        if (this.isJSONObject()) {
            return _object.equals(obj);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        if (this.isJSONArray()) {
            return _array.hashCode();
        }
        if (this.isJSONObject()) {
            return _object.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public String toString() {
        if (this.isJSONArray()) {
            return _array.toString();
        }
        if (this.isJSONObject()) {
            return _object.toString();
        }
        return "";
    }

    public String toString(final int indentFactor) {
        try {
            if (this.isJSONArray()) {
                return _array.toString(indentFactor);
            }
            if (this.isJSONObject()) {
                return _object.toString(indentFactor);
            }
        } catch (Throwable ignored) {
        }
        return this.toString();
    }

    @Override
    public JsonWrapper clone() {
        return new JsonWrapper(this.toString());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JsonWrapper parse(final String text) {
        try {
            if (StringUtils.isJSONObject(text)) {
                _array = null;
                _object = new JSONObject(text);
            } else if (StringUtils.isJSONArray(text)) {
                _array = new JSONArray(text);
                _object = null;
            } else {
                _array = null;
                _object = new JSONObject();
            }
            _parse_error = null;
        } catch (Throwable t) {
            _array = null;
            _object = new JSONObject();
            _parse_error = t;
        }
        return this;
    }

    public Map<String, Object> toMap() {
        if (this.isJSONArray()) {
            return JsonWrapper.toMap(this.getJSONArray());
        } else if (this.isJSONObject()) {
            return JsonWrapper.toMap(this.getJSONObject());
        }
        return new HashMap();
    }

    public boolean hasParseError() {
        return _parse_error != null;
    }

    public Throwable getParseError() {
        return _parse_error;
    }

    public boolean isNull() {
        return null == _array && null == _object;
    }

    public boolean isJSONArray() {
        return null != _array;
    }

    public boolean isJSONObject() {
        return null != _object;
    }

    public Object getObject() {
        return null != _object ? _object : _array;
    }

    public JSONObject getJSONObject() {
        return _object;
    }

    public JSONArray getJSONArray() {
        return _array;
    }

    public int length() {
        if (this.isJSONArray()) {
            return _array.length();
        }
        if (this.isJSONObject()) {
            return _object.length();
        }
        return 0;
    }

    public boolean isEmpty() {
        return this.length() == 0;
    }

    public List<Object> values() {
        return toList(this.getObject());
    }

    public void clear() {
        if (null != _array) {
            while (_array.length() > 0) {
                _array.remove(0);
            }
        }
        if (null != _object) {
            final Iterator i = _object.keys();
            while (i.hasNext()) {
                _object.remove((String) i.next());
            }
        }
    }

    public Map<String, Object> flat() {
        return JsonWrapper.toFlatMap(this.getJSONObject());
    }

    //-- special methods to retrieve values navigating object --//

    public Object deep(final String path) {
        if (null != _object) {
            return BeanUtils.getValueIfAny(_object, path);
        } else if (null != _array) {
            return BeanUtils.getValueIfAny(_array, path);
        }
        return null;
    }

    public Object deepRemove(final String path) {
        if (null != _object) {
            final Object result;
            if (path.contains(".")) {
                final String[] tokens = StringUtils.splitLast(path, ".");
                final JSONObject parent = JsonWrapper.getJSON(_object, tokens[0]);
                result = JsonWrapper.remove(parent, tokens[1]);
            } else {
                result = this.remove(path);
            }
            return result;
        }
        return null;
    }

    public JSONArray deepJSONArray(final String path) {
        final Object result = this.deep(path);
        return result instanceof JSONArray ? (JSONArray) result : null;
    }

    public JSONObject deepJSONObject(final String path) {
        final Object result = this.deep(path);
        return result instanceof JSONObject ? (JSONObject) result : null;
    }

    public String deepString(final String path) {
        return this.deepString(path, "");
    }

    public String deepString(final String path, final String def) {
        final Object result = this.deep(path);
        return null != result ? result.toString() : def;
    }

    public boolean deepBoolean(final String path) {
        return this.deepBoolean(path, false);
    }

    public boolean deepBoolean(final String path, final boolean def) {
        try {
            final Object result = this.deep(path);
            return result instanceof Boolean ? Boolean.parseBoolean(result.toString()) : def;
        } catch (Throwable ignored) {
        }
        return def;
    }

    public int deepInteger(final String path) {
        return this.deepInteger(path, 0);
    }

    public int deepInteger(final String path, final int def) {
        try {
            final Object result = this.deep(path);
            if (result instanceof Integer) {
                return Integer.parseInt(result.toString());
            } else {
                return ConversionUtils.toInteger(result, def);
            }
        } catch (Throwable ignored) {
        }
        return def;
    }

    public long deepLong(final String path) {
        return this.deepLong(path, 0L);
    }

    public long deepLong(final String path, final long def) {
        try {
            final Object result = this.deep(path);
            if (result instanceof Long) {
                return Long.parseLong(result.toString());
            } else {
                return ConversionUtils.toLong(result, def);
            }
        } catch (Throwable ignored) {
        }
        return def;
    }

    public double deepDouble(final String path) {
        return this.deepDouble(path, 0.0);
    }

    public double deepDouble(final String path, final double def) {
        try {
            final Object result = this.deep(path);
            if (result instanceof Double) {
                return Double.parseDouble(result.toString());
            } else {
                return ConversionUtils.toDouble(result);
            }
        } catch (Throwable ignored) {
        }
        return def;
    }

    //-- JSONObject --//

    public boolean isNull(final String key) {
        if (this.isJSONObject()) {
            return _object.isNull(key);
        }
        return true;
    }

    public boolean has(final String key) {
        return JsonWrapper.has(_object, key);
    }

    public Set<String> keys() {
        final Set<String> keys = new LinkedHashSet<String>();
        if (this.isJSONObject()) {
            final Iterator<String> iter = _object.keys();
            while (iter.hasNext()) {
                keys.add(iter.next());
            }
        }
        return keys;
    }

    public Object get(final String key) throws JSONException {
        if (this.isJSONObject()) {
            return _object.get(key);
        }
        return null;
    }

    public String getString(final String key) throws JSONException {
        if (this.isJSONObject()) {
            return _object.getString(key);
        }
        return null;
    }

    public boolean getBoolean(final String key) throws JSONException {
        if (this.isJSONObject()) {
            return _object.getBoolean(key);
        }
        return false;
    }

    public double getDouble(final String key) throws JSONException {
        if (this.isJSONObject()) {
            return _object.getDouble(key);
        }
        return 0.0;
    }

    public int getInt(final String key) throws JSONException {
        if (this.isJSONObject()) {
            return _object.getInt(key);
        }
        return 0;
    }

    public long getLong(final String key) throws JSONException {
        if (this.isJSONObject()) {
            return _object.getLong(key);
        }
        return 0L;
    }

    public Date getDate(final String key) throws JSONException {
        if (this.isJSONObject()) {
            final String date = _object.getString(key);
            final DateWrapper dw = DateWrapper.parse(date);
            return null != dw ? dw.getDateTime() : DateUtils.zero();
        }
        return DateUtils.zero();
    }

    public Date getDate(final String key, final Locale locale) throws JSONException {
        if (this.isJSONObject()) {
            final String date = _object.getString(key);
            final DateWrapper dw = DateWrapper.parse(date, locale);
            return null != dw ? dw.getDateTime() : DateUtils.zero();
        }
        return DateUtils.zero();
    }

    public JSONArray getJSONArray(final String key) throws JSONException {
        if (this.isJSONObject()) {
            return _object.getJSONArray(key);
        }
        return null;
    }

    public JSONObject getJSONObject(final String key) throws JSONException {
        if (this.isJSONObject()) {
            return _object.getJSONObject(key);
        }
        return null;
    }

    public Object opt(final String key) {
        if (this.isJSONObject()) {
            return _object.opt(key);
        }
        return null;
    }

    public String optString(final String key) {
        if (this.isJSONObject()) {
            return _object.optString(key, "");
        }
        return null;
    }

    public String optString(final String key, final String def) {
        if (this.isJSONObject()) {
            return _object.optString(key, def);
        }
        return null;
    }

    public boolean optBoolean(final String key) {
        if (this.isJSONObject()) {
            return _object.optBoolean(key);
        }
        return false;
    }

    public double optDouble(final String key) {
        return this.optDouble(key, 0.0);
    }

    public double optDouble(final String key, final double defValue) {
        if (this.isJSONObject()) {
            return _object.optDouble(key, defValue);
        }
        return defValue;
    }

    public int optInt(final String key) {
        if (this.isJSONObject()) {
            return _object.optInt(key);
        }
        return 0;
    }

    public long optLong(final String key) {
        if (this.isJSONObject()) {
            return _object.optLong(key);
        }
        return 0L;
    }

    public JSONArray optJSONArray(final String key) {
        if (this.isJSONObject()) {
            return _object.optJSONArray(key);
        }
        return null;
    }

    public JSONObject optJSONObject(final String key) {
        if (this.isJSONObject()) {
            return _object.optJSONObject(key);
        }
        return null;
    }

    public void put(final String key, final Object obj) throws JSONException {
        if (this.isJSONObject()) {
            if (obj instanceof Map) {
                _object.put(key, (Map) obj);
            } else if (obj instanceof Collection) {
                _object.put(key, (Collection) obj);
            } else if (obj instanceof Boolean) {
                _object.put(key, (Boolean) obj);
            } else if (obj instanceof Integer) {
                _object.put(key, (Integer) obj);
            } else if (obj instanceof Double) {
                _object.put(key, (Double) obj);
            } else if (obj instanceof Long) {
                _object.put(key, (Long) obj);
            } else {
                _object.putOpt(key, obj);
            }
        }
    }

    public JSONObject putDeep(final String path, final Object obj) {
        return JsonWrapper.put(_object, path, obj, true);
    }

    public void putOnce(final String key, final Object obj) throws JSONException {
        if (this.isJSONObject()) {
            _object.putOnce(key, obj);
        }
    }

    public void putOpt(final String key, final Object obj) throws JSONException {
        if (this.isJSONObject()) {
            _object.putOpt(key, obj);
        }
    }

    public void putSilent(final String key, final Object obj) {
        if (this.isJSONObject()) {
            try {
                _object.putOpt(key, obj);
            } catch (Throwable ignored) {

            }
        }
    }

    public Object remove(final String key) {
        if (this.isJSONObject()) {
            return _object.remove(key);
        }
        return null;
    }

    //-- JSONArray --//

    public boolean isNull(final int index) {
        if (this.isJSONArray()) {
            return _array.isNull(index);
        }
        return true;
    }

    public Object remove(final int index) {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.remove(index);
        }
        return null;
    }

    public Object get(final int index) throws JSONException {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.get(index);
        }
        return null;
    }

    public String getString(final int index) throws JSONException {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.getString(index);
        }
        return null;
    }

    public boolean getBoolean(final int index) throws JSONException {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.getBoolean(index);
        }
        return false;
    }

    public double getDouble(final int index) throws JSONException {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.getDouble(index);
        }
        return 0.0;
    }

    public int getInt(final int index) throws JSONException {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.getInt(index);
        }
        return 0;
    }

    public long getLong(final int index) throws JSONException {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.getLong(index);
        }
        return 0L;
    }

    public JSONArray getJSONArray(final int index) throws JSONException {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.getJSONArray(index);
        }
        return null;
    }

    public JSONObject getJSONObject(final int index) throws JSONException {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.getJSONObject(index);
        }
        return null;
    }

    public Object opt(final int index) {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.opt(index);
        }
        return null;
    }

    public String optString(final int index) {
        if (this.isJSONArray() && _array.length() > index) {
            return _array.optString(index);
        }
        return null;
    }

    public boolean optBoolean(final int index) {
        if (this.isJSONArray()) {
            return _array.optBoolean(index);
        }
        return false;
    }

    public double optDouble(final int index) {
        if (this.isJSONArray()) {
            return _array.optDouble(index);
        }
        return 0.0;
    }

    public int optInt(final int index) {
        if (this.isJSONArray()) {
            return _array.optInt(index);
        }
        return 0;
    }

    public long optLong(final int index) {
        if (this.isJSONArray()) {
            return _array.optLong(index);
        }
        return 0L;
    }

    public JSONArray optJSONArray(final int index) {
        if (this.isJSONArray()) {
            return _array.optJSONArray(index);
        }
        return null;
    }

    public JSONObject optJSONObject(final int index) {
        if (this.isJSONArray()) {
            return _array.optJSONObject(index);
        }
        return null;
    }

    public void put(final Object obj) throws JSONException {
        if (this.isJSONArray()) {
            if (obj instanceof Map) {
                _array.put((Map) obj);
            } else if (obj instanceof Collection) {
                _array.put((Collection) obj);
            } else if (obj instanceof Boolean) {
                _array.put((Boolean) obj);
            } else if (obj instanceof Integer) {
                _array.put((Integer) obj);
            } else if (obj instanceof Double) {
                _array.put((Double) obj);
            } else if (obj instanceof Long) {
                _array.put((Long) obj);
            } else {
                _array.put(obj);
            }
        }
    }

    public void put(final int index, final Object obj) throws JSONException {
        if (this.isJSONArray()) {
            if (obj instanceof Map) {
                _array.put(index, (Map) obj);
            } else if (obj instanceof Collection) {
                _array.put(index, (Collection) obj);
            } else if (obj instanceof Boolean) {
                _array.put(index, (Boolean) obj);
            } else if (obj instanceof Integer) {
                _array.put(index, (Integer) obj);
            } else if (obj instanceof Double) {
                _array.put(index, (Double) obj);
            } else if (obj instanceof Long) {
                _array.put(index, (Long) obj);
            } else {
                _array.put(index, obj);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static JsonWrapper wrap(final String json) {
        return new JsonWrapper(json);
    }

    public static JsonWrapper wrap(final JSONArray json) {
        return new JsonWrapper(json);
    }

    public static JsonWrapper wrap(final JSONObject json) {
        return new JsonWrapper(json);
    }

    public static JsonWrapper wrap(final Map map) {
        try {
            final JSONObject json = toJSONObject(map);
            return new JsonWrapper(json);
        } catch (Throwable ignored) {
        }
        return new JsonWrapper(new JSONObject());
    }

    public static JsonWrapper wrap(final Collection collection) {
        try {
            final JSONArray json = toJSONArray(collection);
            return new JsonWrapper(json);
        } catch (Throwable ignored) {
        }
        return new JsonWrapper(new JSONObject());
    }

    public static boolean isEmpty(final Object item) {
        if (item instanceof JSONObject) {
            return ((JSONObject) item).length() == 0;
        } else if (item instanceof JSONArray) {
            return ((JSONArray) item).length() == 0;
        }
        return true;
    }

    // ------------------------------------------------------------------------
    //               S T A T I C  -  C O N V E R S I O N
    // ------------------------------------------------------------------------

    public static List<JSONObject> parseList(final String jsonArray) {
        final JSONArray array = JsonWrapper.wrap(jsonArray).getJSONArray();
        return toListOfJSONObject(array);
    }

    public static JSONObject toJSONObject(final Object object) throws JSONException {
        if (null != object) {
            if (object instanceof Map) {
                return new JSONObject((Map) object);
            } else if (object instanceof JSONObject) {
                return (JSONObject) object;
            } else {
                return new JSONObject(object.toString());
            }
        }
        return new JSONObject();
    }

    /**
     * Returns JSONArray
     *
     * @param object Object to convert
     * @return JSONArray
     */
    public static JSONArray toJSONArray(final Object object) {
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        final JSONArray result = new JSONArray();
        if (null != object) {
            if (object instanceof JSONObject) {
                final JSONObject item = (JSONObject) object;
                final Iterator keys = item.keys();
                while (keys.hasNext()) {
                    result.put(item.opt(keys.next().toString()));
                }
            } else if (object instanceof Map) {
                final Map item = (Map) object;
                final Set keys = item.keySet();
                for (final Object key : keys) {
                    result.put(item.get(key));
                }
            } else if (object instanceof Collection) {
                final Collection list = (Collection) object;
                for (final Object item : list) {
                    result.put(item);
                }
            } else if (object.getClass().isArray()) {
                final Object[] array = (Object[]) object;
                for (final Object value : array) {
                    result.put(value);
                }
            }
        }
        return result;
    }

    /**
     * Convert JSONObject into Map.
     *
     * @param item Object to convert.
     * @return Converted Map.
     */
    public static Map<String, Object> toMap(final JSONObject item) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        final Iterator keys = item.keys();
        while (keys.hasNext()) {
            final String name = keys.next().toString();
            if (null != name) {
                final Object value = item.opt(name);
                result.put(name, null != value ? value : "");
            }
        }
        return result;
    }

    public static Map<String, Object> toFlatMap(final JSONObject item) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (null != item) {
            flatMap(result, "", item);
        }
        return result;
    }

    public static Map<String, Object> toMap(final JSONArray jsonarray) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        if(null!=jsonarray) {
            final int len = jsonarray.length();
            for (int i = 0; i < len; i++) {
                final String key = "param" + i;
                final Object value = jsonarray.opt(i);
                if (null != value) {
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    public static Map<String, String> toMapOfString(final JSONObject json) {
        final Map<String, String> result = new LinkedHashMap<String, String>();
        if(null!=json){
            final Iterator iterator = json.keys();
            while (iterator.hasNext()) {
                final String key = iterator.next().toString();
                final Object value = json.opt(key);
                if (null != value) {
                    result.put(key, StringUtils.toString(value));
                }
            }
        }
        return result;
    }

    public static Map<String, String> toMapOfString(final JSONArray jsonarray) {
        final Map<String, String> result = new LinkedHashMap<String, String>();
        if(null!=jsonarray) {
            final int len = jsonarray.length();
            for (int i = 0; i < len; i++) {
                final String key = "param" + i;
                final Object value = jsonarray.opt(i);
                if (null != value) {
                    result.put(key, StringUtils.toString(value));
                }
            }
        }
        return result;
    }

    public static List<Object> toList(final Object object) {
        final List<Object> result = new LinkedList<Object>();
        try {
            if (object instanceof JSONArray) {
                final JSONArray array = (JSONArray) object;
                for (int i = 0; i < array.length(); i++) {
                    result.add(array.get(i));
                }
            } else if (object instanceof JSONObject) {
                final JSONObject jobject = (JSONObject) object;
                final Iterator keys = jobject.keys();
                while (keys.hasNext()) {
                    final String key = keys.next().toString();
                    final Object value = jobject.opt(key);
                    if (null != value) {
                        result.add(value);
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return result;
    }

    public static Object[] toArray(final JSONArray array) {
        final List<Object> result = toList(array);
        return null != result
                ? result.toArray(new Object[result.size()])
                : new Object[0];
    }

    public static JsonList toListOfJSONObject(final Object object) {
        final JsonList result = new JsonList();
        try {
            if (object instanceof JSONArray) {
                final JSONArray array = (JSONArray) object;
                for (int i = 0; i < array.length(); i++) {
                    final Object item = array.get(i);
                    if (item instanceof JSONObject) {
                        result.add((JSONObject) item);
                    }
                }
            } else if (object instanceof JSONObject) {
                final JSONObject jobject = (JSONObject) object;
                final Iterator keys = jobject.keys();
                while (keys.hasNext()) {
                    final String key = keys.next().toString();
                    final JSONObject value = jobject.optJSONObject(key);
                    if (null != value) {
                        result.add(value);
                    }
                }
            }

        } catch (Throwable ignored) {
        }
        return result;
    }

    public static JSONObject[] toArrayOfJSONObject(final JSONArray array) {
        final List<JSONObject> result = toListOfJSONObject(array);
        return null != result
                ? result.toArray(new JSONObject[result.size()])
                : new JSONObject[0];
    }

    public static List<String> toListOfString(final JSONArray array) {
        final List<String> result = new LinkedList<String>();
        if (null != array) {
            final List<Object> list = toList(array);
            for (final Object item : list) {
                result.add(item.toString());
            }
        }
        return result;
    }

    public static String[] toArrayOfString(final JSONArray array) {
        final List<String> result = toListOfString(array);
        return null != result
                ? result.toArray(new String[result.size()])
                : new String[0];
    }

    // ------------------------------------------------------------------------
    //               S T A T I C  -  E X T E N D
    // ------------------------------------------------------------------------

    public static JSONArray extend(final JSONArray target,
                                   final JSONArray source) {
        return extend(target, source, false, null);
    }

    public static JSONArray extend(final JSONArray target,
                                   final JSONArray source, final boolean overwrite) {
        return extend(target, source, overwrite, null);
    }

    /**
     * Extends JSONObjects inside array
     *
     * @param target
     * @param source
     * @param overwrite
     * @return
     */
    public static JSONArray extend(final JSONArray target,
                                   final JSONArray source,
                                   final boolean overwrite, final Object nullValue) {
        try {
            if (null != target && null != source && target.length() == source.length()) {
                for (int i = 0; i < source.length(); i++) {
                    final Object sval = source.get(i);
                    final Object tval = target.get(i);
                    if (sval instanceof JSONObject && tval instanceof JSONObject) {
                        extend((JSONObject) tval, (JSONObject) sval, overwrite, nullValue);
                    }
                }
            }
        } catch (Throwable t) {
        }
        return target;
    }

    /**
     * Extends target object with source properties, but does not overwrite
     * existing properties in target.
     *
     * @param target
     * @param source
     * @return
     */
    public static JSONObject extend(final JSONObject target,
                                    final JSONObject source) {
        return extend(target, source, false, null);
    }

    /**
     * Extends target object with source properties, but does not overwrite
     * existing properties in target.
     *
     * @param target
     * @param source
     * @param overwrite
     * @return
     */
    public static JSONObject extend(final JSONObject target,
                                    final JSONObject source, final boolean overwrite) {
        return extend(target, source, overwrite, null);
    }

    /**
     * Extends target object with source properties, but does not overwrite
     * existing properties in target.
     *
     * @param target
     * @param source
     * @param overwrite
     * @param nullValue (Optional) If assigned, target is checked for this
     *                  value, and this value is considered as null (overwritten)
     * @return
     */
    public static JSONObject extend(final JSONObject target,
                                    final JSONObject source,
                                    final boolean overwrite,
                                    final Object nullValue) {
        final Iterator keys = source.keys();
        while (keys.hasNext()) {
            try {
                final String key = keys.next().toString();
                final Object sourceval = source.opt(key);
                if (target.has(key)) {
                    if (overwrite) {
                        final Object targetval = target.opt(key);
                        if (targetval instanceof JSONObject && sourceval instanceof JSONObject) {
                            extend((JSONObject) targetval, (JSONObject) sourceval, overwrite);
                        } else {
                            target.putOpt(key, sourceval);
                        }
                    } else if (null != nullValue) {
                        if (nullValue.equals(target.opt(key))) {
                            target.putOpt(key, sourceval);
                        }
                    }
                } else {
                    target.putOpt(key, sourceval);
                }
            } catch (Throwable t) {
            }
        }

        return target;
    }


    // ------------------------------------------------------------------------
    //               S T A T I C  -  P U T   A N D   G E T
    // ------------------------------------------------------------------------

    public static JSONObject put(final JSONObject item,
                                 final String path,
                                 final Object val) {
        return put(item, path, val, false);
    }

    public static JSONObject put(final JSONObject item,
                                 final String path,
                                 final Object val,
                                 final boolean autocreate) {
        final Object value = (val instanceof JsonWrapper) ? ((JsonWrapper) val).getObject() : val;
        try {
            if (path.indexOf(".") > 0) {
                final int len = StringUtils.countOccurrencesOf(path, ".");
                final String[] tokens = StringUtils.splitAt(len, path, ".");
                final Object obj = getValueIfAny(item, tokens[0]);
                if (null == obj && autocreate) {
                    provide(item, path);
                    return put(item, path, val, false);
                }
                if (obj instanceof JSONObject) {
                    JsonWrapper.put((JSONObject) obj, tokens[1], value, false);
                } else if (obj instanceof JSONArray) {
                    JsonWrapper.put((JSONArray) obj, value);
                }
            } else {
                if (!(value instanceof JSONArray) && item.has(path)) {
                    final Object obj = item.opt(path);
                    if (obj instanceof JSONArray) {
                        ((JSONArray) obj).put(value);
                    } else {
                        item.putOpt(path, value);
                    }
                } else {
                    item.putOpt(path, value);
                }
            }
        } catch (Throwable t) {
            getLoggerStatic().log(Level.SEVERE, FormatUtils.format(
                    "Error putting '{0}' into property '{1}' of object '{2}': {3}",
                    value, path, item, t), t);
        }
        return item;
    }

    public static JSONArray put(final JSONArray item,
                                final Object val) {
        final Object value = (val instanceof JsonWrapper) ? ((JsonWrapper) val).getObject() : val;
        try {
            item.put(value);
        } catch (Throwable t) {
            getLoggerStatic().log(Level.SEVERE, FormatUtils.format(
                    "Error putting '{0}' into array '{2}': {3}",
                    value, item, t), t);
        }
        return item;
    }

    /**
     * Append or add object if does not exists.
     *
     * @param item
     * @param field
     * @param val
     * @return
     */
    public static JSONObject append(final JSONObject item,
                                    final String field, final Object val) {
        final Object value = (val instanceof JsonWrapper) ? ((JsonWrapper) val).getObject() : val;
        try {
            if (!item.has(field)) {
                item.putOpt(field, new JSONArray());
            }
            final Object existing = item.get(field);
            if (existing instanceof JSONArray) {
                ((JSONArray) existing).put(value);
            } else {
                final JSONArray array = new JSONArray();
                array.put(value);
                item.putOpt(field, array);
            }
        } catch (Exception ex) {
        }
        return item;
    }

    public static Object remove(final JSONObject item, final String key) {
        return item.remove(key);
    }

    public static Object remove(final JSONArray item, final int index) {
        return item.remove(index);
    }

    public static boolean has(final Object object, final String path) {
        // check JSONObject
        if ((object instanceof JSONObject) && StringUtils.hasText(path)) {
            if (!path.contains(".")) {
                return ((JSONObject) object).has(path);
            }
            final String[] tokens = StringUtils.splitLast(path, ".");
            final JSONObject json = getJSON(object, tokens[0]);
            return null != json ? json.has(tokens[1]) : false;
        }
        return false;
    }

    public static Object get(final JSONObject item,
                             final String field) {
        return getValueIfAny(item, field);
    }

    public static JSONObject getJSON(final Object item,
                                     final String field) {
        return getJSON(item, field, null);
    }

    public static JSONObject getJSON(final Object item,
                                     final String field,
                                     final JSONObject defVal) {
        final Object value = getValueIfAny(item, field);
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        return defVal;
    }

    public static String getString(final Object item,
                                   final String field) {
        return getString(item, field, "");
    }

    public static String getString(final Object item,
                                   final String field, final String defValue) {
        final Object value = getValueIfAny(item, field);
        if (null == value) {
            return defValue;
        }
        return value.toString();
    }

    public static long getLong(final Object item,
                               final String field) {
        return getLong(item, field, 0);
    }

    public static long getLong(final Object item,
                               final String field, final long defValue) {
        final Object value = getValueIfAny(item, field);
        if (null == value) {
            return defValue;
        }
        return ConversionUtils.toLong(value, defValue);
    }

    public static int getInt(final Object item,
                             final String field) {
        return getInt(item, field, 0);
    }

    public static int getInt(final Object item,
                             final String field, final int defValue) {
        final Object value = getValueIfAny(item, field);
        if (null == value) {
            return defValue;
        }
        return ConversionUtils.toInteger(value, defValue);
    }

    public static double getDouble(final Object item,
                                   final String field) {
        return getDouble(item, field, 0.0);
    }

    public static double getDouble(final Object item,
                                   final String field, final double defValue) {
        final Object value = getValueIfAny(item, field);
        if (null == value) {
            return defValue;
        }
        return ConversionUtils.toDouble(value, -1, defValue);
    }

    public static boolean getBoolean(final Object item,
                                     final String field) {
        return getBoolean(item, field, false);
    }

    public static boolean getBoolean(final Object item,
                                     final String field, final boolean defValue) {
        final Object value = getValueIfAny(item, field);
        if (null == value) {
            return defValue;
        }
        return ConversionUtils.toBoolean(value, defValue);
    }

    public static Date getDate(final Object item,
                               final String field) throws JSONException {
        final Object value = getValueIfAny(item, field);
        if (value instanceof String) {
            final String date = (String) value;
            final DateWrapper dw = DateWrapper.parse(date);
            return null != dw ? dw.getDateTime() : DateUtils.zero();
        }
        return DateUtils.zero();
    }

    public static Date getDate(final Object item,
                               final String field,
                               final Locale locale) throws JSONException {
        final Object value = getValueIfAny(item, field);
        if (value instanceof String) {
            final String date = (String) value;
            final DateWrapper dw = DateWrapper.parse(date, locale);
            return null != dw ? dw.getDateTime() : DateUtils.zero();
        }
        return DateUtils.zero();
    }

    public static JSONArray getArray(final Object item,
                                     final String field) {
        if (null != item) {
            final JSONArray result;
            final Object value = getValueIfAny(item, field);
            if (value instanceof JSONArray) {
                result = (JSONArray) value;
            } else if (value instanceof JSONObject) {
                result = JsonWrapper.toJSONArray((JSONObject) value);
            } else if (value instanceof Map) {
                result = JsonWrapper.toJSONArray((Map) value);
            } else {
                result = new JSONArray();
                if (item instanceof JSONObject) {
                    try {
                        ((JSONObject) item).putOpt(field, result);
                    } catch (Throwable t) {
                    }
                }
            }
            return result;
        }
        return null;
    }

    public static List getList(final Object item, final String field) {
        final List<Object> result = new LinkedList<Object>();
        if (null != item) {
            final JSONArray array = JsonWrapper.getArray(item, field);
            if (array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    try {
                        result.add(array.get(i));
                    } catch (Throwable t) {
                    }
                }
            }
        }

        return result;
    }

    // ------------------------------------------------------------------------
    //               S T A T I C  -  E Q U A L I T Y
    // ------------------------------------------------------------------------

    public static boolean equals(final Object item1, final Object item2) {
        if (CompareUtils.equals(item1, item2)) {
            return true;
        } else if (null != item1 && null != item2) {
            if (item1 instanceof JSONObject && item2 instanceof JSONObject) {
                return equals((JSONObject) item1, (JSONObject) item2);
            } else if (item1 instanceof JSONArray && item2 instanceof JSONArray) {
                return equals((JSONArray) item1, (JSONArray) item2);
            }
        }
        return false;
    }

    public static boolean equals(final JSONArray item1, final JSONArray item2) {
        if (CompareUtils.equals(item1, item2)) {
            return true;
        } else if (null != item1 && null != item2 && item1.length() == item2.length()) {
            for (int i = 0; i < item1.length(); i++) {
                final Object value1 = item1.opt(i);
                final Object value2 = item2.opt(i);
                if (!equals(value1, value2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean equals(final JSONObject item1, final JSONObject item2) {
        if (CompareUtils.equals(item1, item2)) {
            return true;
        } else if (null != item1 && null != item2) {
            final Iterator keys = item1.keys();
            while (keys.hasNext()) {
                final String key = keys.next().toString();
                final Object value1 = item1.opt(key);
                final Object value2 = item2.opt(key);
                if (!equals(value1, value2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean contains(final JSONArray array, final Object item) {
        if (null != array && null != item) {
            final int len = array.length();
            for (int i = 0; i < len; i++) {
                final Object value = array.opt(i);
                if (equals(item, value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean contains(final JSONArray array, final String value) {
        final int length = array.length();
        for (int i = 0; i < length; i++) {
            final String name = array.getString(i);
            if (name.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(final JSONArray array,
                                   final String key, final String value) {
        return null != findOne(array, key, value);
    }

    // ------------------------------------------------------------------------
    //               S T A T I C  -  L O O K U P
    // ------------------------------------------------------------------------

    public static JSONObject findOne(final JSONArray array,
                                     final String key,
                                     final Object value) {
        final int length = array.length();
        for (int i = 0; i < length; i++) {
            final Object item = array.get(i);
            if (item instanceof JSONObject) {
                final JSONObject jitem = (JSONObject) item;
                if (jitem.has(key) && CompareUtils.equals(jitem.opt(key), value)) {
                    return jitem;
                }
            }
        }
        return null;
    }

    public static JsonList find(final JSONArray array,
                                final String key,
                                final Object value) {
        final JsonList result = new JsonList();
        final int length = array.length();
        for (int i = 0; i < length; i++) {
            final JSONObject item = array.optJSONObject(i);
            if (null != item && item.has(key) && CompareUtils.equals(item.opt(key), value)) {
                result.add(item);
            }
        }
        return result;
    }

    public static boolean removeOne(final JSONArray array,
                                    final String value) {
        final int length = array.length();
        for (int i = length - 1; i > -1; i--) {
            final String item = array.optString(i);
            if (null != item && item.equalsIgnoreCase(value)) {
                array.remove(i);
                return true;
            }
        }
        return false;
    }

    public static int removeAll(final JSONArray array,
                                final String value) {
        int count = 0;
        final int length = array.length();
        for (int i = length - 1; i > -1; i--) {
            final String item = array.optString(i);
            if (null != item && item.equalsIgnoreCase(value)) {
                array.remove(i);
                count++;
            }
        }
        return count;
    }

    public static JSONObject removeOne(final JSONArray array,
                                       final String key, final String value) {
        final int length = array.length();
        for (int i = length - 1; i > -1; i--) {
            final Object item = array.get(i);
            if ((item instanceof JSONObject)
                    && ((JSONObject) item).has(key)
                    && ((JSONObject) item).optString(key).equalsIgnoreCase(value)) {
                array.remove(i);
                return (JSONObject) item;
            }
        }
        return null;
    }

    public static JsonList removeAll(final JSONArray array,
                                     final String key, final String value) {
        final JsonList result = new JsonList();
        final int length = array.length();
        for (int i = length - 1; i > -1; i--) {
            final Object item = array.get(i);
            if ((item instanceof JSONObject)
                    && ((JSONObject) item).has(key)
                    && ((JSONObject) item).optString(key).equalsIgnoreCase(value)) {
                array.remove(i);
                result.add((JSONObject) item);
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    //               S T A T I C  -  U T I L I T Y
    // ------------------------------------------------------------------------

    public static void clear(final JSONObject target) {
        if (null != target) {
            final String[] keys = CollectionUtils.toArrayOfString(target.keys());
            for (final String key : keys) {
                try {
                    target.remove(key);
                } catch (Throwable t) {
                }
            }
        }
    }

    public static void provide(final JSONObject item,
                               final String path) throws JSONException {
        if (null != item) {
            if (path.indexOf(".") > 0) {
                final String[] tokens = StringUtils.splitAt(1, path, ".");
                if (!item.has(tokens[0])) {
                    item.putOnce(tokens[0], new JSONObject());
                }
                provide(item.optJSONObject(tokens[0]), tokens[1]);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C  p r i v a t e
    // ------------------------------------------------------------------------
    private static Logger getLoggerStatic() {
        return LoggingUtils.getLogger(JsonWrapper.class);
    }

    private static Object getValueIfAny(final Object item, final String path) {
        Object result = BeanUtils.getValueIfAny(item, path);
        if (null == result) {
            result = BeanUtils.getValueIfAny(item, path.toLowerCase());
        }

        if (result instanceof JsonWrapper) {
            result = ((JsonWrapper) result).getObject();
        }

        return result;
    }

    private static void flatMap(final Map<String, Object> map, final String root, final JSONObject item) {
        final Iterator keys = item.keys();
        while (keys.hasNext()) {
            final String name = keys.next().toString();
            if (null != name) {
                final Object value = item.opt(name);
                if (null != value) {
                    final String key = StringUtils.concatDot(root, name);
                    if (value instanceof JSONObject) {
                        flatMap(map, key, (JSONObject) value);
                    } else {
                        map.put(key, value);
                    }
                }
            }
        }
    }

}
