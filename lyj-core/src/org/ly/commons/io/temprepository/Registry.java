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
import org.ly.Smartly;
import org.ly.commons.cryptograph.MD5;
import org.ly.commons.util.FileUtils;
import org.ly.commons.util.JsonWrapper;
import org.ly.commons.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 *
 */
public class Registry {

    private static final long DEFAULT_LIFE = 60 * 1000; // 1 minute

    private static final String LIFE_MS = "life_ms";
    private static final String CHECK_MS = "check_ms";
    private static final String ITEMS = "items";

    private final String _path_settings;
    private final String _path_data;
    private final JsonWrapper _data;
    private final JsonWrapper _settings;
    private Thread _registryThread;

    public Registry(final String path_settings,
                    final String path_data) {
        _path_settings = path_settings;
        _path_data = path_data;

        _data = new JsonWrapper(this.loadData(_path_data));
        _settings = new JsonWrapper(this.loadSettings(_path_settings));
    }

    public void start() {
        this.startRegistryThread();
    }

    public void interrupt() {
        if (null != _registryThread) {
            _registryThread.interrupt();
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
        synchronized (_data) {
            _data.putSilent(ITEMS, new JSONObject());
        }
    }

    public void reloadSettings() {
        _settings.parse(this.loadSettings(_path_settings));
    }

    public long getLife() {
        final long result = _settings.optLong(LIFE_MS);
        return result > 0 ? result : DEFAULT_LIFE;
    }

    public void setLife(final long value) {
        _settings.putSilent(LIFE_MS, value);
        if (_settings.optLong(CHECK_MS) < value) {
            _settings.putSilent(CHECK_MS, value);
        }
        try {
            this.saveSettings();
        } catch (Throwable ignored) {
        }
    }

    public long getCheck() {
        final long result = _settings.optLong(CHECK_MS);
        return result > getLife() ? result : getLife();
    }

    public void setCheck(final long value) {
        _settings.putSilent(CHECK_MS, value);
        try {
            this.saveSettings();
        } catch (Throwable ignored) {
        }
    }

    public void save() throws IOException {
        this.saveData();
    }

    public boolean addItem(final String path) {
        synchronized (_data) {
            final String key = getId(path);
            final JSONObject items = _data.optJSONObject(ITEMS);
            if (!items.has(key)) {
                JsonWrapper.put(items, key, (new RegistryItem(path)).getData());
                _data.putOpt(ITEMS, items);
                return true;
            }
            return false;
        }
    }

    public boolean removeItem(final String path) {
        synchronized (_data) {
            final String key = getId(path);
            return this.removeItemByKey(key);
        }
    }

    public int removeExpired() {
        synchronized (_data) {
            int count = 0;
            final long life = this.getLife();
            final JsonWrapper items = new JsonWrapper(_data.optJSONObject(ITEMS));
            final Set<String> keys = items.keys();
            for (final String key : keys) {
                final RegistryItem item = new RegistryItem(items.optJSONObject(key));
                if (item.expired(life)) {
                    items.remove(key);
                    // remove file
                    this.removeFile(item.getPath());
                    count++;
                }
            }
            return count;
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

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
            return "{'" + LIFE_MS + "':" + DEFAULT_LIFE + ", '" +
                    CHECK_MS + "':" + DEFAULT_LIFE +
                    "}";
        }
    }

    private void saveData() throws IOException {
        FileUtils.writeStringToFile(new File(_path_data),
                _data.toString(1),
                Smartly.getCharset());
    }

    private void saveSettings() throws IOException {
        FileUtils.writeStringToFile(new File(_path_settings),
                _settings.toString(1),
                Smartly.getCharset());
    }

    private void removeFile(final String file) {
        try {
            FileUtils.delete(file);
        } catch (Throwable ignored) {
        }
    }

    public boolean removeItemByKey(final String key) {
        final JSONObject items = _data.optJSONObject(ITEMS);
        return null != JsonWrapper.remove(items, key);
    }

    private void startRegistryThread() {
        // creates thread that check for expired items
        _registryThread = new Thread(new Runnable() {
            boolean _interrupted = false;

            @Override
            public void run() {
                while (!_interrupted) {
                    try {
                        final long sleep = getCheck();
                        Thread.sleep(sleep);
                        //-- check registry items --//
                        if (removeExpired() > 0) {
                            try {
                                save();
                            } catch (Throwable ignored) {
                            }
                        }
                    } catch (InterruptedException ignored) {
                        _interrupted = true;
                    }
                }
            }
        });
        _registryThread.setDaemon(true);
        _registryThread.setPriority(Thread.NORM_PRIORITY);
        _registryThread.start();
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static String getId(final String path) {
        return MD5.encode(PathUtils.toUnixPath(path));
    }

}
