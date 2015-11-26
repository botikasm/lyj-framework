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
 * ClassResourceBundle.java
 *
 */
package org.ly.commons.i18n.resourcebundle.bundle.impl;

import org.ly.commons.i18n.resourcebundle.bundle.IResourceBundle;
import org.ly.commons.util.LocaleUtils;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Wrapper for a standard ResourceBundle.
 *
 * @author
 */
public final class ClassResourceBundle
        implements IResourceBundle {

    private String _baseName;
    private Locale _locale;
    private Boolean _active;
    private ResourceBundle _rb;
    private Throwable _error;

    /**
     * Creates a new instance of ClassResourceBundle
     */
    public ClassResourceBundle(final String baseName,
                               final Locale locale, final ClassLoader classloader) {
        _active = false;
        _baseName = baseName;
        _locale = locale;
        final ClassLoader cl = null != classloader
                ? classloader
                : ClassLoader.getSystemClassLoader();
        try {
            _rb = null != locale
                    ? ResourceBundle.getBundle(baseName, locale, cl)
                    : ResourceBundle.getBundle(baseName, Locale.getDefault(), cl);
            if (null != locale && null != _rb) {
                final boolean match = LocaleUtils.like(locale, _rb.getLocale());
                if (!match) {
                    _rb = ResourceBundle.getBundle(baseName, Locale.getDefault(), cl);
                }
            }

            if (null == _rb) {
                throw new Exception("Unable to retrieve resources " +
                        "for current base name: " + baseName);
            }
            _active = true;
        } catch (Throwable t) {
            // if ResourceBundle not found
            _error = t;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _rb = null;
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Base Name: ").append(_baseName).
                append("; Locale: ").append(_locale.toString());
        if (null != _rb) {
            result.append("; Resources: ").append(_rb.toString());
        }

        return result.toString();
    }

    @Override
    public Throwable getError() {
        return _error;
    }

    @Override
    public String getString(String key) {
        if (null != _rb) {
            return _rb.getString(key);
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
        final Properties result = new Properties();
        if (null == _rb || !_active) {
            return result;
        }
        final Enumeration<String> keys = _rb.getKeys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            result.setProperty(key, _rb.getString(key));
        }
        return result;
    }
}
