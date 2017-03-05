package org.lyj.ext.html.web;

import org.lyj.commons.util.RegExpUtils;
import org.lyj.commons.util.SortUtils;
import org.lyj.commons.util.StringUtils;

import java.util.*;

/**
 * Detect keywords
 */
public final class WebKeywordDetector {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private int _min_keyword_size;
    private final Set<String> _key_exclude;
    private final Map<String, String> _key_replace;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    public WebKeywordDetector() {
        _key_exclude = new HashSet<>();
        _key_replace = new HashMap<>();
    }

    public WebKeywordDetector(final int min_keyword_size) {
        this();
        this.minKeywordSize(min_keyword_size);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public int minKeywordSize() {
        return _min_keyword_size;
    }

    public WebKeywordDetector minKeywordSize(final int value) {
        _min_keyword_size = value;
        return this;
    }

    public Set<String> keyExclude() {
        return _key_exclude;
    }

    public Map<String, String> keyReplace() {
        return _key_replace;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public final List<Map.Entry<String, Integer>> detect(final String content) {
        if (StringUtils.hasText(content)) {
            final Map<String, Integer> keywords = this.detectKeywords(content);
            final List<Map.Entry<String, Integer>> result = SortUtils.sortByKeyLenght(keywords, true);
            return result;
        }
        return new ArrayList<>();
    }

    public final List<Map.Entry<String, Integer>> detect(final Set<String> content) {
        if (null != content && !content.isEmpty()) {
            final Map<String, Integer> keywords = this.detectKeywords(content);
            final List<Map.Entry<String, Integer>> result = SortUtils.sortByKeyLenght(keywords, true);
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * Merge keywords giving more importance at first levels
     *
     * @param levels levels of keywords to merge
     * @return Merged keywords level
     */
    @SafeVarargs
    public final Map<String, Double> level(final List<Map.Entry<String, Integer>>... levels) {
        final Map<String, Double> response = new HashMap<>();
        double curr_level_weight = levels.length;
        double max_weight = 0;

        // save keys adding absolute weight
        for (final List<Map.Entry<String, Integer>> level : levels) {
            final double level_size = level.size();
            for (final Map.Entry<String, Integer> entry : level) {
                final String key = entry.getKey();
                final int entry_value = entry.getValue();
                final double value = ((double) entry_value / level_size) * curr_level_weight; // multiply for level position
                final double new_val = response.containsKey(key)
                        ? response.get(key) + value
                        : value;
                // set max
                if (max_weight < new_val) {
                    max_weight = new_val;
                }
                // set key value
                response.put(key, new_val);
            }
            curr_level_weight--;
        }

        // relativize weight
        final Set<String> keys = response.keySet();
        for (final String key : keys) {
            final double value = ((double) response.get(key) / (double) max_weight) * 100;
            response.put(key, value);
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Map<String, Integer> detectKeywords(final Set<String> contents) {
        final Map<String, Integer> response = new HashMap<>();
        for (final String content : contents) {
            this.detectKeywordOcurrencies(content, response);
        }
        return response;
    }

    private Map<String, Integer> detectKeywords(final String content) {
        final Map<String, Integer> response = new HashMap<>();
        if (StringUtils.hasText(content)) {
            this.detectKeywordOcurrencies(content, response);
        }
        return response;
    }

    private void detectKeywordOcurrencies(final String content, final Map<String, Integer> response) {
        if (StringUtils.hasText(content)) {
            final String[] tokens = this.tokenize(content);
            for (final String token : tokens) {
                final String keyword = this.validateKeyword(token);
                if (StringUtils.hasText(keyword)) {
                    if (!response.containsKey(keyword)) {
                        response.put(keyword, 0);
                    }
                    response.put(token, response.get(keyword) + 1);
                }
            }
        }
    }

    private String validateKeyword(final String text) {
        if (this.isKeyword(text)) {
            if (!_key_replace.containsKey(text)) {
                return text;
            }
            return _key_replace.get(text);
        }
        return "";
    }

    private boolean isKeyword(final String text) {
        return StringUtils.hasText(text) && text.length() >= _min_keyword_size
                && !_key_exclude.contains(text);
    }

    private String[] tokenize(final String content) {
        String clear_text = RegExpUtils.replaceLineTerminators(content).replaceAll("([:;,.!?\"'])", "").toLowerCase();//RegExpUtils.matches("([0-9a-zA-Z éèàìòù])", content).toLowerCase();

        final String[] tokens = StringUtils.split(clear_text, " ", true, true);
        return tokens;
    }

}
