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
 * 
 */
package org.ly.commons.i18n.resourcebundle;

import org.ly.commons.i18n.resourcebundle.bundle.ResourceBundleManager;
import org.ly.commons.util.ClassLoaderUtils;
import org.ly.commons.util.PathUtils;
import org.ly.commons.util.StringUtils;

import java.util.Locale;
import java.util.Properties;


/**
 * Extends this class for a localized class.<br>
 *
 * @author
 */
public abstract class AbstractI18nBundle {

    private final Class _refereeClass;
    private boolean _lookupForFileResource = false;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public AbstractI18nBundle() {
        _refereeClass = this.getClass();
    }

    public AbstractI18nBundle(final Class refereeClass) {
        _refereeClass = refereeClass;
    }

    // --------------------------------------------------------------------
    //               p r o p e r t i e s
    // --------------------------------------------------------------------

    public void setLookupForFileResource(final boolean value) {
        _lookupForFileResource = value;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getMessage(final String key,
                             final Locale locale,
                             final ClassLoader classloader) {
        return this.validate(ResourceBundleManager.getString(_refereeClass,
                key,
                null != locale ? locale : Locale.ENGLISH,
                classloader));
    }

    public Properties getProperties(final Locale locale,
                                    final ClassLoader classloader) {
        final String classPath = PathUtils.getClassPath(_refereeClass);
        try {
            return ResourceBundleManager.getProperties(classPath,
                    null != locale ? locale : Locale.ENGLISH,
                    classloader);
        } catch (Exception ex) {
            return new Properties();
        }
    }

    public abstract String getName();

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private String validate(final String value) {
        // should check if value is a file resource?
        if (_lookupForFileResource) {
            if (StringUtils.hasText(PathUtils.getFilenameExtension(value))) {
                try {
                    return this.readFile(value);
                } catch (Throwable ignored) {
                }
            }
        }

        return value;
    }

    private String readFile(final String fileName) throws Exception {
        final String result = ClassLoaderUtils.getResourceAsString(null, this.getClass(), fileName);
        if (null == result) {
            throw new Exception("not a file");
        }
        return result;
    }

}
