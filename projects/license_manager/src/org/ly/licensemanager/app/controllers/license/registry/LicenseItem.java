package org.ly.licensemanager.app.controllers.license.registry;

import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.DateWrapper;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.MathUtils;
import org.lyj.commons.util.json.JsonItem;

import java.util.Date;

public class LicenseItem
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_UID = "uid";
    private static final String FLD_TIMESTAMP = "timestamp";
    private static final String FLD_DURATION_DAYS = "duration_days";
    private static final String FLD_ENABLED = "enabled";
    private static final String FLD_EMAIL = "email";
    private static final String FLD_NAME = "name";
    private static final String FLD_LANG = "lang";
    private static final String FLD_FMT_EXPIRATION = "fmt_expiration";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LicenseItem() {
        super();
        this.init();
    }

    public LicenseItem(final Object item) {
        super(item);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public long timestamp() {
        return super.getLong(FLD_TIMESTAMP);
    }

    public String uid() {
        return super.getString(FLD_UID);
    }

    public LicenseItem uid(final String value) {
        super.put(FLD_UID, value);
        return this;
    }

    public String lang() {
        return super.getString(FLD_LANG);
    }

    public LicenseItem lang(final String value) {
        super.put(FLD_LANG, value);
        return this;
    }

    public String email() {
        return super.getString(FLD_EMAIL);
    }

    public LicenseItem email(final String value) {
        super.put(FLD_EMAIL, value);
        return this;
    }

    public String name() {
        return super.getString(FLD_NAME);
    }

    public LicenseItem name(final String value) {
        super.put(FLD_NAME, value);
        return this;
    }

    public int durationDays() {
        return super.getInt(FLD_DURATION_DAYS);
    }

    public LicenseItem durationDays(final int value) {
        super.put(FLD_DURATION_DAYS, value);
        return this;
    }

    public boolean enabled() {
        return super.getBoolean(FLD_ENABLED);
    }

    public LicenseItem enabled(final boolean value) {
        super.put(FLD_ENABLED, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void postpone(final int days) {
        final int duration = this.durationDays();
        this.durationDays(duration + days);

        this.recalculate();
    }

    public void expirationDate(final String date) {
        try {
            final DateWrapper dt = new DateWrapper(date, DateWrapper.DATEFORMAT_DEFAULT);
            expirationDate(dt.getTime());
        } catch (Throwable ignored) {
        }
    }

    public void expirationDate(final Date date) {
        this.expirationDate(date.getTime());
    }

    public void expirationDate(final long date) {
        final long timestamp = this.timestamp(); // start of license
        final long duration_ms = date - timestamp;
        final int duration_day = MathUtils.floor(duration_ms / DateUtils.ONE_DAY_MS);

        this.durationDays(duration_day);
    }

    public boolean expired() {
        try {
            final long now = System.currentTimeMillis();
            final long expiration_time = this.expirationTime();

            return expiration_time < now;
        } catch (Throwable ignored) {
            // ignored
        }
        return true;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!super.has(FLD_TIMESTAMP)) {
            super.put(FLD_TIMESTAMP, System.currentTimeMillis());
        }
        if (!super.has(FLD_ENABLED)) {
            super.put(FLD_ENABLED, false);
        }

        this.recalculate();
    }

    private long durationMs() {
        return DateUtils.ONE_DAY_MS * this.durationDays();
    }

    private long expirationTime() {
        final long timestamp = this.timestamp();
        final long duration_ms = this.durationMs();

        return timestamp + duration_ms;
    }

    private void recalculate(){
        final String fmt_date = FormatUtils.formatDate(new Date(this.expirationTime()));
        super.put(FLD_FMT_EXPIRATION, fmt_date);
    }
}
