package org.lyj.ext.html.web.webcrawler.elements;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.JsonItem;

/**
 * Settings wrapper
 */
public class WebCrawlerSettings extends JsonItem {

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
    private static final String FLD_DOCUMENT = "document"; // document settings
    private static final String FLD_PAGE_EXCLUDE = "page_exclude";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebCrawlerSettings() {

    }

    public WebCrawlerSettings(final Object item) {
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
    public WebCrawlerSettings allowExternalLinks(final boolean value) {
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
    public WebCrawlerSettings loopDetectionThreashold(final int value) {
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

    public WebCrawlerSettings linkLimit(final int value) {
        super.put(FLD_LINK_LIMIT, value);
        return this;
    }

    public JSONArray pageExclude() {
        if(!super.has(FLD_PAGE_EXCLUDE)){
           super.put(FLD_PAGE_EXCLUDE, new JSONArray());
        }
        return super.getJSONArray(FLD_PAGE_EXCLUDE);
    }

    public WebCrawlerSettings pageExclude(final JSONArray value) {
        super.put(FLD_PAGE_EXCLUDE, value);
        return this;
    }

    public WebCrawlerDocumentSettings document() {
        if (!super.has(FLD_DOCUMENT)) {
            super.put(FLD_DOCUMENT, new JSONObject());
        }
        return new WebCrawlerDocumentSettings(super.getJSONObject(FLD_DOCUMENT));
    }

    // ------------------------------------------------------------------------
    //                      e m b e d d e d
    // ------------------------------------------------------------------------

    public static class WebCrawlerDocumentSettings extends JsonItem {

        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String FLD_AUTODETECT_CONTENT_THREASHOLD = "autodetect_content_threashold";
        private static final String FLD_MIN_KEYWORD_SIZE = "min_keyword_size";

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public WebCrawlerDocumentSettings(final JSONObject item) {
            super(item);
        }

        // ------------------------------------------------------------------------
        //                      p u b l ic
        // ------------------------------------------------------------------------

        public int autodetectContentThreashold() {
            return super.getInt(FLD_AUTODETECT_CONTENT_THREASHOLD, AUTODETECT_CONTENT_THREASHOLD);
        }

        public WebCrawlerDocumentSettings autodetectContentThreashold(final int value) {
            super.put(FLD_AUTODETECT_CONTENT_THREASHOLD, value > 0 ? value : AUTODETECT_CONTENT_THREASHOLD);
            return this;
        }

        public int minKeywordSize() {
            return super.getInt(FLD_MIN_KEYWORD_SIZE, MIN_KEYWORD_SIZE);
        }

        public WebCrawlerDocumentSettings minKeywordSize(final int value) {
            super.put(FLD_MIN_KEYWORD_SIZE, value > 0 ? value : MIN_KEYWORD_SIZE);
            return this;
        }

    }

}
