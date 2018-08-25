package org.lyj.commons.io.cache.memorycache;

import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.converters.JsonConverter;

import java.util.Map;
import java.util.TreeMap;

/**
 * Wrap a cached item
 */
public class MemoryCacheItem<T> {

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

    private final Map<String, Object> _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MemoryCacheItem() {
        _data = new TreeMap<>();
        this.duration(DEFAULT_LIFE);
    }

    @Override
    public String toString() {
        return JsonConverter.toObject(_data).toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public long timestamp() {
        return ConversionUtils.toLong(_data.get(TIMESTAMP));
    }

    public long duration() {
        return ConversionUtils.toLong(_data.get(DURATION));
    }

    public MemoryCacheItem<T> duration(final long value) {
        _data.put(DURATION, value);
        return this;
    }

    public MemoryCacheItem<T> wakeUp() {
        _data.put(TIMESTAMP, System.currentTimeMillis());
        return this;
    }

    public boolean expired() {
        final long now = System.currentTimeMillis();
        return now - this.timestamp() > this.duration();
    }

    public MemoryCacheItem<T> item(final T item) {
        return this.item(item, true);
    }

    public MemoryCacheItem<T> item(final T item, final boolean wakeup) {
        _data.put(ITEM, item);
        if(wakeup){
            _data.put(TIMESTAMP, System.currentTimeMillis());
        }
        return this;
    }

    public T item() {
        return (T) _data.get(ITEM);
    }


}
