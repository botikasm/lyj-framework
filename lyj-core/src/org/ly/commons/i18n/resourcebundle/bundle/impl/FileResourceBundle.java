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

/*
 * FileResourceBundle.java
 *
 */
package org.ly.commons.i18n.resourcebundle.bundle.impl;

import org.ly.commons.i18n.resourcebundle.bundle.IResourceBundle;
import org.ly.commons.logging.Level;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * This ResourceBundle load properties from file.
 *
 * @author
 */
public final class FileResourceBundle
        implements IResourceBundle {

    private static final String EXTENSION_SEPARATOR = ".";
    private static final String EXTENSION = ".properties";
    private Boolean _active = false;
    private Properties _properties;
    private Throwable _error;

    /**
     * Creates a new instance of FileResourceBundle
     *
     * @param path   Valid file path. Can include extension or not.
     *               If file has an extension different from ".properties", it
     *               will be replaced with ".properties".<br>
     *               Locale's details, like Language_Country_Variant, are added automatically.
     * @param locale desired Locale.
     */
    public FileResourceBundle(final String path,
                              final Locale locale) {
        _properties = new Properties();
        if (StringUtils.hasText(path)) {
            final File file = this.solveFile(path, locale);
            if (null != file) {
                this.loadProperties(_properties, file);
            } else {
                LoggingUtils.getLogger(
                        FileResourceBundle.class.getName()).log(Level.FINEST,
                        String.format("Unable to find file [%s] for locale [%s]. "
                                + "Please, check file path or locale is not NULL.",
                                path, null != locale ? locale.toString() : "NULL"));
            }
        }
    }

    /**
     * Creates a new instance of FileResourceBundle
     */
    public FileResourceBundle(final File file) {
        _properties = new Properties();
        this.loadProperties(_properties, file);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _properties = null;
            _active = false;
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    @Override
    public Throwable getError() {
        return _error;
    }

    @Override
    public String getString(String key) {
        if (null != _properties) {
            return _properties.getProperty(key);
        } else {
            return null;
        }
    }

    @Override
    public boolean isActive() {
        return _active;
    }

    @Override
    public Properties getProperties() {
        Properties result = new Properties();
        if (null == _properties || !_active) {
            return result;
        }
        result.putAll(_properties);
        return result;
    }
    // ------------------------------------------------------------------------
    //                          p r i v a t e
    // ------------------------------------------------------------------------

    private void loadProperties(final Properties properties,
                                final File file) {
        if (null == file || !file.exists()) {
            return;
        }
        try {
            final FileInputStream reader = new FileInputStream(file);
            properties.load(reader);
            _active = true;
        } catch (Throwable t) {
            _error = t;
        }
    }

    private File solveFile(final String path, final Locale locale) {
        File file = null;
        final String clearPath = this.stripFilenameExtension(path);
        if (null != locale) {
            final String language = locale.getLanguage();
            final String country = locale.getCountry();
            final String variant = locale.getVariant();
            String testPath = null;

            // try with language_country_variant
            if (country.length() > 0 && variant.length() > 0) {
                testPath = clearPath.concat("_").concat(language).concat("_").concat(country).concat("_").concat(variant).concat(EXTENSION);
                file = new File(testPath);
                if (file.exists()) {
                    return file;
                }
            }

            // try with language_country
            if (country.length() > 0) {
                testPath = clearPath.concat("_").concat(language).concat("_").concat(country).concat(EXTENSION);
                file = new File(testPath);
                if (file.exists()) {
                    return file;
                }
            }

            // try with language
            if (language.length() > 0) {
                testPath = clearPath.concat("_").concat(language).concat(EXTENSION);
                file = new File(testPath);
                if (file.exists()) {
                    return file;
                }
            }
        }
        // if no Locale file was found, try with default one
        file = new File(clearPath.concat(EXTENSION));
        if (file.exists()) {
            return file;
        }
        return null;
    }

    private String stripFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        return sepIndex != -1 ? path.substring(0, sepIndex) : path;
    }
}
