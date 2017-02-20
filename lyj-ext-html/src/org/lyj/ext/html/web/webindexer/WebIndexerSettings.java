package org.lyj.ext.html.web.webindexer;

import org.json.JSONArray;
import org.lyj.commons.util.JsonItem;

/**
 *
 */
public class WebIndexerSettings extends JsonItem {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String FLD_URL = "url"; // root url
    private final String FLD_EXCLUDE = "exclude"; // excluded paths from indexing
    private final String FLD_KEY_SIZE = "key_size";
    private final String FLD_PAGE_LIMIT = "page_limit"; // max number of indexable pages
    private final String FLD_PAGE_KEY_LIMIT = "page_key_limit";   // max number of keywords per page
    private final String FLD_CONTENT_SIZE = "content_size"; // min content threashold
    private final String FLD_DEFAULT_IMAGE = "default_image";
    
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebIndexerSettings() {

    }

    public WebIndexerSettings(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String defaultImage() {
        return super.getString(FLD_DEFAULT_IMAGE);
    }

    public WebIndexerSettings defaultImage(final String value) {
        super.put(FLD_DEFAULT_IMAGE, value);
        return this;
    }

    public String url() {
        return super.getString(FLD_URL);
    }

    public WebIndexerSettings url(final String value) {
        super.put(FLD_URL, value);
        return this;
    }

    public int keySize() {
        return super.getInt(FLD_KEY_SIZE, 5);
    }

    public WebIndexerSettings keySize(final int value) {
        super.put(FLD_KEY_SIZE, value);
        return this;
    }

    public int pageLimit() {
        return super.getInt(FLD_PAGE_LIMIT, 100);
    }

    public WebIndexerSettings pageLimit(final int value) {
        super.put(FLD_PAGE_LIMIT, value);
        return this;
    }

    public int pageKeyLimit() {
        return super.getInt(FLD_PAGE_KEY_LIMIT, 5);
    }

    public WebIndexerSettings pageKeyLimit(final int value) {
        super.put(FLD_PAGE_KEY_LIMIT, value);
        return this;
    }

    public int contentSize() {
        return super.getInt(FLD_CONTENT_SIZE, 200);
    }

    public WebIndexerSettings contentSize(final int value) {
        super.put(FLD_CONTENT_SIZE, value);
        return this;
    }

    public JSONArray exclude() {
        if (!super.has(FLD_EXCLUDE)) {
            super.put(FLD_EXCLUDE, new JSONArray());
        }
        return super.getJSONArray(FLD_EXCLUDE);
    }

    public WebIndexerSettings exclude(final JSONArray value) {
        super.put(FLD_EXCLUDE, value);
        return this;
    }

}
