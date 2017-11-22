package org.lyj.commons.util.json;

import org.json.JSONObject;
import org.lyj.commons.i18n.ILocalizable;
import org.lyj.commons.util.StringUtils;

import java.util.Map;
import java.util.Properties;

public class JsonItemI18n
        extends JsonItem
        implements ILocalizable {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String LANG_BASE = "base";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JsonItemI18n() {
        super();
    }

    public JsonItemI18n(final String item) {
        super(item);
    }

    public JsonItemI18n(final JSONObject item) {
        super(item);
    }

    public JsonItemI18n(final JsonItem item) {
        super(item);
    }

    public JsonItemI18n(final Map<String, ?> item) {
        super(item);
    }

    public JsonItemI18n(final Properties item) {
        super(item);
    }

    public JsonItemI18n(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Object i18nGet(final String field) {
        return this.i18nGet(LANG_BASE, field);
    }

    public <T> T i18nGet(final String field, final T def_val) {
        return this.i18nGet(LANG_BASE, field, def_val);
    }

    public String i18nGetString(final String field) {
        return i18nGetString(LANG_BASE, field);
    }

    public void i18nSet(final String field, final Object value) {
        this.i18nSet(LANG_BASE, field, value);
    }

    public Object i18nGet(final String lang, final String field) {
        if (!super.has(field)) {
            super.put(field, new JSONObject());
        }
        return super.getJSONObject(field).opt(this.lang(lang));
    }

    public <T> T i18nGet(final String lang, final String field, final T def_val) {
        if (!super.has(field)) {
            super.put(field, new JSONObject());
        }
        final Object response = super.getJSONObject(field).opt(this.lang(lang));
        return null != response ? (T) response : def_val;
    }


    public String i18nGetString(final String lang, final String field) {
        if (!super.has(field)) {
            super.put(field, new JSONObject());
        }
        return super.getJSONObject(field).optString(this.lang(lang));
    }

    public void i18nSet(final String lang, final String field, final Object value) {
        if (!super.has(field)) {
            super.put(field, new JSONObject());
        }
        super.getJSONObject(field).put(this.lang(lang), value);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String lang(final String value) {
        return StringUtils.hasText(value) ? value : LANG_BASE;
    }

}
