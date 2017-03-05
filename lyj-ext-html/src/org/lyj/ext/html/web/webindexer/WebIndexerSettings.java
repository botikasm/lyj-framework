package org.lyj.ext.html.web.webindexer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.ext.html.web.grabber.IWebCrawlerConstants;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class WebIndexerSettings
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String FLD_TYPE = "type";
    private final String FLD_CHANNEL_TAGS = "channel_tags"; // array of channels
    private final String FLD_UPDATE_EXISTING = "update_existing"; // root url
    private final String FLD_URL = "url"; // root url

    private final String FLD_PAGE_EXCLUDE = "page_exclude"; // excluded from crawler
    private final String FLD_EXCLUDE = "exclude"; // excluded paths from indexing

    private final String FLD_KEY_SIZE = "key_size";
    private final String FLD_KEY_EXCLUDE = "key_exclude";
    private final String FLD_KEY_REPLACE = "key_replace";
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

    public String type() {
        return super.getString(FLD_TYPE, IWebCrawlerConstants.TYPE_HTML);
    }

    public WebIndexerSettings type(final String value) {
        super.put(FLD_TYPE, value);
        return this;
    }

    public boolean updateExisting() {
        return super.getBoolean(FLD_UPDATE_EXISTING);
    }

    public WebIndexerSettings updateExisting(final boolean value) {
        super.put(FLD_UPDATE_EXISTING, value);
        return this;
    }

    public JSONArray channelTags() {
        if(!super.has(FLD_CHANNEL_TAGS)){
           super.put(FLD_CHANNEL_TAGS, new JSONArray());
        }
        return super.getJSONArray(FLD_CHANNEL_TAGS);
    }

    public WebIndexerSettings channelTags(final JSONArray value) {
        super.put(FLD_CHANNEL_TAGS, value);
        return this;
    }

    public String url() {
        return super.getString(FLD_URL);
    }

    public WebIndexerSettings url(final String value) {
        super.put(FLD_URL, value);
        return this;
    }

    public String defaultImage() {
        return super.getString(FLD_DEFAULT_IMAGE);
    }

    public WebIndexerSettings defaultImage(final String value) {
        super.put(FLD_DEFAULT_IMAGE, value);
        return this;
    }

    public int keySize() {
        return super.getInt(FLD_KEY_SIZE, 5);
    }

    public WebIndexerSettings keySize(final int value) {
        super.put(FLD_KEY_SIZE, value);
        return this;
    }

    public JSONArray keyExclude() {
        if (!super.has(FLD_KEY_EXCLUDE)) {
            super.put(FLD_KEY_EXCLUDE, new JSONArray());
        }
        return super.getJSONArray(FLD_KEY_EXCLUDE);
    }

    public WebIndexerSettings keyExclude(final JSONArray value) {
        super.put(FLD_KEY_EXCLUDE, value);
        return this;
    }

    public Set<String> keyExcludeSet() {
        return new HashSet<>(JsonWrapper.toListOfString(this.keyExclude()));
    }

    public JSONObject keyReplace() {
        if (!super.has(FLD_KEY_REPLACE)) {
            super.put(FLD_KEY_REPLACE, new JSONObject());
        }
        return super.getJSONObject(FLD_KEY_REPLACE);
    }

    public Map<String, String> keyReplaceMap() {
        return JsonWrapper.toMapOfString(this.keyReplace());
    }

    public WebIndexerSettings keyReplace(final JSONObject value) {
        super.put(FLD_KEY_REPLACE, value);
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

    public JSONArray pageExclude() {
        if (!super.has(FLD_PAGE_EXCLUDE)) {
            super.put(FLD_PAGE_EXCLUDE, new JSONArray());
        }
        return super.getJSONArray(FLD_PAGE_EXCLUDE);
    }

    public WebIndexerSettings pageExclude(final JSONArray value) {
        super.put(FLD_PAGE_EXCLUDE, value);
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
