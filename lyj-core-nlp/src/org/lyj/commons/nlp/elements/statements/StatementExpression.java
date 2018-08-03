package org.lyj.commons.nlp.elements.statements;

import org.lyj.commons.nlp.elements.IKeywordConstants;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Statements can be grouped with parenthesis.
 * Each group of parenthesis create a StatementExpression.
 * <p>
 * StatementExpression contains expression, operators and statements.
 * ex: ( ( love = like ) + hello ) + mark
 * <p>
 * An Expression matches with a phrase (array of words) when all Statements in that expression matches with one
 * or more word in phrase.
 * <p>
 * SAMPLE:
 * - expression = (like|love) + cat
 * MATCH: "Does mike love that cat?"
 * DON'T MATCH: "Does mike love that dog?"
 */
public class StatementExpression
        implements IKeywordConstants {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String REG_EXP_BLOCKS_1 = "\\([^()]*\\)";
    private static final String REG_EXP_BLOCKS_2 = "\\(([^()]*|\\([^()]*\\))*\\)";
    private static final String REG_EXP_BLOCKS_3 = "\\(([^()]*|\\(([^()]*|\\([^()]*\\))*\\))*\\)";  // match 3 levels of parenthesis. aaa(aa(aa(a)a)aaa)

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _raw_expression;
    private final List<String> _operators;
    private final List<Statement[]> _statements; // chain of statements

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public StatementExpression(final String expression) {
        _raw_expression = StringUtils.hasText(expression) ? expression.toLowerCase() : "";
        _statements = new LinkedList<>();
        _operators = new LinkedList<>();

        this.parse(_raw_expression);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean match(final String phrase) {
        if (StringUtils.hasText(phrase)) {
            return this.match(phrase.split(" "));
        }
        return false;
    }


    /**
     * Match a Phrase with a semantic expression.
     * A Phrase match with expression when all statements in expression match with one or more words in phrase,
     *
     * @param phrase The phrase (array of words) to match
     * @return Tru if all statements in expression matches with some words in phrase.
     */
    public boolean match(final String[] phrase) {
        if (null != phrase && phrase.length > 0) {
            return this.match(Arrays.asList(phrase));
        }
        return false;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void parse(final String expression) {
        final OutMap out = new OutMap();  // solved statements
        out.text = expression;
        this.parse(expression, out);

        final String clean_exp = cleanExpression(out.text);
        final String[] tokens_exp = StringUtils.split(clean_exp, " ");

        // loop on expression tokens to create the expression list
        for (final String token : tokens_exp) {
            if (isOperator(token)) {
                _operators.add(token);
            } else if (token.startsWith("$") && out.placeholders.containsKey(token)) {
                _statements.add(out.placeholders.get(token));
            } else {
                _statements.add(new Statement[]{new Statement(token)});
            }
        }
    }

    private void parse(final String expression,
                       final OutMap out) {
        if (StringUtils.hasText(expression)) {
            final Matcher m = Pattern.compile(REG_EXP_BLOCKS_3).matcher(expression);
            while (m.find()) {
                final String group = m.group().trim();
                final String clean = trimParenthesis(group);
                if (hasParenthesis(clean)) {
                    this.parse(clean, out);
                } else {
                    // found simple statement : replace with a placeholder in expression
                    final String key = "$" + out.placeholders.size();
                    out.placeholders.put(key, parseStatements(clean));
                    out.text = out.text.replace(group, key);
                }
            }
        }
    }

    private boolean match(final List<String> phrase) {
        int count_matches = 0;
        for (int i = 0; i < _statements.size(); i++) {
            final Statement[] statement = _statements.get(i);
            final String operator = CollectionUtils.get(_operators, i);
            for (final String word : phrase) {
                final boolean match = (null != operator && operator.equals(OP_NOT)) != this.match(statement, word);
                if (match) {
                    count_matches++;
                    break;
                }
            }
        }
        return count_matches == _statements.size();
    }

    private boolean match(final Statement[] statement_or, final String word) {
        for (final Statement statement : statement_or) {
            if (statement.match(word)) {
                return true;
            }
        }
        return false;
    }

    private static String cleanExpression(final String text) {
        final StringBuilder response = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            //Process char
            if (isCharOperator(c)) {
                //add missing spaces
                if (!hasSpaceBefore(text, i)) {
                    response.append(" ");
                }
                response.append(c);
                if (!hasSpaceAfter(text, i)) {
                    response.append(" ");
                }
            } else if (!isCharParenthesis(c)) {
                response.append(c);
            }
        }
        return response.toString();
    }

    private static boolean hasSpaceAfter(final String text, final int index) {
        if (index < text.length()) {
            return text.charAt(index + 1) == ' ';
        }
        return true;
    }

    private static boolean hasSpaceBefore(final String text, final int index) {
        if (index > 0) {
            return text.charAt(index - 1) == ' ';
        }
        return true;
    }

    private static boolean isCharOperator(final char c) {
        return c == OP_AND.charAt(0) || c == OP_NOT.charAt(0);
    }

    private static boolean isCharParenthesis(final char c) {
        return c == BLOCK_START.charAt(0) || c == BLOCK_END.charAt(0);
    }

    private static Statement[] parseStatements(final String text) {
        final List<Statement> response = new ArrayList<>();
        final String[] tokens = StringUtils.split(text, OP_OR);
        for (final String token : tokens) {
            response.add(new Statement(token));
        }
        return response.toArray(new Statement[response.size()]);
    }

    private static boolean hasParenthesis(final String text) {
        final Matcher m = Pattern.compile(REG_EXP_BLOCKS_3).matcher(text);
        return m.find();
    }

    private static String trimParenthesis(final String text) {
        if (text.startsWith(BLOCK_START) && text.endsWith(BLOCK_END)) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }


    private static boolean isStatement(final Object item) {
        return item instanceof Statement;
    }

    private static boolean isExpression(final Object item) {
        return item instanceof StatementExpression;
    }

    private static boolean isOperator(final Object item) {
        return item instanceof String && CollectionUtils.contains(OPERATORS, item);
    }


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------


    private static class OutMap {

        public String text;
        public Map<String, Statement[]> placeholders = new HashMap<>();

    }


}
