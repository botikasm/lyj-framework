package org.lyj.commons.io.db.filedb;

import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.json.JsonItem;


public class FileDBEntity
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String KEY = IFileDBConstants.KEY;
    private static final String TIMESTAMP = IFileDBConstants.TIMESTAMP;
    private static final String INDEX = IFileDBConstants.INDEX;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileDBEntity() {
        super();
        this.init();
    }

    public FileDBEntity(final long index) {
        this();
        this.index(index);
    }

    public FileDBEntity(final Object item) {
        super(item);
        this.init();
    }

    public FileDBEntity(final long index, final Object item) {
        this(item);
        this.index(index);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String key() {
        return super.getString(KEY);
    }

    public FileDBEntity key(final String value) {
        super.put(KEY, value);
        return this;
    }

    public long timestamp() {
        return super.getLong(TIMESTAMP);
    }

    public long index() {
        return super.getLong(INDEX);
    }

    FileDBEntity index(final long value) {
        super.put(INDEX, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!super.has(TIMESTAMP)) {
            super.put(TIMESTAMP, DateUtils.timestamp());
        }
    }

}
