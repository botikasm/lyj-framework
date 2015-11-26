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
package org.ly.commons.io.serialization.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.ly.commons.io.serialization.json.utils.JsonBeanUtils;

/**
 * Simple serializer/deserializer from java bean to json string and from
 * json string to JSONObject (or Exception or List of JSONObject)
 *
 * @author angelo.geminiani
 */
public class JsonSerializer {

    private static final String TAG_TYPE = JsonBeanUtils.TAG_TYPE;
    private static final String OBJECT = JsonBeanUtils.TYPE_OBJECT;
    private static final String ERROR = JsonBeanUtils.TYPE_ERROR;

    public static String serialize(final Object object) throws JSONException {
        final JsonBean result = new JsonBean(object);
        return result.toString();
    }

    /**
     * Deserialize JSON string. Return:<br/>
     * <ul>
     * <li>Exception</li><br/>
     * <li>JSONObject</li><br/>
     * <li>List of JSONObject</li><br/>
     * </ul>
     *
     * @param jsontext
     * @return
     */
    public static Object deserialize(final String jsontext) {
        final JsonBean result = new JsonBean(jsontext);
        return result.asObject();
    }

    public static boolean isError(final String json) {
        try {
            return isError(new JSONObject(json));
        } catch (Throwable t) {
        }
        return false;
    }

    public static boolean isError(final JSONObject json) {
        final String classType = json.optString(TAG_TYPE, null);
        if (null != classType) {
            return classType.equals(ERROR);
        }
        return false;
    }

    public static boolean isObject(final String json) {
        try {
            return isObject(new JSONObject(json));
        } catch (Throwable t) {
        }
        return false;
    }

    public static boolean isObject(final JSONObject json) {
        final String classType = json.optString(TAG_TYPE, null);
        if (null != classType) {
            return classType.equals(OBJECT);
        }
        return false;
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
}
