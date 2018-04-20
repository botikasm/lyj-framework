package org.ly.licensemanager.app.controllers.license.registry;

import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.DateWrapper;
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
            final long timestamp = this.timestamp();
            final long now = System.currentTimeMillis();
            final long duration_ms = DateUtils.ONE_DAY_MS * this.durationDays();

            return timestamp + duration_ms < now;
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
            super.put(FLD_ENABLED, true);
        }
    }


}
