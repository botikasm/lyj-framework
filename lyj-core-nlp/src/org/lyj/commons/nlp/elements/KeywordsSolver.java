package org.lyj.commons.nlp.elements;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;

import java.util.Map;

/**
 * Solve keywords matching.
 */
public class KeywordsSolver {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String[] FLD_KEYS = {"keys", "keywords"};
    private static final String FLD_VALUE = "value";
    private static final String FLD_PARAMS = "params";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private KeywordsSolver() {
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean hasValue(final JSONObject item) {
        return StringUtils.hasText(item.optString(FLD_VALUE));
    }

    public boolean hasParams(final JSONObject item) {
        return StringUtils.hasText(item.optString(FLD_PARAMS));
    }

    public boolean hasKeywords(final JSONObject item) {
        final JSONArray array = getKeys(item);
        return array.length() > 0;
    }

    public String[] getKeywords(final JSONObject item) {
        return JsonWrapper.toArrayOfString(getKeys(item));
    }

    public Object matchKeywords(final String text,
                                final JSONArray items,
                                final IKeywordConstants.Callback callback) {
        final int len = items.length();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                final Object item = items.get(i);
                if (item instanceof JSONObject) {
                    final Object response = this.matchKeywords(text, (JSONObject) item, callback);
                    if (null != response && !(response instanceof Boolean)) {
                        if (StringUtils.hasText(response.toString())) {
                            return response;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Object matchKeywords(final String text,
                                final JSONObject item,
                                final IKeywordConstants.Callback callback) {
        final String match_resp = getValue(item); // item.optString(FLD_VALUE);
        final JSONArray array = getKeys(item); // item.optJSONArray(FLD_KEYS);
        if (array.length() > 0 && StringUtils.hasText(match_resp)) {
            final String[] keywords = JsonWrapper.toArrayOfString(array);
            return this.matchKeywords(text, keywords, match_resp, item, callback);
        }
        return null;
    }

    public Object matchKeywords(final String text,
                                final String[] keywords,
                                final String matchResponse,
                                final JSONObject item,
                                final IKeywordConstants.Callback callback) {
        if (StringUtils.hasText(text) && null != keywords && keywords.length > 0) {
            final KeywordList kl = new KeywordList(
                    new KeywordList.Parameters()
                            .items(keywords)
                            .callback(callback)
                            .node(item)
            );
            final Object match = kl.match(text);
            if (match instanceof Boolean && (Boolean) match) {
                return matchResponse;
            } else if (null != match) {
                return match;
            }
        }
        return null;
    }

    public boolean matchKeywords(final String text,
                                 final String[] keywords) {
        final KeywordList kl = new KeywordList(
                new KeywordList.Parameters()
                        .items(keywords)
        );
        final Object match = kl.match(text);
        if (match instanceof Boolean) {
            return (Boolean) match;
        }
        return false;
    }

    public int matchIndex(final String[] phrase,
                          final String[] keywords) {
        final KeywordList kl = new KeywordList(
                new KeywordList.Parameters()
                        .items(keywords)
        );
        return kl.matchIndex(phrase);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static KeywordsSolver __instance;

    public static synchronized KeywordsSolver instance() {
        if (null == __instance) {
            __instance = new KeywordsSolver();
        }
        return __instance;
    }

    public static String getValue(final Map item) {
        return null != item && item.containsKey(FLD_VALUE) ? StringUtils.toString(item.get(FLD_VALUE)) : "";
    }

    public static String getValue(final JSONObject item) {
        return null != item ? item.optString(FLD_VALUE) : "";
    }

    public static String getParams(final JSONObject item) {
        return null != item ? item.optString(FLD_PARAMS) : "";
    }

    public static JSONArray getKeys(final Map item) {
        return getKeys(JsonWrapper.toJSONObject(item));
    }

    public static JSONArray getKeys(final JSONObject item) {
        for (final String field_name : FLD_KEYS) {
            if (item.has(field_name)) {
                return item.optJSONArray(field_name);
            }
        }
        return new JSONArray();
    }

}
