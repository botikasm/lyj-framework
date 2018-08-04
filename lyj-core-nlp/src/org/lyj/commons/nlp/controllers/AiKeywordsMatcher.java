package org.lyj.commons.nlp.controllers;

import org.lyj.commons.nlp.elements.IKeywordConstants;
import org.lyj.commons.nlp.elements.KeywordsSolver;
import org.json.JSONArray;

/**
 * Simple matcher for keywords
 */
public class AiKeywordsMatcher {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private AiKeywordsMatcher() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String match(final String text,
                        final JSONArray keywords) {
        final Object match = this.match(text, keywords, null);
        if (match instanceof String) {
            return (String) match;
        }
        return "";
    }

    public Object match(final String text,
                        final JSONArray keywords,
                        final IKeywordConstants.Callback callback) {
        if (null != keywords && keywords.length() > 0) {
            // contains keywords to match
            return KeywordsSolver.instance().matchKeywords(text, keywords, callback);
        }
        return null;
    }

    public boolean match(final String text,
                         final String[] keywords) {
        return KeywordsSolver.instance().matchKeywords(text, keywords);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static AiKeywordsMatcher __instance;

    public static synchronized AiKeywordsMatcher instance() {
        if (null == __instance) {
            __instance = new AiKeywordsMatcher();
        }
        return __instance;
    }

}
