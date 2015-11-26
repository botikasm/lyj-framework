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

package org.ly.commons.i18n.impl;


import org.ly.commons.i18n.resourcebundle.AbstractI18nBundle;
import org.ly.commons.util.FormatUtils;
import org.ly.commons.util.LocaleUtils;
import org.ly.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChainDictionary
        extends BaseDictionary {

    private static final String NAME = "base";

    private final AbstractI18nBundle _super;
    private final Map<String, Map<String, String>> _dictionaries;

    public ChainDictionary() {
        _super = this.getSuperclassInstance();
        _dictionaries = new HashMap<String, Map<String, String>>();
    }

    public String getName() {
        return NAME;
    }

    public boolean hasNext() {
        return null != _super;
    }

    public AbstractI18nBundle next() {
        return _super;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getMessage(final String key,
                             final Locale locale,
                             final ClassLoader classloader,
                             final Object... args) {
        // retrieve a dictionary
        final Map<String, String> dic = this.getDictionary(locale);
        // try dictionary
        String result = dic.get(key);
        // try current bundle
        if (!StringUtils.hasText(result)) {
            result = super.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // try parent bundle
        if (!StringUtils.hasText(result)) {
            result = _super.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // return formatted result
        if (StringUtils.hasText(result)) {
            if (args.length > 0) {
                return FormatUtils.format(result, args);
            } else {
                return result;
            }
        }
        return "";
    }

    public String getMessage(final String key,
                             final Locale locale,
                             final ClassLoader classloader,
                             final Map<String, ?> args) {
        // retrieve a dictionary
        final Map<String, String> dic = this.getDictionary(locale);
        // try dictionary
        String result = dic.get(key);
        // try current bundle
        if (!StringUtils.hasText(result)) {
            result = super.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // try parent bundle
        if (!StringUtils.hasText(result)) {
            result = _super.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // return formatted result
        if (StringUtils.hasText(result)) {
            if (null != args && !args.isEmpty()) {
                return FormatUtils.format(result, args);
            } else {
                return result;
            }
        }
        return "";
    }

    @Override
    public String getMessage(final String key,
                             final Locale locale,
                             final Object... args) {
        return this.getMessage(key,
                locale, BaseDictionary.getClassLoader(), args);
    }

    @Override
    public String getMessage(final String key,
                             final String slocale,
                             final Object... args) {
        final Locale locale = LocaleUtils.getLocaleFromString(slocale);
        return this.getMessage(key, locale, args);
    }

    @Override
    public String getMessage(final String key,
                             final Locale locale,
                             final Map<String, ?> args) {
        return this.getMessage(key,
                locale, BaseDictionary.getClassLoader(), args);
    }

    @Override
    public String getMessage(final String key,
                             final String slocale,
                             final Map<String, ?> args) {
        final Locale locale = LocaleUtils.getLocaleFromString(slocale);
        return this.getMessage(key, locale, args);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Map<String, String> getDictionary(final Locale locale) {
        final String key = locale.toString();
        if (_dictionaries.containsKey(key)) {
            return _dictionaries.get(key);
        }
        final Map<String, String> result = new HashMap<String, String>();
        _dictionaries.put(key, result);
        return result;
    }

    private AbstractI18nBundle getSuperclassInstance() {
        AbstractI18nBundle result = null;
        try {
            result = (AbstractI18nBundle) this.getClass().getSuperclass().newInstance();
        } catch (Throwable ignored) {
        }
        return null != result ? result : getInstance();
    }

    public String lookup(final String key,
                         final Locale locale,
                         final ClassLoader classloader) {
        // retrieve a dictionary
        final Map<String, String> dic = this.getDictionary(locale);
        // try dictionary
        String result = dic.get(key);
        // try current bundle
        if (!StringUtils.hasText(result)) {
            result = super.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // try parent bundle
        if (!StringUtils.hasText(result)) {
            result = _super.getMessage(key, locale, classloader);
            dic.put(key, result);
        }

        return result;
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


}
