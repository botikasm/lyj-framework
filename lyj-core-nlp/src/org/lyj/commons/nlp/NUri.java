package org.lyj.commons.nlp;

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Parse text passed to bot to identify tags and parameters
 * Sample:
 * "tag?para1=1234&param2=45678"
 * "$script?para1=1234&param2=45678"
 */
public class NUri {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String SEP = "?";
    private static final String PREFIX_CUSTOM_SCRIPT = "$";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _text;
    private final Map<String, Object> _params;

    private boolean _is_simple_text;
    private boolean _is_custom_script;
    private String _tag; // first segment of passed text
    private String _queryString;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * Creates new instance
     *
     * @param text "tag?para1=1234&param2=45678", "$script?para1=1234&param2=45678"
     */
    public NUri(final String text) {
        _text = text;
        _tag = "";
        _queryString = "";
        _params = new HashMap<>();

        this.parse(text);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.isCustomScript()) {
            sb.append(PREFIX_CUSTOM_SCRIPT);
        }
        sb.append(_tag);
        if (null != _params && !_params.isEmpty()) {
            sb.append("?");
            sb.append(StringUtils.toQueryString(_params));
        }

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String text() {
        return _text;
    }

    public boolean isSimpleText() {
        return _is_simple_text;
    }

    public boolean isCustomScript() {
        return _is_custom_script;
    }

    public String root() {
        return _tag;
    }

    public NUri root(final String value) {
        if (StringUtils.hasText(value) && value.contains(SEP)) {
            final String[] tokens = StringUtils.split(value, SEP);
            _tag = tokens[0];
            this.addParams(tokens[1]);
        } else {
            _tag = value;
        }
        return this;
    }

    public String queryString() {
        return _queryString;
    }

    public Map<String, Object> params() {
        return _params;
    }

    public Map<String, Object> addParam(final String key, final Object value) {
        _params.put(key, value);
        return _params;
    }

    public Map<String, Object> addParams(final String stringParams) {
        if (StringUtils.hasText(stringParams)) {
            final String[] tokens = StringUtils.split(stringParams, "&");
            final Map<String, String> map = CollectionUtils.splitArrayElementsIntoMap(tokens, "=");
            if (null != map && !map.isEmpty()) {
                _params.putAll(map);
            }
        }
        return _params;
    }

    public Object get(final String name) {
        return StringUtils.hasText(name) ? _params.get(name.toLowerCase()) : "";
    }

    public String getString(final String name) {
        return StringUtils.toString(this.get(name));
    }

    public boolean getBoolean(final String name) {
        return ConversionUtils.toBoolean(this.get(name));
    }

    public int getInteger(final String name) {
        return ConversionUtils.toInteger(this.get(name));
    }

    public double getDouble(final String name) {
        return ConversionUtils.toDouble(this.get(name));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void parse(final String text) {
        if (StringUtils.hasText(text)) {
            final String[] tokens = StringUtils.split(text, SEP);
            _tag = tokens[0];
            if (_tag.startsWith(PREFIX_CUSTOM_SCRIPT)) {
                _tag = StringUtils.replace(_tag, PREFIX_CUSTOM_SCRIPT, "");
                _is_custom_script = true;
            } else {
                _is_custom_script = false;
            }

            if (tokens.length == 2) {
                _is_simple_text = false;
                _queryString = tokens[1];
                CollectionUtils.toMap(_queryString).forEach((key, value) -> {
                    _params.put(key.toLowerCase(), value);
                });
            } else {
                // no tokens
                _is_simple_text = true;
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static NUri create(final String path) {
        return new NUri(path);
    }

}

