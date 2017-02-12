package org.lyj.commons.lang;

/**
 * Utility class to use in Lambda expressions or anonymous methods.
 */
public class Counter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private long _count;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Counter(int initial) {
        _count = initial;
    }

    public Counter() {
        this(0);
    }

    @Override
    public String toString() {
        return _count + "";
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public long value() {
        return _count;
    }

    public int valueAsInt() {
        return (int) _count;
    }

    public void inc() {
        synchronized (this) {
            _count++;
        }
    }

    public void inc(final long value) {
        synchronized (this) {
            _count += value;
        }
    }

    public void reset() {
        _count = 0;
    }

}
