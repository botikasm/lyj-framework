package org.lyj.commons.io.memorycache;

import org.lyj.commons.util.json.JsonItem;

/**
 * Wrap a cached item
 */
public class MemoryCacheItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final long DEFAULT_LIFE = 60 * 1000; // 1 minute

    private static final String TIMESTAMP = "timestamp";
    private static final String DURATION = "duration";
    private static final String ITEM = "item";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonItem _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MemoryCacheItem() {
        _data = new JsonItem();
        this.duration(DEFAULT_LIFE);
    }

    @Override
    public String toString() {
        return _data.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public long timestamp() {
        return _data.getLong(TIMESTAMP);
    }

    public long duration() {
        return _data.getLong(DURATION);
    }

    public MemoryCacheItem duration(final long value) {
        _data.put(DURATION, value);
        return this;
    }

    public boolean expired() {
        final long now = System.currentTimeMillis();
        return now - this.timestamp() > this.duration();
    }

    public MemoryCacheItem item(final Object item) {
        _data.put(ITEM, item);
        _data.put(TIMESTAMP, System.currentTimeMillis());

        return this;
    }

    public Object item() {
        return _data.get(ITEM);
    }


}
