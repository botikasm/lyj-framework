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

package org.lyj.commons.io.filecache.registry;

import org.json.JSONObject;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.PathUtils;

import java.io.File;

/**
 *
 */
public class RegistryItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final long DEFAULT_LIFE = 60 * 1000; // 1 minute

    private static final String UID = "uid";
    private static final String PATH = "path";
    private static final String TIMESTAMP = "timestamp";
    private static final String DURATION = "duration";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonWrapper _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RegistryItem() {
        _data = new JsonWrapper(new JSONObject());
        this.duration(DEFAULT_LIFE);
    }


    public RegistryItem(final JSONObject item) {
        _data = new JsonWrapper(item);
    }

    @Override
    public String toString() {
        return _data.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject json() {
        return _data.getJSONObject();
    }

    public RegistryItem path(final String path) {
        _data.putSilent(PATH, PathUtils.toUnixPath(path));
        _data.putSilent(TIMESTAMP, System.currentTimeMillis());
        if (!_data.has(UID)) {
            this.uid(getId(this.path()));
        }

        return this;
    }

    public String path() {
        return _data.optString(PATH);
    }

    public String uid() {
        return _data.optString(UID);
    }

    public RegistryItem uid(final String value) {
        _data.putSilent(UID, value);
        return this;
    }

    public long timestamp() {
        return _data.optLong(TIMESTAMP);
    }

    public long duration() {
        return _data.optLong(DURATION);
    }

    public RegistryItem duration(final long value) {
        _data.putSilent(DURATION, value);
        return this;
    }

    public boolean expired() {
        final long now = System.currentTimeMillis();
        return now - this.timestamp() > this.duration();
    }

    public boolean isDir() {
        return FileUtils.isDir(this.path());
    }

    public boolean isFileOrEmptyDir() {
        final File file = new File(this.path());
        if (file.isDirectory()) {
            return FileUtils.isEmptyDir(file, true);
        }
        return true;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static String getId(final String path) {
        return MD5.encode(PathUtils.toUnixPath(path));
    }

}
