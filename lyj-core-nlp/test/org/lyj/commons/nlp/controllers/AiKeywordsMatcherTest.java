package org.lyj.commons.nlp.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.lyj.commons.nlp.elements.Keyword;
import org.lyj.commons.nlp.elements.custom.CustomExpression;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.StringUtils;

import static org.junit.Assert.*;

public class AiKeywordsMatcherTest {

    @Test
    public void match() throws Exception {
        final String response = AiKeywordsMatcher.instance().match("via", this.keywords());
        System.out.println(response);
    }


    @Test
    public void matchAdvanced() throws Exception {
        final JSONArray array = this.advanced();

        System.out.println("QUERY: 1");
        Object response = AiKeywordsMatcher.instance().match("prodotti", array, this::handleCustomExpression);
        System.out.println(response);

        System.out.println("QUERY: 2");
        response = AiKeywordsMatcher.instance().match("pummarò e prospetti con contorno di patatine olè!!", array, this::handleCustomExpression);
        System.out.println(response);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JSONArray keywords() {
        final String json = ClassLoaderUtils.getResourceAsString(null, this.getClass(),
                "keywords.json");
        return new JSONArray(json);
    }

    private JSONArray advanced() {
        final String json = ClassLoaderUtils.getResourceAsString(null, this.getClass(),
                "keywords-advanced.json");
        return new JSONArray(json);
    }

    private Object handleCustomExpression(final CustomExpression expression,
                                          final String[] keywords,
                                          final JSONObject node) {
        System.out.println(expression);
        System.out.println(StringUtils.toString(keywords));

        final JSONArray items = new JSONArray();

        // simulate query on database
        for (final String key : keywords) {
            final String clean_key = Keyword.clearKeyword(key);
            if (clean_key.equalsIgnoreCase("pummarò") || clean_key.equalsIgnoreCase("olè")) {
                // item found
                final JSONObject item = new JSONObject();
                item.put("_key", clean_key);
                item.put("description", "Sample item: " + clean_key);
                items.put(item);
            }
        }

        return items;
    }

}