package org.ly.ose.server.application.persistence.global.model;

import org.ly.ose.server.application.persistence.PersistentModel;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FormatUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ModelSession
        extends PersistentModel {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String COLLECTION = "sessions";

    public static final String FLD_SESSION_ID = "__session_id";
    public static final String FLD_UID = "__uid";

    private static final String FLD_TIMESTAMP = "__timestamp";
    private static final String FLD_TIMESTAMP_FMT = "__timestamp_fmt";
    private static final String FLD_ELAPSED_MS = "__elapsed_ms";


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModelSession() {
        super();
    }

    public ModelSession(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String sessionId() {
        return super.getString(FLD_SESSION_ID);
    }

    public ModelSession sessionId(final String value) {
        super.put(FLD_SESSION_ID, value);
        return this;
    }

    public String uid() {
        return super.getString(FLD_UID);
    }

    public ModelSession uid(final String value) {
        super.put(FLD_UID, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public ModelSession renew() {
        final Date now = DateUtils.now();
        final long prev = super.getLong(FLD_TIMESTAMP, now.getTime());
        super.put(FLD_TIMESTAMP, now.getTime());
        super.put(FLD_TIMESTAMP_FMT, FormatUtils.formatDate(now, FormatUtils.DEFAULT_DATETIMEFORMAT));
        super.put(FLD_ELAPSED_MS, now.getTime() - prev);

        return this;
    }

    public int elapsed() {
        return (int) super.getLong(FLD_ELAPSED_MS, 0);
    }

    public Set<String> keys() {
        final Set<String> response = new HashSet<>();
        super.keySet().forEach((key) -> {
            if (!key.startsWith("_")) {
                response.add(key);
            }
        });
        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
