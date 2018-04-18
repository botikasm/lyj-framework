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

package org.lyj.commons.io.cache.filecache.registry.file;

import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.io.cache.filecache.registry.IRegistryItem;
import org.lyj.commons.io.db.filedb.FileDBEntity;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;

/**
 *
 */
public class FileRegistryItem
        extends FileDBEntity
        implements IRegistryItem {

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

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileRegistryItem() {
        this.init();
    }


    public FileRegistryItem(final Object item) {
        super(item);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public FileRegistryItem path(final String path) {
        super.put(PATH, PathUtils.toUnixPath(path));
        super.put(TIMESTAMP, System.currentTimeMillis());
        if (!super.has(UID)) {
            this.uid(getId(this.path()));
        }

        return this;
    }

    public String path() {
        return super.getString(PATH);
    }

    public String uid() {
        return super.getString(UID);
    }

    public FileRegistryItem uid(final String value) {
        super.put(UID, value);
        return this;
    }

    public long timestamp() {
        return super.getLong(TIMESTAMP);
    }

    public long duration() {
        return super.getLong(DURATION);
    }

    public FileRegistryItem duration(final long value) {
        super.put(DURATION, value);
        return this;
    }

    public boolean expired() {
        final long now = System.currentTimeMillis();
        final long life = now - this.timestamp();
        final long duration = this.duration();
        return life > duration;
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

    private void init() {
        if (!super.has(DURATION)) {
            super.put(DURATION, DEFAULT_LIFE);
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static String getId(final String path) {
        return MD5.encode(PathUtils.toUnixPath(path));
    }

}
