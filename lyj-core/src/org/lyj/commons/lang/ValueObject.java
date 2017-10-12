package org.lyj.commons.lang;

/**
 * Utility class to use in Lambda expressions or anonymous methods.
 */
public class ValueObject<T> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private T _default_value;
    private T _value;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ValueObject(final T value) {
        _value = value;
    }

    public ValueObject() {
        this(null);
    }

    @Override
    public String toString() {
        return null != _value ? _value.toString() : "";
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public T defaultValue() {
        return _default_value;
    }

    public void defaultValue(final T value) {
        _default_value = value;
    }

    public T content() {
        return _value != null ? _value : _default_value;
    }

    public void content(final T value) {
        _value = value;
    }


}
