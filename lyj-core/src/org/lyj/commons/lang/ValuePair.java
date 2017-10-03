package org.lyj.commons.lang;

/**
 * Utility class to use in Lambda expressions or anonymous methods.
 */
public class ValuePair<K, V> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private K _key;
    private V _value;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ValuePair(final K key, final V value) {
        _key = key;
        _value = value;
    }

    public ValuePair() {
        this(null, null);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(_key).append("=");
        sb.append(_value);
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public K key() {
        return _key;
    }

    public void content(final K value) {
        _key = value;
    }

    public V value() {
        return _value;
    }

    public void value(final V value) {
        _value = value;
    }

}
