package org.ly.licensemanager.app.controllers.license.registry;

import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.json.JsonItem;

public class LicenseItem
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_TIMESTAMP = "timestamp";
    private static final String FLD_DURATION_DAYS = "duration_days";

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

    public int durationDays() {
        return super.getInt(FLD_DURATION_DAYS);
    }

    public LicenseItem durationDays(final int value) {
        super.put(FLD_DURATION_DAYS, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean expired() {
        try {
            final long timestamp = this.timestamp();
            final long now = System.currentTimeMillis();
            final long duration_ms = DateUtils.ONE_DAY_MS * this.durationDays();

            return 
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
    }


}
