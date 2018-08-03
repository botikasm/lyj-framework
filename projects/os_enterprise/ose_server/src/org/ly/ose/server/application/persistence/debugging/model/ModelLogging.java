package org.ly.ose.server.application.persistence.debugging.model;

import org.ly.ose.server.application.persistence.PersistentModel;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.SystemUtils;

import java.util.Date;

public class ModelLogging
        extends PersistentModel {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String COLLECTION = "logging";

    public static final String FLD_SESSION_ID = "session_id";
    public static final String FLD_CLIENT_ID = "client_id";

    public static final String FLD_TIMESTAMP = "timestamp";
    private static final String FLD_TIMESTAMP_FMT = "timestamp_fmt";

    private static final String FLD_MEMORY_TOTAL = "memory_total";
    private static final String FLD_MEMORY_USED = "memory_used";
    private static final String FLD_MEMORY_FREE = "memory_free";
    private static final String FLD_MEMORY_MAX = "memory_max";

    public static final String FLD_PROGRAM_NAME = "program_name";
    public static final String FLD_LEVEL = "level";
    private static final String FLD_MESSAGE = "message";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModelLogging() {
        super();
        this.init();
    }

    public ModelLogging(final Object item) {
        super(item);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String sessionId() {
        return super.getString(FLD_SESSION_ID);
    }

    public ModelLogging sessionId(final String value) {
        super.put(FLD_SESSION_ID, value);
        return this;
    }

    public String clientId() {
        return super.getString(FLD_CLIENT_ID);
    }

    public ModelLogging clientId(final String value) {
        super.put(FLD_CLIENT_ID, value);
        return this;
    }

    public String programName() {
        return super.getString(FLD_PROGRAM_NAME);
    }

    public ModelLogging programName(final String value) {
        super.put(FLD_PROGRAM_NAME, value);
        return this;
    }

    public String message() {
        return super.getString(FLD_MESSAGE);
    }

    public ModelLogging message(final String value) {
        super.put(FLD_MESSAGE, value);
        return this;
    }

    public String level() {
        return super.getString(FLD_LEVEL);
    }

    public ModelLogging level(final String value) {
        super.put(FLD_LEVEL, value);
        return this;
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private ModelLogging init() {
        // timestamp
        final Date now = DateUtils.now();
        super.put(FLD_TIMESTAMP, now.getTime());
        super.put(FLD_TIMESTAMP_FMT, FormatUtils.formatDate(now, FormatUtils.DEFAULT_DATETIMEFORMAT));

        // memory
        final double mem_total = ConversionUtils.bytesToMbyte(SystemUtils.getTotalMemory());
        final double mem_free = ConversionUtils.bytesToMbyte(SystemUtils.getFreeMemory());
        final double mem_max = ConversionUtils.bytesToMbyte(SystemUtils.getMaxMemory());
        final double mem_used = mem_max - mem_free;

        super.put(FLD_MEMORY_TOTAL, mem_total);
        super.put(FLD_MEMORY_FREE, mem_free);
        super.put(FLD_MEMORY_MAX, mem_max);
        super.put(FLD_MEMORY_USED, mem_used);

        return this;
    }



}
