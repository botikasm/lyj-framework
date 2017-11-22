package org.lyj.commons.i18n;

/**
 * Generic interface for localizable objects
 */
public interface ILocalizable {

    Object i18nGet(final String field);
    <T> T i18nGet(final String field, final T def_val);
    String i18nGetString(final String field);
    void i18nSet(final String field, final Object value);

    Object i18nGet(final String lang, final String field);
    <T> T i18nGet(final String lang, final String field, final T def_val);
    String i18nGetString(final String lang, final String field);
    void i18nSet(final String lang, final String field, final Object value);

}
