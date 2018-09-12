package org.ly.appsupervisor.app.loop.launcher.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.StringUtils;

public class RuleExpression {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String OP_EQUALS = "=";
    private static final String OP_NOT_EQUALS = "!=";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _raw_text;

    private String _operator;
    private String _value;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RuleExpression(final String raw_text) {
        _raw_text = raw_text;

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean validate(final String raw_value) {
        try {
            if(StringUtils.hasText(_operator)){
                final String check_value = normalize(_value);
                final String value = normalize(raw_value);
                if(_operator.equalsIgnoreCase(OP_EQUALS)){
                    return check_value.equalsIgnoreCase(value);
                } else if (_operator.equalsIgnoreCase(OP_NOT_EQUALS)){
                    return !check_value.equalsIgnoreCase(value);
                }
            }
        } catch (Throwable t) {
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (StringUtils.hasText(_raw_text)) {
            final String[] tokens = StringUtils.splitFirst(_raw_text, ".");
            if (tokens.length == 2) {
                _operator = tokens[0];
                _value = tokens[1];
            }
        }
    }

    private String normalize(final String value){
        if(StringUtils.isJSON(value)){
           if(StringUtils.isJSONObject(value)){
              return new JSONObject(value).toString();
           } else if (StringUtils.isJSONArray(value)){
               return new JSONArray(value).toString();
           }
        }
        return value;
    }

}
