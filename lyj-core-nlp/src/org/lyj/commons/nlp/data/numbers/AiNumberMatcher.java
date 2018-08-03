package org.lyj.commons.nlp.data.numbers;

import org.lyj.commons.nlp.controllers.AiKeywordsMatcher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.lang.ValueObject;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.json.JsonWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Detect a number
 */
public class AiNumberMatcher {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_VALUE = "value";
    private static final String FLD_KEYWORDS = "keywords";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private AiNumberMatcher() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Number matchOne(final String lang,
                           final String text) {
        final ValueObject<Number> response = new ValueObject<>();
        final JSONObject[] array = this.getResource(lang);

        for (final JSONObject item : array) {
            final JsonItem jitem = new JsonItem(item);
            final Number value = (Number) jitem.get(FLD_VALUE);
            final JSONArray keywords = jitem.getJSONArray(FLD_KEYWORDS);

            if (AiKeywordsMatcher.instance().match(text, JsonWrapper.toArrayOfString(keywords))) {
                response.content(value);
                break;
            }
        }

        return response.content();
    }

    public Number[] matchAll(final String lang,
                             final String text) {
        final Set<Number> response = new HashSet<>();
        final JSONObject[] array = this.getResource(lang);

        for (final JSONObject item : array) {
            final JsonItem jitem = new JsonItem(item);
            final Number value = (Number) jitem.get(FLD_VALUE);
            final JSONArray keywords = jitem.getJSONArray(FLD_KEYWORDS);

            if (AiKeywordsMatcher.instance().match(text, JsonWrapper.toArrayOfString(keywords))) {
                response.add(value);
            }
        }

        return response.toArray(new Number[0]);
    }

    public int count(final String lang,
                     final String text) {
        int response = 0;
        final JSONObject[] array = this.getResource(lang);

        for (final JSONObject item : array) {
            final JsonItem jitem = new JsonItem(item);
            final Number value = (Number) jitem.get(FLD_VALUE);
            final JSONArray keywords = jitem.getJSONArray(FLD_KEYWORDS);

            if (AiKeywordsMatcher.instance().match(text, JsonWrapper.toArrayOfString(keywords))) {
                response++;
            }
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JSONObject[] getResource(final String raw_lang) {

        final String lang = LocaleUtils.getLanguage(raw_lang);

        String response = ClassLoaderUtils.getResourceAsString(null, this.getClass(), "/data/" + lang + ".json");
        if (!StringUtils.hasText(response)) {
            response = ClassLoaderUtils.getResourceAsString(null, this.getClass(), "/data/base.json");
        }
        final JSONArray array = StringUtils.isJSONArray(response) ? new JSONArray(response) : new JSONArray();
        return JsonWrapper.toArrayOfJSONObject(array);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static AiNumberMatcher __instance;

    public static synchronized AiNumberMatcher instance() {
        if (null == __instance) {
            __instance = new AiNumberMatcher();
        }
        return __instance;
    }

}
