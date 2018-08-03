package org.lyj.commons.nlp.elements;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * List of keyword expressions
 */
public class KeywordList
        implements IKeywordConstants {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Parameters _params;
    private final List<Keyword> _keywords;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public KeywordList(final Parameters params) {
        _params = params;
        _keywords = new LinkedList<>();
        this.addAll(_params.items());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public KeywordList clear() {
        _keywords.clear();
        ;
        return this;
    }

    public KeywordList add(final String keyword) {
        _keywords.add(new Keyword(keyword, _params));
        return this;
    }

    public KeywordList addAll(final String[] keywords) {
        Arrays.asList(keywords).forEach(this::add);
        return this;
    }

    /**
     * Match a phrase with at least one keyword.
     *
     * @param phrase Phrase to match
     * @return True when al least a keyword (statement expression) match the phrase.
     */
    public Object match(final String phrase) {
        final String[] tokens = StringUtils.split(phrase, " ", true);
        for (final Keyword keyword : _keywords) {
            final Object match = keyword.match(tokens); // boolean or object from a custom parser
            if (null != match) {
                if (match instanceof Boolean) {
                    if ((Boolean) match) {
                        return true;
                    }
                } else if (match instanceof JSONArray) {
                    if (((JSONArray) match).length() > 0) {
                        return match;
                    }
                } else if (match instanceof JSONObject) {
                    if (((JSONObject) match).length() > 0) {
                        return match;
                    }
                } else {
                    return match;
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class Parameters {

        private Callback _callback;
        private String[] _items;
        private JSONObject _node;

        public Callback callback() {
            return _callback;
        }

        public Parameters callback(final Callback value) {
            _callback = value;
            return this;
        }


        public String[] items() {
            return _items;
        }

        public Parameters items(final String[] value) {
            _items = value;
            return this;
        }

        public JSONObject node() {
            return _node;
        }

        public Parameters node(final JSONObject value) {
            _node = value;
            return this;
        }

    }

}
