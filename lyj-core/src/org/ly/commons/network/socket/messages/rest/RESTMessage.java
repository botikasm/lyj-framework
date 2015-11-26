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

package org.ly.commons.network.socket.messages.rest;

import org.json.JSONObject;
import org.ly.commons.network.socket.messages.AbstractMessage;
import org.ly.commons.util.StringUtils;

import java.util.Date;

/**
 * Wrapper for REST like message.
 */
public class RESTMessage
        extends AbstractMessage {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private String _method;
    private String _path;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public RESTMessage() {
        _method = METHOD_GET;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("CreationDate: ").append(new Date(this.getCreationDate()));
        sb.append(", ");
        sb.append("Elapsed: ").append(this.getElapsedTime());
        sb.append(", ");
        sb.append("UserToken: ").append(this.getUserToken());
        sb.append(", ");
        sb.append("Method: ").append(this.getMethod());
        sb.append(", ");
        sb.append("Path: ").append(this.getPath());
        sb.append("}");
        return sb.toString();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void setPath(final String value) {
        _path = value;
    }

    public String getPath() {
        return _path;
    }

    public void setMethod(final String value) {
        _method = value;
    }

    public String getMethod() {
        return _method;
    }

    public RESTMessage setData(final String json) {
        if (StringUtils.isJSONObject(json)) {
            super.setData(json);
        }
        return this;
    }

    public RESTMessage setData(final JSONObject json) {
        super.setData(json.toString());
        return this;
    }

    public String getData() {
        return super.getDataString();
    }

    public JSONObject getDataAsJSON() {
        final String data = this.getData();
        if (StringUtils.isJSONObject(data)) {
            return new JSONObject(data);
        }
        return new JSONObject();
    }
}
