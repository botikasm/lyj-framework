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
 * ResourceBundleUtils.java
 *
 */
package org.ly.commons.i18n.utils;

import org.ly.commons.i18n.resourcebundle.bundle.IResourceBundle;
import org.ly.commons.i18n.resourcebundle.bundle.impl.ClassResourceBundle;
import org.ly.commons.i18n.resourcebundle.bundle.impl.FileResourceBundle;
import org.ly.commons.i18n.resourcebundle.cache.ResourceBundleCache;

import java.util.Locale;

/**
 *
 */
public abstract class I18nUtils {

    public static IResourceBundle getOrCreateBundle(final String baseName,
                                                    final Locale locale,
                                                    final ClassLoader classloader) throws Exception {
        final String key = I18nUtils.buildResourceBundleKey(baseName, locale);

        // search rb in repository
        IResourceBundle result = ResourceBundleCache.get(key);
        // create rb and add to repository
        if (null == result) {
            result = new ClassResourceBundle(baseName, locale, classloader);
            if (result.isActive()) {
                ResourceBundleCache.add(key, result);
            } else {
                result = new FileResourceBundle(baseName, locale);
                if (result.isActive())
                    ResourceBundleCache.add(key, result);
            }
        }

        // rethrow bundle exception
        if (null != result && null != result.getError()) {
            throw new Exception(result.getError());
        }
        return result;
    }

    public static String buildResourceBundleKey(final String baseName,
                                                final Locale locale) {
        return null != locale
                ? baseName.concat(":").
                concat(locale.getDisplayCountry()).concat(":").
                concat(locale.getDisplayLanguage())
                : baseName;
    }

    public static String buildResourceKey(final String baseName,
                                          final String labelName,
                                          final Locale locale) {
        return null != locale
                ? baseName.concat(":").
                concat(labelName).concat(":").
                concat(locale.getDisplayCountry()).concat(":").
                concat(locale.getDisplayLanguage())
                : baseName.concat(":").concat(labelName);
    }

}
