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

package org.ly.commons.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * LinkedList of JSONObjects
 */
public class JsonList
        extends LinkedList<JSONObject> {

    public JsonList() {

    }

    public JsonList(final Collection<JSONObject> items) {
        super(items);
    }

    public JsonList(final JSONObject[] items) {
        super(Arrays.asList(items));
    }

    public JsonList(final JSONArray items) {
        final int length = items.length();
        for (int i = 0; i < length; i++) {
            try {
                final Object item = items.opt(i);
                super.add((JSONObject)item);
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public String toString() {
        final JSONArray array = new JSONArray(this);
        return array.toString();
    }

    public JSONArray toJSONArray() {
        return new JSONArray(this);
    }
}
