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

package org.ly.commons.i18n;

import org.ly.Smartly;
import org.ly.commons.i18n.resourcebundle.AbstractI18nBundle;
import org.ly.commons.util.FormatUtils;
import org.ly.commons.util.LocaleUtils;
import org.ly.commons.util.StringUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Dictionary helper class
 */
public class Dictionary {

    public static final String NAME = "dic"; // velocity name

    public Dictionary() {
    }

    public String getName() {
        return NAME;
    }

    public String get(final String lang, final String key) {
        return Dictionary.getMessage(lang, key);
    }

    public String get(final String lang, final String key, final Object... args) {
        return Dictionary.getMessage(lang, key, args);
    }

    public String get(final String lang, final String dicName, final String key) {
        return Dictionary.getMessage(lang, dicName, key);
    }

    public String get(final String lang, final String dicName, final String key, final Object... args) {
        return Dictionary.getMessage(lang, dicName, key, args);
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static String getMessage(final String lang,
                                    final String key,
                                    final Object... args) {
        final Locale locale = LocaleUtils.getLocaleFromString(toLangString(lang));
        return getMessage(locale, key, args);
    }

    public static String getMessage(final String lang,
                                    final String key,
                                    final Map<String, ? extends Object> args) {
        final Locale locale = LocaleUtils.getLocaleFromString(toLangString(lang));
        return getMessage(locale, key, args);
    }

    public static String getMessage(final Locale locale,
                                    final String key,
                                    final Object... args) {
        final String resource = lookup(key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    public static String getMessage(final Locale locale,
                                    final String key,
                                    final Map<String, ? extends Object> args) {
        final String resource = lookup(key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    public static String getMessage(final String lang,
                                    final String dicName,
                                    final String key,
                                    final Object... args) {
        final Locale locale = LocaleUtils.getLocaleFromString(toLangString(lang));
        return getMessage(locale, dicName, key, args);
    }

    public static String getMessage(final String lang,
                                    final String dicName,
                                    final String key,
                                    final Map<String, ? extends Object> args) {
        final Locale locale = LocaleUtils.getLocaleFromString(toLangString(lang));
        return getMessage(locale, dicName, key, args);
    }

    public static String getMessage(final Locale locale,
                                    final String dicName,
                                    final String key,
                                    final Object... args) {
        final String resource = lookup(dicName, key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    public static String getMessage(final Locale locale,
                                    final String dicName,
                                    final String key,
                                    final Map<String, ? extends Object> args) {
        final String resource = lookup(dicName, key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String lookup(final String key, final Locale locale) {
        final Collection<AbstractI18nBundle> list = DictionaryRegistry.getDictionaries();
        for (final AbstractI18nBundle dic : list) {
            final String resource = dic.getMessage(key, locale, dic.getClass().getClassLoader());
            if (StringUtils.hasText(resource)) {
                return resource;
            }
        }
        return "";
    }

    private static String lookup(final String dicName, final String key, final Locale locale) {
        final AbstractI18nBundle dic = DictionaryRegistry.getDictionary(dicName);
        if (null != dic) {
            return dic.getMessage(key, locale, dic.getClass().getClassLoader());
        }
        return "";
    }

    private static String toLangString(final Object lang) {
        return null != lang ? lang.toString() : Smartly.getLang();
    }
}
