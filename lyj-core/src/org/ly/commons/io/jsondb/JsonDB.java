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

package org.ly.commons.io.jsondb;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.commons.Delegates;
import org.ly.commons.lang.CharEncoding;
import org.ly.commons.logging.Level;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.util.FileUtils;
import org.ly.commons.util.JsonWrapper;
import org.ly.commons.util.PathUtils;

import java.io.File;

/**
 *
 */
public class JsonDB {

    public static final String CHARSET = CharEncoding.UTF_8;

    // ------------------------------------------------------------------------
    //                      e v e n t s
    // ------------------------------------------------------------------------

    private static final Class EVENT_ERROR = Delegates.ExceptionCallback.class;

    // ------------------------------------------------------------------------
    //                      c o n s t a n t s
    // ------------------------------------------------------------------------

    private static final String COLLECTIONS = "collections";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Delegates.Handlers _eventHandlers;
    private final String _root;
    private final Object _syncObj;

    private String _db_root;
    private String _file_metadata;
    private JsonWrapper _matadata;
    private boolean _open;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JsonDB(final String root) {
        _eventHandlers = new Delegates.Handlers();
        _root = root;
        _syncObj = new Object();
    }

    public JsonDB open(final String name) {
        if (!_open) {
            try {
                _db_root = PathUtils.concat(_root, name);
                _file_metadata = PathUtils.concat(_db_root, "_metadata.json");
                _matadata = this.init();
                _open = true;
            } catch (Throwable t) {
                this.close();
                this.handle(t);
            }
        }
        return this;
    }

    public void close() {
        _file_metadata = null;
        _matadata = null;
        _open = false;
    }

    public boolean isOpen() {
        return _open;
    }

    public String getRoot() {
        return _db_root;
    }

    public JsonDBCollection collection(final String name) {
        this.collectionsMetadata(name, false);
        return new JsonDBCollection(this, name);
    }

    public JsonDBCollection dropCollection(final String name) {
        this.collectionsMetadata(name, true);
        return new JsonDBCollection(this, name);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void onError(final Delegates.ExceptionCallback handler) {
        _eventHandlers.add(handler);
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------


    void collectionsMetadata(final String collName, final boolean drop) {
        synchronized (_syncObj) {
            final JSONArray collections = _matadata.optJSONArray(COLLECTIONS);
            if (null != collections) {
                final int length = collections.length();
                if (drop) {
                    // remove
                    JsonWrapper.removeAll(collections, collName);
                } else {
                    // add
                    if (!JsonWrapper.contains(collections, collName)) {
                        collections.put(collName);
                    }
                }
                this.saveMetadata();
            }
        }
    }

    void handle(final Throwable t) {
        if (_eventHandlers.contains(EVENT_ERROR)) {
            _eventHandlers.triggerAsync(EVENT_ERROR, t);
        } else {
            LoggingUtils.getLogger(this).log(Level.SEVERE, null, t);
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JsonWrapper init() {
        JsonWrapper result = new JsonWrapper(new JSONObject());
        try {
            if (!FileUtils.exists(_file_metadata)) {
                // create empty
                result.put(COLLECTIONS, new JSONArray());
                // save
                FileUtils.mkdirs(_file_metadata);
                this.saveMetadata();
            } else {
                result = new JsonWrapper(this.readMetadata());
            }
        } catch (Throwable t) {
            this.handle(t);
        }
        return result;
    }


    private String readMetadata() {
        try {
            if (PathUtils.exists(_file_metadata)) {
                return FileUtils.readFileToString(new File(_file_metadata), CHARSET);
            }
        } catch (Throwable ignored) {

        }
        return null;
    }

    private boolean saveMetadata() {
        try {
            FileUtils.copy(_matadata.toString().getBytes(CHARSET), new File(_file_metadata));
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

}
