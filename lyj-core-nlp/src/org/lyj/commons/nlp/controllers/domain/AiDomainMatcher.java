package org.lyj.commons.nlp.controllers.domain;

import org.lyj.commons.nlp.elements.IKeywordConstants;
import org.lyj.commons.nlp.elements.KeywordsSolver;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;

/**
 * Match multiple Domains (array of keywords) at once and returns a sorted list
 * (sorted by descending rating) of matching domains.
 */
public class AiDomainMatcher {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private AiDomainMatcher() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public DomainMatchList match(final String text, final Collection<JSONArray> domains) {
        final DomainMatchList responses = new DomainMatchList();

        if (null != domains && !domains.isEmpty()) {
            for (final JSONArray domain : domains) {
                final DomainMatch match = this.match(text, domain);
                if (null != match && match.rating() > 0.0) {
                    responses.add(match);
                    if (match.hasMatchValue() && match.hasMatchParams()) {
                        break;
                    }
                }
            }
        }

        responses.sort();
        return responses;
    }

    public DomainMatch match(final String text, final JSONArray domain) {
        return this.matchDomain(text, domain, true, null);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    /**
     * Return a DomainMatch object containing response and a match rating
     *
     * @param text      text to match
     * @param domain    semantic domain (VERB and PARAMS)
     * @param optimized If TRUE exit if VERB does not match with keywords
     * @return
     */
    private DomainMatch matchDomain(final String text,
                                    final JSONArray domain,
                                    final boolean optimized,
                                    final IKeywordConstants.Callback callback) {
        final int len = domain.length();
        if (len > 0) {
            final DomainMatch match = new DomainMatch();
            for (int i = 0; i < len; i++) {
                final Object item = domain.get(i);
                if (item instanceof JSONObject) {
                    final JSONObject domain_keywords = (JSONObject) item;
                    if (KeywordsSolver.instance().hasKeywords(domain_keywords)
                            && (KeywordsSolver.instance().hasValue(domain_keywords)
                            || KeywordsSolver.instance().hasParams(domain_keywords))) {
                        final boolean is_value = KeywordsSolver.instance().hasValue(domain_keywords);
                        final String[] keywords = KeywordsSolver.instance().getKeywords(domain_keywords);
                        final Object match_response = is_value
                                ? KeywordsSolver.instance().matchKeywords(text, keywords, KeywordsSolver.getValue(domain_keywords), domain_keywords, callback)
                                : KeywordsSolver.instance().matchKeywords(text, keywords, KeywordsSolver.getParams(domain_keywords), domain_keywords, callback);
                        if (match_response instanceof String) {
                            final String value = (String) match_response;
                            if (is_value) {
                                if (StringUtils.hasText(value)) {
                                    match.addVerb(value);
                                } else {
                                    // add default value (fallback), but not matched
                                    match.addVerbDefault(KeywordsSolver.instance().getValue(domain_keywords));

                                    if (optimized) {
                                        // exit because missing the match and verb does not exists
                                        break;
                                    }
                                }
                            } else {
                                match.addParams(value);
                            }
                        } else {
                            // no match value
                        }
                    }
                }
            }
            //System.out.println(match.rating());
            return match;
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static AiDomainMatcher __instance;

    public static synchronized AiDomainMatcher instance() {
        if (null == __instance) {
            __instance = new AiDomainMatcher();
        }
        return __instance;
    }

}
