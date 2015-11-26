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

package org.ly.commons.io.temprepository;

import org.json.JSONObject;
import org.ly.commons.util.JsonWrapper;
import org.ly.commons.util.PathUtils;

/**
 *
 */
public class RegistryItem {

    private static final String PATH = "path";
    private static final String TIMESTAMP = "timestamp";

    private final JsonWrapper _data;
    private String _path;

    public RegistryItem() {
        _data = new JsonWrapper(new JSONObject());
    }

    public RegistryItem(final String path) {
        _data = new JsonWrapper(new JSONObject());
        this.setPath(path);
    }

    public RegistryItem(final JSONObject item) {
        _data = new JsonWrapper(item);
    }

    public JSONObject getData() {
        return _data.getJSONObject();
    }

    public void setPath(final String path) {
        _data.putSilent(PATH, PathUtils.toUnixPath(path));
        _data.putSilent(TIMESTAMP, System.currentTimeMillis());
    }

    public String getPath() {
        return _data.optString(PATH);
    }

    public long getTimestamp() {
        return _data.optLong(TIMESTAMP);
    }

    public boolean expired(final long duration) {
        final long now = System.currentTimeMillis();
        return now - this.getTimestamp() > duration;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
