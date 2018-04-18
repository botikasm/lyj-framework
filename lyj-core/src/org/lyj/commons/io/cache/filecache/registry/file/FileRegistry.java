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

import org.lyj.Lyj;
import org.lyj.commons.io.cache.filecache.registry.IRegistry;
import org.lyj.commons.io.cache.filecache.registry.IRegistryItem;
import org.lyj.commons.io.db.filedb.FileDB;
import org.lyj.commons.io.db.filedb.FileDBCollection;
import org.lyj.commons.io.db.filedb.FileDBEntity;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class FileRegistry
        implements IRegistry {

    private static final long DEFAULT_LIFE = 60 * 1000; // 1 minute

    private static final String CHECK_MS = "check_ms";

    private final String _path_settings;
    private final String _path_data;

    private final JsonWrapper _settings;
    private Thread _registryThread;

    private FileDB _db;
    private FileDBCollection _collection;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileRegistry(final String path_settings,
                        final String path_data) {
        _path_settings = path_settings;
        _path_data = path_data;

        _settings = new JsonWrapper(this.loadSettings(_path_settings));
    }

    // ------------------------------------------------------------------------
    //                      s e t t i n g s
    // ------------------------------------------------------------------------

    public void reloadSettings() {
        _settings.parse(this.loadSettings(_path_settings));
    }

    public long getCheck() {
        synchronized (_settings) {
            return _settings.optLong(CHECK_MS);
        }
    }

    public void setCheck(final long value) {
        synchronized (_settings) {
            try {
                _settings.putSilent(CHECK_MS, value);
                this.saveSettings();
            } catch (Throwable ignored) {
            }
        }
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void start() {
        this.interrupt();
        this.initDB();
        _registryThread = this.startRegistryThread(this);
    }

    public void interrupt() {
        try {
            if (null != _registryThread) {
                _registryThread.interrupt();
            }
        } catch (Throwable ignored) {

        } finally {
            _registryThread = null;
        }
    }

    public void join() {
        if (null != _registryThread) {
            try {
                _registryThread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void clear() {
        this.reloadSettings();
        this.resetDB();
    }

    public void save() throws IOException {

    }

    public boolean trySave() {
        try {

            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public boolean has(final String key) {
        if (null != _collection) {
            return _collection.exists(key);
        }
        return false;
    }

    public FileRegistryItem get(final String key) {
        if (null != _collection) {
            return new FileRegistryItem(_collection.get(key));
        }
        return null;
    }

    public boolean addItem(final String path,
                           final long duration) throws Exception {
        final String key = FileRegistryItem.getId(path);
        return this.addItem(key, path, duration);
    }

    public boolean addItem(final String key,
                           final String path,
                           final long duration) throws Exception {

        if (null != _collection) {
            final FileRegistryItem item = new FileRegistryItem();
            item.key(key);
            item.uid(key);
            item.path(path);
            item.duration(duration);

            _collection.upsert(item);
        }

        return false;
    }

    public boolean removeItem(final IRegistryItem item) throws Exception {
        final String key = item.uid();
        return this.removeItemByKey(key, true);
    }

    public boolean removeItem(final String key) throws Exception {
        return this.removeItemByKey(key, true);
    }

    public boolean removeItemByPath(final String path) throws Exception {
        return this.removeItemByPath(path, true);
    }

    public int removeExpired() throws Exception {
        final Set<String> expired = new HashSet<>();
        if (null != _collection) {
            _collection.forEach((entity) -> {
                final FileRegistryItem item = new FileRegistryItem(entity);
                if (item.expired() && item.isFileOrEmptyDir()) {
                    expired.add(item.key());
                }
                return false;
            });

            // remove expired items
            for (final String key : expired) {
                this.removeItem(key);
            }
        }
        return expired.size();
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void initDB() {
        // init database
        final String db_root = PathUtils.getParent(_path_data);
        final String collection_name = PathUtils.getFilename(_path_data, false);
        _db = new FileDB(db_root, "registry");
        _collection = _db.collection(collection_name);
    }

    private void resetDB() {
        if (null != _db) {
            FileUtils.tryDelete(_db.dbPath());
        }
        this.initDB();
    }

    private String loadData(final String fileName) {
        try {
            return FileUtils.readFileToString(new File(fileName));
        } catch (Throwable t) {
            return "{'items':{}}";
        }
    }

    private String loadSettings(final String fileName) {
        try {
            return FileUtils.readFileToString(new File(fileName));
        } catch (Throwable t) {
            return "{'" + CHECK_MS + "':" + DEFAULT_LIFE + "}";
        }
    }

    private void saveSettings() throws IOException {
        FileUtils.writeStringToFile(new File(_path_settings),
                _settings.toString(1),
                Lyj.getCharset());
    }

    private void removeFile(final String file) {
        try {
            FileUtils.delete(file);
        } catch (Throwable ignored) {
        }
    }

    private boolean removeItemByKey(final String key,
                                    final boolean remove_resource) throws Exception {
        if (null != _collection) {
            final FileDBEntity entity = _collection.remove(key);
            if (null != entity) {
                final FileRegistryItem item = new FileRegistryItem(entity);
                if (remove_resource) {
                    final String path = item.path();
                    if (StringUtils.hasText(path)) {
                        FileUtils.tryDelete(path);
                    }
                }
                return true;
            }

        }
        return false;
    }

    private boolean removeItemByPath(final String path,
                                     final boolean remove_resource) throws Exception {
        if (null != _collection) {
            final FileDBEntity entity = _collection.get((db_entity) -> (new FileRegistryItem(db_entity)).path().equalsIgnoreCase(path));
            if (null != entity) {
                if (remove_resource) {
                    if (StringUtils.hasText(path)) {
                        FileUtils.tryDelete(path);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private Thread startRegistryThread(final IRegistry registry) {
        // creates thread that check for expired items
        final Thread t = new Thread(new Runnable() {
            boolean _interrupted = false;

            @Override
            public void run() {
                while (!_interrupted) {
                    try {

                        //-- check registry items --//
                        if (registry.removeExpired() > 0) {
                            try {
                                registry.save();
                            } catch (Throwable ignored) {
                            }
                        }

                        final long sleep = registry.getCheck();
                        Thread.sleep(sleep);

                    } catch (Exception ignored) {
                        _interrupted = true;
                    }
                }
            }
        });
        t.setDaemon(true);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();

        return t;
    }


}
