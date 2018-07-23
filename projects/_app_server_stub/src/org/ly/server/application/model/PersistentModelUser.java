package org.ly.server.application.model;

import org.json.JSONObject;
import org.ly.server.IConstant;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.DateWrapper;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.StringUtils;

/**
 * Base user
 */
public class PersistentModelUser
        extends PersistentModel {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String FLD_COMPANY_UID = "company_uid"; // unique uid for this company

    public static final String FLD_USERNAME = "username";
    public static final String FLD_PASSWORD = "password";

    public static final String FLD_LAST_ACCESS_DATE = "last_access_date";
    public static final String FLD_LAST_ACCESS_DATE_FMT = "last_access_date_fmt";

    public static final String FLD_TIMESTAMP_LAST_PASSWORD_CHANGE = "timestamp_password_change"; // last email sent
    public static final String FLD_TIMESTAMP_LAST_EMAIL_VALIDATE = "timestamp_email_validate";

    public static final String FLD_FIRST_NAME = "first_name";
    public static final String FLD_LAST_NAME = "last_name";
    public static final String FLD_PROFILE_PIC = "profile_pic";

    public static final String FLD_LOCALE = "locale";
    public static final String FLD_LANG = "lang";

    public static final String FLD_COUNTRY = "country";
    public static final String FLD_TIMEZONE = "timezone";
    public static final String FLD_GENDER = "gender";
    public static final String FLD_EMAIL = "email";
    public static final String FLD_MOBILE = "mobile";
    public static final String FLD_PHONE = "phone";

    public static final String FLD_IS_ENABLED = "is_enabled";


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public PersistentModelUser() {
        super();
        this.initDefaults();
    }

    public PersistentModelUser(final Object item) {
        super(item);
        this.initDefaults();
    }


    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


    public String companyUid() {
        return super.getString(FLD_COMPANY_UID);
    }

    public PersistentModelUser companyUid(final String value) {
        super.put(FLD_COMPANY_UID, value);
        return this;
    }

    public boolean isEnabled() {
        return super.getBoolean(FLD_IS_ENABLED);
    }

    public PersistentModelUser isEnabled(final boolean value) {
        super.put(FLD_IS_ENABLED, value);
        return this;
    }

    public long timestampLastEmalValidate() {
        return super.getLong(FLD_TIMESTAMP_LAST_EMAIL_VALIDATE);
    }

    public PersistentModelUser timestampLastEmalValidate(final long value) {
        if (value > 0) {
            super.put(FLD_TIMESTAMP_LAST_EMAIL_VALIDATE, value);
        }
        return this;
    }

    public long timestampLastPasswordChange() {
        return super.getLong(FLD_TIMESTAMP_LAST_PASSWORD_CHANGE);
    }

    public PersistentModelUser timestampLastPasswordChange(final long value) {
        if (value > 0) {
            super.put(FLD_TIMESTAMP_LAST_PASSWORD_CHANGE, value);
        }
        return this;
    }

    public long lastAccessDate() {
        return super.getLong(FLD_LAST_ACCESS_DATE);
    }

    public PersistentModelUser lastAccessDate(final long value) {
        if (value > 0) {
            super.put(FLD_LAST_ACCESS_DATE, value);
            final DateWrapper date = new DateWrapper(value);
            this.lastAccessDateFmt(date.toString(DateWrapper.PATTERNS[2]));  // Tue, 5 Jan 2013 21:47:38
        }

        return this;
    }

    public String lastAccessDateFmt() {
        return super.getString(FLD_LAST_ACCESS_DATE_FMT);
    }

    public PersistentModelUser lastAccessDateFmt(final String value) {
        super.put(FLD_LAST_ACCESS_DATE_FMT, value);
        return this;
    }

    public String username() {
        return super.getString(FLD_USERNAME);
    }

    public PersistentModelUser username(final String value) {
        super.put(FLD_USERNAME, value);
        return this;
    }

    public String password() {
        return super.getString(FLD_PASSWORD);
    }

    public PersistentModelUser password(final String value) {
        super.put(FLD_PASSWORD, value);
        return this;
    }

    public String firstName() {
        return super.getString(FLD_FIRST_NAME);
    }

    public PersistentModelUser firstName(final String value) {
        super.put(FLD_FIRST_NAME, value);
        return this;
    }

    public String lastName() {
        return super.getString(FLD_LAST_NAME);
    }

    public PersistentModelUser lastName(final String value) {
        super.put(FLD_LAST_NAME, value);
        return this;
    }

    public String profilePic() {
        return super.getString(FLD_PROFILE_PIC);
    }

    public PersistentModelUser profilePic(final String value) {
        super.put(FLD_PROFILE_PIC, value);
        return this;
    }

    public String locale() {
        String result = super.getString(FLD_LOCALE);
        if (!StringUtils.hasText(result)) {
            final String lang = this.lang();
            final String country = this.country();
            if (StringUtils.hasText(lang) || StringUtils.hasText(country)) {
                result = LocaleUtils.getLocale(lang, country).toString();
                // update locale and reset lang and country
                this.locale(result);
            }
        }
        return result;
    }

    public PersistentModelUser locale(final String value) {
        super.putNotEmpty(FLD_LOCALE, value);
        super.putNotEmpty(FLD_LANG, getLanguage(value));
        super.putNotEmpty(FLD_COUNTRY, getCountry(value));
        return this;
    }

    public String lang() {
        return super.getString(FLD_LANG);
    }

    public PersistentModelUser lang(final String value) {
        super.putNotEmpty(FLD_LANG, value);
        return this;
    }

    public String country() {
        return super.getString(FLD_COUNTRY);
    }

    public PersistentModelUser country(final String value) {
        super.putNotEmpty(FLD_COUNTRY, value);
        return this;
    }

    public String timezone() {
        return super.getString(FLD_TIMEZONE);
    }

    public PersistentModelUser timezone(final String value) {
        super.put(FLD_TIMEZONE, value);
        return this;
    }

    public String gender() {
        return IConstant.GENDER_MAP.get(super.getString(FLD_GENDER, IConstant.GENDER_MALE));
    }

    public PersistentModelUser gender(final String value) {
        super.put(FLD_GENDER, IConstant.GENDER_MAP.get(value));
        return this;
    }

    public String genderCode() {
        return IConstant.GENDER_MAP.get(this.gender());
    }

    public String email() {
        return super.getString(FLD_EMAIL);
    }

    public PersistentModelUser email(final String value) {
        super.put(FLD_EMAIL, value);
        return this;
    }

    public String mobile() {
        return super.getString(FLD_MOBILE);
    }

    public PersistentModelUser mobile(final String value) {
        super.put(FLD_MOBILE, value);
        return this;
    }

    public String phone() {
        return super.getString(FLD_PHONE);
    }

    public PersistentModelUser phone(final String value) {
        super.put(FLD_PHONE, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void setAccessNow() {
        this.lastAccessDate(DateUtils.now().getTime());
    }

    public String fullName() {
        final String first_name = this.firstName();
        final String last_name = this.lastName();
        final StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(first_name)) {
            sb.append(first_name);
        }
        if (StringUtils.hasText(last_name)) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(last_name);
        }
        return sb.toString();
    }

    public void merge(final JSONObject model) {
        if (null != model) {
            super.putAll(model, true);
        }
    }

    public void initDefaults() {

        this.setAccessNow();

        if (!this.has(FLD_EMAIL)) {
            this.put(FLD_EMAIL, "");
        }
        if (!this.has(FLD_GENDER)) {
            this.put(FLD_GENDER, "u"); // undefined
        }
        if (!this.has(FLD_COUNTRY)) {
            this.put(FLD_COUNTRY, "");
        }
        if (!this.has(FLD_FIRST_NAME)) {
            this.put(FLD_FIRST_NAME, "");
        }
        if (!this.has(FLD_LAST_NAME)) {
            this.put(FLD_LAST_NAME, "");
        }
        if (!this.has(FLD_LANG)) {
            this.put(FLD_LANG, "");
        }
        if (!this.has(FLD_MOBILE)) {
            this.put(FLD_MOBILE, "");
        }
        if (!this.has(FLD_PHONE)) {
            this.put(FLD_PHONE, "");
        }
        if (!super.has(FLD_IS_ENABLED)) {
            this.isEnabled(true);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String getLanguage(final String locale) {
        final String lang = LocaleUtils.getLanguage(locale);
        return StringUtils.hasText(lang) ? lang : IConstant.DEF_LANG;
    }

    private static String getCountry(final String locale) {
        final String country = LocaleUtils.getCountry(locale);
        return StringUtils.hasText(country) ? country : "";
    }

}
