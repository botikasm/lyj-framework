package org.lyj.ext.html.web.grabber;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.JsonWrapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Settings wrapper
 */
public class CrawlerSettings
        extends JsonItem {

    // crawler settings
    private static final int MAX_LOOP = 3000;

    // document settings
    private static final int AUTODETECT_CONTENT_THREASHOLD = 200; // min content size;
    private static final int MIN_KEYWORD_SIZE = 4;

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_NAVIGATE_EXTERNAL = "allow_external_links";
    private static final String FLD_LINK_LIMIT = "link_limit";
    private static final String FLD_LOOP_DETECTION_THREASHOLD = "loop_detection_threashold";
    private static final String FLD_PAGE_EXCLUDE = "page_exclude";
    private static final String FLD_DOCUMENT = "document"; // document settings


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CrawlerSettings() {

    }

    public CrawlerSettings(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------



    /**
     * Allow/Disallow external link navigation
     */
    public boolean allowExternalLinks() {
        return super.getBoolean(FLD_NAVIGATE_EXTERNAL);
    }

    /**
     * Allow/Disallow external link navigation
     */
    public CrawlerSettings allowExternalLinks(final boolean value) {
        super.put(FLD_NAVIGATE_EXTERNAL, value);
        return this;
    }

    /**
     * Max number of loops on same page.
     * Once reached this limit the crawler stop working.
     */
    public int loopDetectionThreashold() {
        return super.getInt(FLD_LOOP_DETECTION_THREASHOLD, MAX_LOOP);
    }

    /**
     * Set Max number of loops on same page.
     * Once reached this limit the crawler stop working.
     */
    public CrawlerSettings loopDetectionThreashold(final int value) {
        super.put(FLD_LOOP_DETECTION_THREASHOLD, value);
        return this;
    }

    /**
     * Max number of links to navigate
     * (DEFAULT no limits = -1)
     */
    public int linkLimit() {
        return super.getInt(FLD_LINK_LIMIT, -1);
    }

    public CrawlerSettings linkLimit(final int value) {
        super.put(FLD_LINK_LIMIT, value);
        return this;
    }

    public JSONArray pageExclude() {
        if (!super.has(FLD_PAGE_EXCLUDE)) {
            super.put(FLD_PAGE_EXCLUDE, new JSONArray());
        }
        return super.getJSONArray(FLD_PAGE_EXCLUDE);
    }

    public CrawlerSettings pageExclude(final JSONArray value) {
        super.put(FLD_PAGE_EXCLUDE, value);
        return this;
    }

    public CrawlerDocumentSettings document() {
        if (!super.has(FLD_DOCUMENT)) {
            super.put(FLD_DOCUMENT, new JSONObject());
        }
        return new CrawlerDocumentSettings(super.getJSONObject(FLD_DOCUMENT));
    }

    // ------------------------------------------------------------------------
    //                      e m b e d d e d
    // ------------------------------------------------------------------------

    public static class CrawlerDocumentSettings
            extends JsonItem {

        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String FLD_TYPE = "type";
        private static final String FLD_AUTODETECT_CONTENT_THREASHOLD = "autodetect_content_threashold";
        private static final String FLD_MIN_KEYWORD_SIZE = "min_keyword_size";
        private static final String FLD_KEY_EXCLUDE = "key_exclude";
        private static final String FLD_KEY_REPLACE = "key_replace";

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public CrawlerDocumentSettings(final JSONObject item) {
            super(item);
        }

        // ------------------------------------------------------------------------
        //                      p u b l ic
        // ------------------------------------------------------------------------

        public String type() {
            return super.getString(FLD_TYPE, IWebCrawlerConstants.TYPE_HTML);
        }

        public CrawlerDocumentSettings type(final String value) {
            super.put(FLD_TYPE, value);
            return this;
        }

        public int autodetectContentThreashold() {
            return super.getInt(FLD_AUTODETECT_CONTENT_THREASHOLD, AUTODETECT_CONTENT_THREASHOLD);
        }

        public CrawlerDocumentSettings autodetectContentThreashold(final int value) {
            super.put(FLD_AUTODETECT_CONTENT_THREASHOLD, value > 0 ? value : AUTODETECT_CONTENT_THREASHOLD);
            return this;
        }

        public int minKeywordSize() {
            return super.getInt(FLD_MIN_KEYWORD_SIZE, MIN_KEYWORD_SIZE);
        }

        public CrawlerDocumentSettings minKeywordSize(final int value) {
            super.put(FLD_MIN_KEYWORD_SIZE, value > 0 ? value : MIN_KEYWORD_SIZE);
            return this;
        }

        public JSONArray keyExclude() {
            if (!super.has(FLD_KEY_EXCLUDE)) {
                super.put(FLD_KEY_EXCLUDE, new JSONArray());
            }
            return super.getJSONArray(FLD_KEY_EXCLUDE);
        }

        public CrawlerDocumentSettings keyExclude(final JSONArray value) {
            super.put(FLD_KEY_EXCLUDE, value);
            return this;
        }

        public JSONObject keyReplace() {
            if (!super.has(FLD_KEY_REPLACE)) {
                super.put(FLD_KEY_REPLACE, new JSONObject());
            }
            return super.getJSONObject(FLD_KEY_REPLACE);
        }

        public CrawlerDocumentSettings keyReplace(final JSONObject value) {
            super.put(FLD_KEY_REPLACE, value);
            return this;
        }

        public Map<String, String> keyReplaceMap() {
            return JsonWrapper.toMapOfString(this.keyReplace());
        }

        public Set<String> keyExcludeSet() {
            return new HashSet<>(JsonWrapper.toListOfString(this.keyExclude()));
        }
    }

}
