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

package org.ly.commons.io.jsondb;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.commons.cryptograph.GUID;
import org.ly.commons.io.jsondb.exceptions.JsonDBInvalidItemException;
import org.ly.commons.util.*;

import java.io.File;

/**
 * Collection
 */
public class JsonDBCollection {

    private static final String ID = IJsonDBConstants.ID;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonDB _db;
    private final String _file_path;
    private final String _name;

    private JSONArray __data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JsonDBCollection(final JsonDB db,
                            final String name) {
        _db = db;
        _file_path = PathUtils.concat(_db.getRoot(), name.concat(".json"));
        _name = name;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String getName() {
        return _name;
    }

    public void drop() {
        // remove metadata
        _db.collectionsMetadata(_name, true);
        // remove data from memory
        __data = null;
        // remove data
        this.remove();
    }

    public JsonList find() {
        final JSONArray data = this.getData();
        return JsonWrapper.toListOfJSONObject(data);
    }

    public JsonList find(final String key, final Object value) {
        try {
            final JSONArray data = this.getData();
            return JsonWrapper.find(data, key, value);
        } catch (Throwable t) {
            this.handle(t);
        }
        return null;
    }

    public JSONObject findOne(final String key, final Object value) {
        try {
            final JSONArray data = this.getData();
            return JsonWrapper.findOne(data, key, value);
        } catch (Throwable t) {
            this.handle(t);
        }
        return null;
    }

    public String findOneAsString(final String key, final Object value) {
        try {
            final JSONArray data = this.getData();
            final JSONObject result = JsonWrapper.findOne(data, key, value);
            return null != result ? result.toString() : "";
        } catch (Throwable t) {
            this.handle(t);
        }
        return "";
    }

    public JSONObject upsert(final Object item) throws JsonDBInvalidItemException {
        if (item instanceof String && StringUtils.isJSONObject(item)) {
            return this.upsert(new JSONObject((String) item));
        } else if (item instanceof JSONObject) {
            return this.upsert((JSONObject) item);
        } else {
            throw new JsonDBInvalidItemException("Invalid item type: " + null != item ? item.getClass().getName() : "NULL");
        }
    }

    public String upsertAsString(final String item) {
        try {
            return this.upsert(new JSONObject(item)).toString();
        } catch (Throwable t) {
            this.handle(t);
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handle(final Throwable t) {
        if (null != _db) {
            _db.handle(t);
        }
    }

    private JSONArray getData() {
        if (null == __data) {
            final String data = this.read();
            if (StringUtils.isJSONArray(data)) {
                __data = new JSONArray(data);
            } else {
                __data = new JSONArray();
                this.save();
            }
        }
        return __data;
    }

    private JSONObject upsert(final JSONObject item) {
        //-- prepare item --//
        if (item.has(ID)) {
            // update
            final JSONObject existing = JsonWrapper.removeOne(this.getData(), ID, item.optString(ID));
            if (null != existing) {
                return this.update(existing, item);
            } else {
                return this.insert(item);
            }
        } else {
            // insert
            return this.insert(item);
        }
    }

    private JSONObject update(final JSONObject existing,
                              final JSONObject item) {
        JsonWrapper.extend(existing, item, true);
        this.add(existing);
        return existing;
    }

    public JSONObject insert(final JSONObject item) {
        if (!item.has(ID)) {
            item.put(ID, GUID.create());
        }
        this.add(item);
        return item;
    }

    private void add(final JSONObject item) {
        //-- add to store and save --/
        this.getData().put(item);
        this.save();
    }

    private String read() {
        try {
            if (PathUtils.exists(_file_path)) {
                return FileUtils.readFileToString(new File(_file_path),
                        JsonDB.CHARSET);
            }
        } catch (Throwable ignored) {

        }
        return null;
    }

    private void remove() {
        try {
            if (PathUtils.exists(_file_path)) {
                FileUtils.delete(_file_path);
            }
        } catch (Throwable ignored) {
        }
    }

    private boolean save() {
        try {
            final String json = this.getData().toString();
            FileUtils.copy(json.getBytes(JsonDB.CHARSET), new File(_file_path));
        } catch (Throwable t) {
            return false;
        }
        return true;
    }
}
