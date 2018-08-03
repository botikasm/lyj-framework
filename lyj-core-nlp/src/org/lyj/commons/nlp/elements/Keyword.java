package org.lyj.commons.nlp.elements;

import org.lyj.commons.nlp.elements.custom.CustomExpression;
import org.lyj.commons.nlp.elements.statements.StatementExpression;
import org.lyj.commons.util.RegExpUtils;

/**
 * Wrap a keyword expression like "how + old", "l*** + cat*"
 */
public class Keyword {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PREFIX_CUSTOM = IKeywordConstants.PREFIX_CUSTOM; // custom expression for external parser

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _raw;
    private final StatementExpression _expression;
    private final KeywordList.Parameters _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Keyword(final String raw,
                   final KeywordList.Parameters params) {
        _raw = raw;
        _params = params;
        _expression = !isCustom(raw) ? new StatementExpression(raw) : null;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Object match(final String[] phrase) {
        if (null != _expression) {
            return _expression.match(phrase);
        } else {
            // custom expression: send to external
            if (null != _params && null != _params.callback()) {
                return _params.callback().handle(CustomExpression.parse(_raw), phrase, _params.node());
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    public static boolean isCustom(final String raw) {
        return raw.startsWith(PREFIX_CUSTOM);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    /**
     * Remove dots, comma, etc.. and lowercase
     *
     * @param raw raw text
     */
    public static String clearKeyword(final String raw) {
        return RegExpUtils.replaceNotChars(raw).toLowerCase();
    }


}
