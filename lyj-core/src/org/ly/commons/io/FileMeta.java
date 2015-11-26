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

package org.ly.commons.io;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.commons.util.CollectionUtils;
import org.ly.commons.util.JsonWrapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple data model to hold file meta info and content.
 * "Content" is only property not serialized in JSON structure.
 */
public class FileMeta {

    public static final String FILE_NAME = "name";
    public static final String FILE_SIZE = "size";
    public static final String FILE_TYPE = "type";
    public static final String ATTRIBUTES = "attributes";

    private final JSONObject _json;
    private InputStream _content;

    public FileMeta() {
        _json = new JSONObject();
        JsonWrapper.put(_json, ATTRIBUTES, new JSONObject());
    }

    @Override
    public String toString() {
        return _json.toString();
    }

    public JSONObject getJSON() {
        return _json;
    }

    public void put(final String name, final Object value) {
        JsonWrapper.put(_json, name, value);
    }

    public Object get(final String name) {
        return JsonWrapper.get(_json, name);
    }

    public String getName() {
        return JsonWrapper.getString(_json, FILE_NAME);
    }

    public void setName(String fileName) {
        JsonWrapper.put(_json, FILE_NAME, fileName);
    }

    public long getSize() {
        return JsonWrapper.getLong(_json, FILE_SIZE);
    }

    public void setSize(long fileSize) {
        JsonWrapper.put(_json, FILE_SIZE, fileSize);
    }

    public String getType() {
        return JsonWrapper.getString(_json, FILE_TYPE);
    }

    public void setType(String fileType) {
        JsonWrapper.put(_json, FILE_TYPE, fileType);
    }

    public JSONObject getAttributes() {
        return JsonWrapper.getJSON(_json, ATTRIBUTES);
    }

    public Set<String> getAttributeNames() {
        final Set<String> result = new HashSet<String>();
        final JSONObject attributes = this.getAttributes();
        final JSONArray names = attributes.names();
        CollectionUtils.forEach(names, new CollectionUtils.IterationCallback() {
            @Override
            public Object handle(Object item, int index, Object key) {
                if (item instanceof String) {
                    result.add((String) item);
                }
                return null;
            }
        });
        return result;
    }

    public void addAttribute(final String name, final Object value) {
        final JSONObject attributes = this.getAttributes();
        JsonWrapper.put(attributes, name, value);
    }

    public Object getAttribute(final String name) {
        final JSONObject attributes = this.getAttributes();
        return JsonWrapper.get(attributes, name);
    }

    public InputStream getContent() {
        return _content;
    }

    public void setContent(InputStream content) {
        _content = content;
    }


}
