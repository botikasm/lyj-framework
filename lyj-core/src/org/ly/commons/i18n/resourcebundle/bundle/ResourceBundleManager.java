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

package org.ly.commons.i18n.resourcebundle.bundle;


import org.ly.commons.i18n.utils.I18nUtils;
import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 *
 */
public abstract class ResourceBundleManager {


    // -----------------------------------------------------------------------
    //                  works with path + baseName
    // -----------------------------------------------------------------------
    public static String getString(final Class callerClass,
                                   final String baseName, final String resourceKey,
                                   final ClassLoader classloader) {
        return getString(callerClass,
                baseName,
                resourceKey,
                Locale.getDefault(),
                classloader);
    }

    public static String getString(final Class callerClass,
                                   final String baseName, final String resourceKey,
                                   final Locale locale, final ClassLoader classloader) {
        return getString(callerClass,
                baseName,
                resourceKey,
                locale,
                null,
                classloader);
    }

    public static String getString(final Class callerClass,
                                   final String baseName, final String resourceKey,
                                   final Locale locale, final String defaultValue,
                                   final ClassLoader classloader) {
        final String base = PathUtils.getPackagePath(callerClass).concat("/").concat(baseName);
        return getResourceString(base,
                resourceKey,
                locale,
                defaultValue,
                classloader);
    }

    public static String getString(final String path,
                                   final String baseName, final String resourceKey,
                                   final Locale locale, final String defaultValue,
                                   final ClassLoader classloader) {
        final String base = PathUtils.getClassPath(path).concat("/").concat(baseName);
        return getResourceString(base,
                resourceKey,
                locale,
                defaultValue,
                classloader);
    }

    // -----------------------------------------------------------------------
    //                  works with full path (full class name)
    // -----------------------------------------------------------------------
    public static String getString(final Class callerCalss,
                                   final String resourceKey, final ClassLoader classloader) {
        return getString(callerCalss,
                resourceKey,
                Locale.getDefault(),
                classloader);
    }

    public static String getString(final Class callerCalss,
                                   final String resourceKey, final Locale locale,
                                   final ClassLoader classloader) {
        return getString(callerCalss, resourceKey, locale, null, classloader);
    }

    public static String getString(final Class callerCalss,
                                   final String resourceKey, final Locale locale,
                                   final String defaultValue, final ClassLoader classloader) {
        final String base = PathUtils.getClassPath(callerCalss);
        return getResourceString(base,
                resourceKey,
                locale,
                defaultValue,
                classloader);
    }

    public static String getString(final String path,
                                   final String resourceKey, final ClassLoader classloader) {
        return getString(path, resourceKey, Locale.getDefault(), classloader);
    }

    public static String getString(final String path,
                                   final String resourceKey, final Locale locale,
                                   final ClassLoader classloader) {
        return getString(path, resourceKey, locale, null, classloader);
    }

    public static String getString(final String path,
                                   final String resourceKey, final Locale locale,
                                   final String defaultValue, final ClassLoader classloader) {
        final String base = PathUtils.getClassPath(path);
        return getResourceString(base, resourceKey, locale,
                defaultValue, classloader);
    }

    // ------------------------------------------------------------------------
    //                  m i s c.
    // ------------------------------------------------------------------------
    public static Properties getProperties(final String path,
                                           final String langCode, final ClassLoader classloader) throws Exception {
        final Locale locale = StringUtils.hasText(langCode)
                ? LocaleUtils.getLocaleFromString(langCode)
                : null;
        return getProperties(path, locale, classloader);
    }

    public static Properties getProperties(final String path,
                                           final Locale locale, final ClassLoader classloader) throws Exception {
        return getAllProperties(path, locale, classloader);
    }

    /**
     * Search a property file starting from a "startFolder".
     *
     * @param startFolder Folder where start file search.
     * @param baseName    Name of file without extension
     * @param locale      Locale to search for
     * @return Properties
     */
    public static Properties searchProperties(final String startFolder,
                                              final String baseName, final Locale locale,
                                              final ClassLoader classloader) {
        final String name = baseName.concat(".properties");
        final Properties result = new Properties();
        final List<File> fileList = new ArrayList<File>();
        FileUtils.listFiles(fileList, new File(startFolder), name);
        for (File file : fileList) {
            try {
                // system.properties
                final String fileName = file.getAbsolutePath();
                // accept only baseName (file.properties) and not localized names (file_en.properties)
                //if (PathUtils.isBaseName(fileName)) {
                final Properties props = ResourceBundleManager.getProperties(fileName,
                        locale,
                        classloader);
                if (null != props) {
                    result.putAll(props);
                }
                //}
            } catch (Exception ex) {
                getLogger().log(Level.SEVERE,
                        null,
                        ExceptionUtils.getRealCause(ex));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    //                  p r i v a t e
    // ------------------------------------------------------------------------
    private static Logger getLogger() {
        return LoggingUtils.getLogger(ResourceBundleManager.class.getName());
    }

    private static String getResourceString(final String baseName,
                                            final String resourceKey, final Locale locale,
                                            final String defaultValue, final ClassLoader classloader) {
        try {
            // retrieve a resource bundle
            IResourceBundle rb = I18nUtils.getOrCreateBundle(baseName,
                    locale,
                    classloader);

            final String result = rb.getString(resourceKey);
            return result != null ? result : defaultValue;
        } catch (Exception ex) {
            getLogger().log(Level.FINE,
                    String.format("Resource not found. BaseName='%s'; "
                            + "Key='%s'; Locale='%s'",
                            baseName, resourceKey, locale.toString()), ex);
        }
        return "";
    }

    private static Properties getAllProperties(final String baseName,
                                               final Locale locale,
                                               final ClassLoader classloader) throws Exception {
        // retrieve a resource bundle
        final IResourceBundle rb = I18nUtils.getOrCreateBundle(baseName,
                locale,
                classloader);

        return rb != null
                ? rb.getProperties()
                : null;
    }
}
