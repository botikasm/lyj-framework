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

package org.ly.commons.network.socket.messages;

import org.json.JSONObject;
import org.ly.commons.util.JsonWrapper;

/**
 * Wrapper for custom data transfer between client and server
 *
 *
 */
public class UserToken {

    public static final String USER_ID = "userid";
    public static final String PROCESS_ID = "process_id";

    //-- file data --//
    public static final String SOURCE_ABSOLUTE_PATH = "source_absolute_path";
    public static final String TARGET_ABSOLUTE_PATH = "target_absolute_path";
    public static final String CRC = "crc";
    public static final String LENGTH = "length";

    //-- response data --//
    public static final String ELAPSED_TIME = "elapsed_time";
    public static final String ERROR = "error";
    public static final String DATA_LENGTH = "data_length";

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final JSONObject _json;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public UserToken() {
        _json = new JSONObject();
    }

    public UserToken(final String json) {
        _json = new JSONObject(json);
    }

    @Override
    public String toString() {
        return _json.toString();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public JSONObject toJSON() {
        return _json;
    }

    public String getUserId() {
        return JsonWrapper.getString(_json, USER_ID);
    }

    public void setUserId(final String value) {
        JsonWrapper.put(_json, USER_ID, value);
    }

    public String getProcessId() {
        return JsonWrapper.getString(_json, PROCESS_ID);
    }

    public void setProcessId(final String value) {
        JsonWrapper.put(_json, PROCESS_ID, value);
    }

    public void put(final String key, final Object value) {
        JsonWrapper.put(_json, key, value);
    }

    public Object get(final String key) {
        return JsonWrapper.get(_json, key);
    }

    //-- file data --//

    public String getSourceAbsolutePath() {
        return JsonWrapper.getString(_json, SOURCE_ABSOLUTE_PATH);
    }

    public void setSourceAbsolutePath(final String value) {
        JsonWrapper.put(_json, SOURCE_ABSOLUTE_PATH, value);
    }

    public String getTargetAbsolutePath() {
        return JsonWrapper.getString(_json, TARGET_ABSOLUTE_PATH);
    }

    public void setTargetAbsolutePath(final String value) {
        JsonWrapper.put(_json, TARGET_ABSOLUTE_PATH, value);
    }

    public long getCrc() {
        return JsonWrapper.getLong(_json, CRC);
    }

    public void setCrc(final long value) {
        JsonWrapper.put(_json, CRC, value);
    }

    public long getLength() {
        return JsonWrapper.getLong(_json, LENGTH);
    }

    public void setLength(final long value) {
        JsonWrapper.put(_json, LENGTH, value);
    }

    //-- response data --//

    public long getDataLength() {
        return JsonWrapper.getLong(_json, DATA_LENGTH);
    }

    public void setDataLength(final long value) {
        JsonWrapper.put(_json, DATA_LENGTH, value);
    }

    public long getElapsedTime() {
        return JsonWrapper.getLong(_json, ELAPSED_TIME);
    }

    public void setElapsedTime(final long value) {
        JsonWrapper.put(_json, ELAPSED_TIME, value);
    }

    public String getError() {
        return JsonWrapper.getString(_json, ERROR);
    }

    public void setError(final String value) {
        JsonWrapper.put(_json, ERROR, value);
    }

}
