package org.lyj.commons.nlp.elements.custom;

import org.json.JSONObject;
import org.lyj.commons.nlp.elements.IKeywordConstants;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.MapConverter;

import java.util.Map;

/**
 * Parse custom expressions.
 * Custom prefix is @
 * ----------------------
 * [operator].[collection].[field]
 * "<.tb_products.uid"
 */
public class CustomExpression {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PREFIX = IKeywordConstants.PREFIX_CUSTOM; // @
    private static final String SEP = ".";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _raw;

    private String _operator;
    private String _collection;
    private String _field;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CustomExpression(final String text) {
        _raw = text.replace(PREFIX, "");
        this.parseText(_raw);
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String raw() {
        return PREFIX.concat(_raw);
    }

    public boolean isValid() {
        return StringUtils.hasText(this.operator())
                && StringUtils.hasText(this.collection())
                && StringUtils.hasText(this.field());
    }

    public String operator() {
        return _operator;
    }

    public String collection() {
        return _collection;
    }

    public String field() {
        return _field;
    }

    public JSONObject toJson() {
        final JSONObject response = new JSONObject();

        response.put("operator", _operator);
        response.put("collection", _collection);
        response.put("field", _field);

        return response;
    }

    public Map toMap() {
        return MapConverter.toMap(this.toJson());
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void parseText(final String text) {
        if (StringUtils.hasText(text)) {
            final String[] tokens = StringUtils.split(text, SEP);
            if (tokens.length == 2) {
                _operator = "==";
                _collection = tokens[0];
                _field = tokens[1];
            } else if (tokens.length == 3) {
                _operator = tokens[0];
                _collection = tokens[1];
                _field = tokens[2];
            }
        }
    }

    public static CustomExpression parse(final String text) {
        return new CustomExpression(text);
    }

}
