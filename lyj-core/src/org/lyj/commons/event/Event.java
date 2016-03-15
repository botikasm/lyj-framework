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

package org.lyj.commons.event;

import org.json.JSONObject;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import java.io.Serializable;

/**
 *
 *
 */
public class Event
        implements Serializable {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_TIMESTAMP = "timestamp";
    private static final String FLD_SENDER = "sender";
    private static final String FLD_NAME = "name";
    private static final String FLD_TAG = "tag";
    private static final String FLD_DATA = "data";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JSONObject _json;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Event() {
        this(null, null, null);
    }

    public Event(final Object sender,
                 final String name) {
        this(sender, name, null);
    }

    public Event(final Object sender,
                 final String name,
                 final Object data) {
        _json = new JSONObject();
        this.put(FLD_TIMESTAMP, System.currentTimeMillis());
        if (null != sender) {
            this.put(FLD_SENDER, sender);
        }
        if (StringUtils.hasText(name)) {
            this.put(FLD_NAME, name);
        } else {
            this.put(FLD_NAME, RandomUtils.randomUUID());
        }


        this.setData(data);
    }

    @Override
    public String toString() {
        return _json.toString();
    }

    public JSONObject toJSON() {
        return new JSONObject(_json.toString());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Object getSender() {
        return JsonWrapper.get(_json, FLD_SENDER);
    }

    public Event setSender(final Object value) {
        this.put(FLD_SENDER, value);
        return this;
    }

    public String getName() {
        return JsonWrapper.getString(_json, FLD_NAME);
    }

    public Event setName(final String value) {
        this.put(FLD_NAME, value);
        return this;
    }

    public String getTag() {
        return JsonWrapper.getString(_json, FLD_TAG);
    }

    public Event setTag(final String value) {
        if (StringUtils.hasText(value)) {
            this.put(FLD_TAG, value);
        }

        return this;
    }

    public Object getData() {
        return JsonWrapper.get(_json, FLD_DATA);
    }

    public Event setData(final Object data) {
        if (null != data) {
            this.put(FLD_DATA, data);
        }

        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static Event create() {
        return new Event();
    }

    public static Event create(final String name) {
        return new Event(null, name, null);
    }

    public static Event create(final Object sender) {
        return new Event(sender, null, null);
    }

    public static Event create(final Object sender, final String name) {
        return new Event(sender, name, null);
    }

    public static Event create(final Object sender, final String name, final Object data) {
        return new Event(sender, name, data);
    }


}
