package org.lyj.ext.mongo.schema;

import org.lyj.commons.Delegates;

/**
 * Field descriptor
 */
public class LyjMongoField {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _name;
    private boolean _index;
    private boolean _unique;
    private Object _type; // String, Integer, [{field1:String, field2:Boolean]
    private Object _default; // default value (value or function)

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LyjMongoField(final String name) {
        this(name, String.class, false, false);
    }

    public LyjMongoField(final String name, final Object type, final boolean index, final boolean unique) {
        _name = name;
        _type = type;
        _index = index;
        _unique = unique;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String getName() {
        return _name;
    }

    public boolean isIndex() {
        return _index;
    }

    public void setIndex(final boolean value) {
        _index = value;
    }

    public boolean isUnique() {
        return _unique;
    }

    public void setUnique(final boolean value) {
        _unique = value;
    }

    public String getType() {
        if (_type instanceof Class) {
            return ((Class) _type).getSimpleName();
        } else {
            return null != _type ? _type.toString() : "";
        }
    }

    public Object getDefault() {
        return _default;
    }

    public void setDefault(final Object value) {
        _default = value;
    }

    public void setDefault(final Delegates.Function value) {
        _default = value;
    }

    public Object getDefaultValue(){
        if(_default instanceof Delegates.Function){
            return ((Delegates.Function)_default).handle();
        } else {
            return _default;
        }
    }
}
