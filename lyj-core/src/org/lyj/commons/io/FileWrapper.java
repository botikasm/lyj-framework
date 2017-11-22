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

package org.lyj.commons.io;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.*;
import org.lyj.commons.util.json.JsonWrapper;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Serializable File descriptor
 */
public final class FileWrapper
        extends JSONObject {

    // --------------------------------------------------------------------
    //               c o n s t a n t s
    // --------------------------------------------------------------------

    public static final String TYPE_FILE = "file";
    public static final String TYPE_DIRECTORY = "directory";

    private static final String HASH = "hash";
    private static final String ABSOLUTE_PATH = "absolute_path";
    private static final String TYPE = "type";
    private static final String CHILDREN = "children";
    private static final String LENGTH = "length";
    private static final String CRC = "crc";
    private static final String NAME = "name";
    private static final String EXT = "ext";
    private static final String DESCRIPTION = "description";

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public FileWrapper(final String json) {
        super(json);
    }

    public FileWrapper(final File file) {
        this.init(file);
    }

    // --------------------------------------------------------------------
    //               p r o p e r t i e s
    // --------------------------------------------------------------------

    public String getHash() {
        return JsonWrapper.getString(this, HASH);
    }

    public FileWrapper setHash(final String value) {
        JsonWrapper.put(this, HASH, value);
        return this;
    }

    public String getAbsolutePath() {
        return JsonWrapper.getString(this, ABSOLUTE_PATH);
    }

    public FileWrapper setAbsolutePath(final String value) {
        JsonWrapper.put(this, ABSOLUTE_PATH, value);
        return this;
    }

    public String getType() {
        return JsonWrapper.getString(this, TYPE);
    }

    public FileWrapper setType(final String value) {
        JsonWrapper.put(this, TYPE, value);
        return this;
    }

    public String getName() {
        return JsonWrapper.getString(this, NAME);
    }

    public FileWrapper setName(final String value) {
        if(StringUtils.hasText(PathUtils.getFilenameExtension(value))){
            JsonWrapper.put(this, NAME, PathUtils.getFilename(value, false));
            this.setExt(PathUtils.getFilenameExtension(value, true));
        } else {
            JsonWrapper.put(this, NAME, value);
        }
        return this;
    }

    public String getDescription() {
        return JsonWrapper.getString(this, DESCRIPTION);
    }

    public FileWrapper setDescription(final String value) {
        JsonWrapper.put(this, DESCRIPTION, value);
        return this;
    }

    public String getExt() {
        return JsonWrapper.getString(this, EXT);
    }

    public FileWrapper setExt(final String value) {
        JsonWrapper.put(this, EXT, value);
        return this;
    }

    public long getLength() {
        return JsonWrapper.getLong(this, LENGTH);
    }

    public FileWrapper setLength(final long value) {
        JsonWrapper.put(this, LENGTH, value);
        return this;
    }

    public long getCRC() {
        return JsonWrapper.getLong(this, CRC);
    }

    public FileWrapper setCRC(final long value) {
        JsonWrapper.put(this, CRC, value);
        return this;
    }

    public JSONArray getChildren() {
        return JsonWrapper.getArray(this, CHILDREN);
    }

    public List<FileWrapper> getChildrenAsList() {
        final JSONArray children = this.getChildren();
        final int len = children.length();
        if (len > 0) {
            final List<FileWrapper> result = new LinkedList<FileWrapper>();
            for (int i = 0; i < len; i++) {
                final Object item = children.get(i);
                if (item instanceof FileWrapper) {
                    result.add((FileWrapper) item);
                } else {
                    result.add(new FileWrapper(item.toString()));
                }
            }
            return result;
        } else {
            return new LinkedList<FileWrapper>();
        }
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public boolean isValid() {
        return StringUtils.hasText(this.getHash());
    }

    public boolean isDirectory() {
        return this.getType().equalsIgnoreCase(TYPE_DIRECTORY);
    }

    public boolean hasChildren() {
        return this.getChildren().length() > 0;
    }

    public void addChildren(final FileWrapper file) {
        this.getChildren().put(file);
    }

    public String getFullName() {
        if (this.isDirectory()) {
            return this.getName();
        } else {
            return this.getName().concat(this.getExt());
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void init(final File file) {
        // init children container
        JsonWrapper.put(this, CHILDREN, new JSONArray());

        final String path = file.getAbsolutePath();
        final String type = PathUtils.isFile(file.getAbsolutePath()) ? TYPE_FILE : TYPE_DIRECTORY;

        this.setAbsolutePath(path);
        this.setType(type);
        this.setName(PathUtils.getFilename(path, false));
        this.setExt(PathUtils.getFilenameExtension(path, true));

        if (TYPE_DIRECTORY.equalsIgnoreCase(type)) {
            this.initDir(file);
        } else {
            this.setLength(file.length());
            this.setCRC(FileUtils.getCRC(file));
        }
        this.setHash(createUUID());
        this.setDescription(describe(this));
    }

    private void initDir(final File dir) {
        long length = 0;
        final File[] files = dir.listFiles();
        if (null != files && files.length > 0) {
            for (final File file : files) {
                if (null != file) {
                    final FileWrapper child = new FileWrapper(file);
                    this.addChildren(child);
                    length += child.getLength();
                }
            }
        }
        this.setLength(length);
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------


    public static String createUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String describe(final FileWrapper file) {
        final StringBuilder sb = new StringBuilder();
        if (file.isDirectory()) {
            // DIRECTORY
            sb.append("*Directory* ").append("\n");
            sb.append("_Size:_ ").append(file.getLength()).append(" (").append(ConversionUtils.bytesToMbyte(file.getLength(), 3)).append(" Mb)").append(" \n");
            sb.append("_Size:_ ").append(file.getLength()).append(" \n");
        } else {
            sb.append("*File* ").append("\n");
            sb.append("_Size:_ ").append(file.getLength()).append(" (").append(ConversionUtils.bytesToMbyte(file.getLength(), 3)).append(" Mb)").append(" \n");
            sb.append("_CRC:_ ").append(file.getCRC()).append(" \n");
        }
        return sb.toString();
    }
}
