package org.lyj.commons.nlp.elements;

import org.lyj.commons.nlp.elements.custom.CustomExpression;
import org.json.JSONObject;

/**
 *
 */
public interface IKeywordConstants {

    String WILDCHAR = "*"; // a character
    String OP_AND = "+";
    String OP_OR = "|";
    String OP_NOT = "-";
    String BLOCK_START = "(";
    String BLOCK_END = ")";

    String[] OPERATORS = new String[]{OP_AND, OP_OR, OP_NOT};

    /**
     * Callback handler for custom expressions
     */
    @FunctionalInterface
    public static interface Callback {
        Object handle(final CustomExpression expressions, final String[] phrase, final JSONObject node);
    }

    /**
     * Custom expression prefix. i.e. @tb_products.uid
     */
    String PREFIX_CUSTOM = "@";

}
