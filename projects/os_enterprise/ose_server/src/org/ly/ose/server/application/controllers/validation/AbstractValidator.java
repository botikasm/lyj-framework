package org.ly.ose.server.application.controllers.validation;

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

/**
 * Abstract validator/transformer for data validation or manipulation
 */
public abstract class AbstractValidator {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String EXPRESSION_SEPARATOR = "|";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _raw_exp;

    private String _name;
    private String[] _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * @param expression ex: format|numeric|#.00
     */
    public AbstractValidator(final String expression) {
        _raw_exp = expression;

        this.init();
    }

    public abstract Object validate(final Object value) throws Exception;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String rawExpression() {
        return _raw_exp;
    }

    public String name() {
        return _name;
    }

    public String[] params() {
        return null != _params ? _params : new String[0];
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        final String[] tokens = StringUtils.split(_raw_exp, EXPRESSION_SEPARATOR);
        if (tokens.length > 0) {
            _name = tokens[0];
            if (tokens.length > 1) {
                _params = CollectionUtils.subArray(tokens, 1, tokens.length - 1);
            }
        }

    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static String name(final String expression) {
        final String[] tokens = StringUtils.split(expression, EXPRESSION_SEPARATOR);
        return tokens[0];
    }

}
