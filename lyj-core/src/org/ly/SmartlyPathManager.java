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

package org.ly;

import org.ly.commons.io.jsonrepository.JsonRepository;
import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.util.PathUtils;
import org.ly.commons.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages path for smartly application and smartly modules.
 * Each module can have a custom configuration path.
 */
public final class SmartlyPathManager {

    private final Map<String, String> _paths;
    private final Map<String, JsonRepository> _configurations;

    private SmartlyPathManager() {
        _paths = Collections.synchronizedMap(new HashMap<String, String>());
        _configurations = Collections.synchronizedMap(new HashMap<String, JsonRepository>());
    }

    public String getPath(final Class aclass) {
        if (null != aclass) {
            return this.getPath(aclass.getName());
        }
        return null;
    }

    public String getPath(final String key) {
        if (StringUtils.hasText(key)) {
            return _paths.get(key);
        }
        return null;
    }

    public void putPath(final Class aclass, final String path) {
        if (null != aclass) {
            this.putPath(aclass.getName(), path);
        }
    }

    public void putPath(final String key, final String path) {
        synchronized (_paths) {
            _paths.put(key, path);
        }
    }

    public JsonRepository getConfig(final Class aclass) {
        if (null != aclass) {
            return this.getConfig(aclass.getName());
        }
        return null;
    }

    public JsonRepository getConfig(final String key) {
        if (StringUtils.hasText(key)) {
            return _configurations.get(key);
        }
        return null;
    }

    public void putConfig(final Class aclass, final JsonRepository config) {
        if (null != aclass) {
            this.putConfig(aclass.getName(), config);
        }
    }

    public void putConfig(final String key, final JsonRepository config) {
        synchronized (_configurations) {
            _configurations.put(key, config);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static SmartlyPathManager __instance;

    private static SmartlyPathManager getInstance() {
        if (null == __instance) {
            __instance = new SmartlyPathManager();
        }
        return __instance;
    }

    private static Logger getLogger() {
        return LoggingUtils.getLogger(SmartlyPathManager.class);
    }

    public static void put(final String key, final String path) {
        getInstance().putPath(key, path);
    }

    public static void put(final Class key, final String path) {
        getInstance().putPath(key, path);
    }

    public String get(final String key) {
        return getInstance().getPath(key);
    }

    public String get(final Class key) {
        return getInstance().getPath(key);
    }

    public static String getAbsolutePath(final String relativePath) {
        return PathUtils.getAbsolutePath(relativePath);
    }

    public static String getConfigurationPath() {
        return getAbsolutePath(IConstants.PATH_CONFIGFILES);
    }

    public static String getConfigurationPath(final Class acClass) {
        final String class_path = getInstance().getPath(acClass);
        if (StringUtils.hasText(class_path)) {
            return PathUtils.concat(class_path, IConstants.PATH_CONFIGFILES);
        } else {
            return getConfigurationPath();
        }
    }

    public static String getConfigurationPath(final String className) {
        final String class_path = getInstance().getPath(className);
        if (StringUtils.hasText(class_path)) {
            return PathUtils.concat(class_path, IConstants.PATH_CONFIGFILES);
        } else {
            return getConfigurationPath();
        }
    }

    public static JsonRepository getConfiguration(final Class key) {
        if (null != key) {
            return getConfiguration(key.getName());
        }
        return null;
    }

    public static JsonRepository getConfiguration(final String key) {
        final SmartlyPathManager instance = getInstance();
        JsonRepository result = instance.getConfig(key);
        if (null == result) {
            try {
                final String path = getConfigurationPath(key);
                result = new JsonRepository(path);
                instance.putConfig(key, result);
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, null, t);
            }
        }
        return result;
    }

}
