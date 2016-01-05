package org.lyj.commons.i18n;

import org.lyj.Lyj;
import org.lyj.commons.i18n.resourcebundle.AbstractI18nBundle;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Global static Dictionary Helper.
 * Extend this class to have a dictionary controller for your application.
 */
public class DictionaryController {


    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DictionaryController() {

        //-- register all dictionaries in subclass constructor --//


    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    public String get(final String lang,
                      final String key,
                      final Object... args) {
        final Locale locale = LocaleUtils.getLocaleFromString(toLangString(lang));
        return get(locale, key, args);
    }

    public String get(final String lang,
                      final String key,
                      final Map<String, Object> args) {
        final Locale locale = LocaleUtils.getLocaleFromString(toLangString(lang));
        return get(locale, key, args);
    }

    public String get(final Locale locale,
                      final String key,
                      final Object... args) {
        final String resource = lookup(key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    public String get(final Locale locale,
                      final String key,
                      final Map<String, Object> args) {
        final String resource = lookup(key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }


    // ------------------------------------------------------------------------
    //                     p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void register(final Class<? extends AbstractI18nBundle> dic) {
        DictionaryRegistry.register(dic);
    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private String toLangString(final Object lang) {
        return null != lang ? lang.toString() : Lyj.getLang();
    }

    private String lookup(final String key, final Locale locale) {
        final Collection<AbstractI18nBundle> list = DictionaryRegistry.getDictionaries();
        for (final AbstractI18nBundle dic : list) {
            final String resource = dic.getMessage(key, locale, dic.getClass().getClassLoader());
            if (StringUtils.hasText(resource)) {
                return resource;
            }
        }
        return "";
    }

    private String lookup(final String dicName, final String key, final Locale locale) {
        final AbstractI18nBundle dic = DictionaryRegistry.getDictionary(dicName);
        if (null != dic) {
            return dic.getMessage(key, locale, dic.getClass().getClassLoader());
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                     S T A T I C
    // ------------------------------------------------------------------------

    private static DictionaryController __instance;

    public static DictionaryController getInstance() {
        if (null == __instance) {
            __instance = new DictionaryController();
        }
        return __instance;
    }


}
