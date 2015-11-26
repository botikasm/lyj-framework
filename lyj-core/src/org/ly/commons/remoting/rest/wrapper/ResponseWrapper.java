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

package org.ly.commons.remoting.rest.wrapper;

import org.json.JSONObject;
import org.ly.commons.io.serialization.json.JsonBean;
import org.ly.commons.util.JsonWrapper;
import org.ly.commons.util.StringUtils;

/**
 * Wrap Response value into JSONObject
 */
public class ResponseWrapper {

    private static final String RESPONSE = "response";


    public static Object getResponse(final Object data) {
        if (StringUtils.isJSONObject(data)) {
            final JsonWrapper json = new JsonWrapper(data.toString());
            return json.get(RESPONSE);
        }
        return data;
    }

    /**
     * Serialize an object in JSON Object String.
     * Native values (int, boolean, etc..) are wrapped into JSON response object.
     *
     * @param data Data to wrap
     * @return JSONObject or JSONArray as String
     */
    public static String wrapToJSONString(final Object data) {
        if (StringUtils.isJSON(data)) {
            final JsonBean json = new JsonBean(data);
            return json.asJSONObject().toString();
        } else {
            return wrapToJSONResponse(data.toString()).toString();
        }
    }

    public static JSONObject wrapToJSONResponse(final String text) {
        final JSONObject json = new JSONObject();
        JsonWrapper.put(json, RESPONSE, text);
        return json;
    }

}
