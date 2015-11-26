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

/*
 *
 */

package org.ly.commons.event;

import org.json.JSONObject;
import org.ly.commons.util.JsonWrapper;
import org.ly.commons.util.StringUtils;

/**
 *
 *
 */
public class Event {

    private static final String FLD_SENDER = "sender";
    private static final String FLD_NAME = "name";
    private static final String FLD_DATA = "data";

    private final JSONObject _json;

    public Event(final Object sender,
                 final String name) {
        _json = new JSONObject();
        this.put(FLD_SENDER, sender);
        this.put(FLD_NAME, name);
    }

    public Event(final Object sender,
                 final String name,
                 final Object data) {
        _json = new JSONObject();
        this.put(FLD_SENDER, sender);
        this.put(FLD_NAME, name);
        this.setData(data);
    }

    @Override
    public String toString() {
        return _json.toString();
    }

    public JSONObject toJSON() {
        return new JSONObject(_json.toString());
    }

    public Object getSender() {
        return JsonWrapper.get(_json, FLD_SENDER);
    }

    /*
    public void setSender(Object sender) {
        _sender = sender;
    }
    */
    public String getName() {
        return JsonWrapper.getString(_json, FLD_NAME);
    }

    public Object getData() {
        return JsonWrapper.get(_json, FLD_DATA);
    }

    public void setData(final Object data) {
        this.put(FLD_DATA, data);
    }

    protected void put(final String key, final Object value) {
        if (StringUtils.hasText(key) && null != value) {
            JsonWrapper.put(_json, key, value);
        }
    }

    protected Object get(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.get(_json, key);
        }
        return null;
    }

    protected String getString(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.getString(_json, key);
        }
        return "";
    }

    protected boolean getBoolean(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.getBoolean(_json, key);
        }
        return false;
    }

    protected int getInt(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.getInt(_json, key);
        }
        return 0;
    }

    protected double getDouble(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.getDouble(_json, key);
        }
        return 0.0;
    }
}
