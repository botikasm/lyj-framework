package org.lyj.ext.html.utils;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.*;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.ext.html.IHtmlConstants;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public final class RssParser {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String TYPE_RSS_2 = IHtmlConstants.TYPE_RSS_2;

    private static final String NODE_CHANNEL = "channel";
    private static final String NODE_IMAGE = "image";
    private static final String NODE_ITEM = "item";

    private static final String NODE_LINK = "link";
    private static final String NODE_TITLE = "title";
    private static final String NODE_DESCRIPTION = "description";
    private static final String NODE_ENCLOSURE = "enclosure";
    private static final String NODE_CONTENT = "content";
    private static final String NODE_CONTENT_ENCODED = "content:encoded";

    private static final String ATTR_URL = "url";

    private static final Map<String, String> REMAP_NODES = MapBuilder.createSS()
            .put(NODE_CONTENT_ENCODED, NODE_CONTENT)
            .toMap();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _type;
    private final String _xml;

    private final Document _parser;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RssParser(final String type, final String xml) {
        _type = type;
        _xml = xml;

        _parser = Jsoup.parse(xml, "", Parser.xmlParser());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String type() {
        return _type;
    }

    public boolean isRss2() {
        return _type.equalsIgnoreCase(TYPE_RSS_2);
    }

    public String xml() {
        return _xml;
    }

    public Channel channel() {
        return this.toChannel(this.getElementChannel());
    }

    public Item getLatestItem() {
        return this.toItem(this.getElementsItem(0));
    }

    public Item item(final int index) {
        return this.toItem(this.getElementsItem(index));
    }

    public void forEachItem(final Delegates.Callback<Item> callback) {
        if (null != callback) {
            this.getElementsItem().forEach((element) -> {
                final Item item = this.toItem(element);
                if (null != item) {
                    callback.handle(item);
                }
            });
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Element getElementChannel() {
        return _parser.select(NODE_ITEM).first();
    }

    private boolean hasItems() {
        return _parser.select(NODE_ITEM).size() > 0;
    }

    private int countItems() {
        return _parser.select(NODE_ITEM).size();
    }

    private Elements getElementsItem() {
        return _parser.select(NODE_ITEM);
    }

    private Element getElementsItem(final int index) {
        if (this.countItems() > index) {
            return this.getElementsItem().get(index);
        }
        return null;
    }

    private Item toItem(final Element element) {
        if (null != element) {
            final Item item = new Item();

            element.children().forEach((node) -> {
                final String tag = node.nodeName();
                if (tag.equalsIgnoreCase(NODE_ENCLOSURE)) {
                    put(item, tag, node.attr(ATTR_URL));
                } else {
                    put(item, tag, node.text());
                }
            });

            return item;
        }
        return null;
    }

    private Channel toChannel(final Element element) {
        if (null != element) {
            final Channel item = new Channel();

            element.children().forEach((node) -> {
                final String tag = node.nodeName();
                put(item, tag, node.text());
            });

            return item;
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static String decode(final String text) {
        return StringEscapeUtils.unescapeHtml3(text);
    }

    private static String text(final String text) {
        return Jsoup.parse(text).text();
    }

    private static String html(final String text) {
        return Jsoup.parse(text).val();
    }

    private static String key(final String rss_key) {
        if (REMAP_NODES.containsKey(rss_key)) {
            return REMAP_NODES.get(rss_key);
        }
        return rss_key;
    }

    private static void put(final JsonItem item, final String raw_key, final String raw_value) {
        final String key = key(raw_key);
        final String text_value = text(raw_value);
        final String html_value = decode(raw_value);
        if (item.has(key)) {
            // category or other text array
            final Object item_val = item.get(key);
            if (item_val instanceof JSONArray) {
                ((JSONArray) item_val).put(text_value);
            } else {
                item.put(key, new JSONArray());
                item.getJSONArray(key).put(item_val);
                item.getJSONArray(key).put(text_value);
            }
        } else {
            item.put(key(key), text_value);
            if (!text_value.equalsIgnoreCase(html_value)) {
                item.put(key(key) + "_html", html_value);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      I t e m
    // ------------------------------------------------------------------------

    public static class Channel
            extends JsonItem {


        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String FLD_LINK = "link";
        private static final String FLD_TITLE = "title";
        private static final String FLD_DESCRIPTION = "description";
        private static final String FLD_LANGUAGE = "language";
        private static final String FLD_LAST_BUILD_DATE = "lastBuildDate";

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public Channel() {
            super();
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public String link() {
            return super.getString(FLD_LINK);
        }

        public Channel link(final String value) {
            super.put(FLD_LINK, value);
            return this;
        }

        public String title() {
            return super.getString(FLD_TITLE);
        }

        public Channel title(final String value) {
            super.put(FLD_TITLE, value);
            return this;
        }

        public String description() {
            return super.getString(FLD_DESCRIPTION);
        }

        public Channel description(final String value) {
            super.put(FLD_DESCRIPTION, value);
            return this;
        }

        public String language() {
            return super.getString(FLD_LANGUAGE);
        }

        public Channel language(final String value) {
            super.put(FLD_LANGUAGE, value);
            return this;
        }

        public String lastBuildDate() {
            return super.getString(FLD_LAST_BUILD_DATE);
        }

        public Channel lastBuildDate(final String value) {
            super.put(FLD_LAST_BUILD_DATE, value);
            return this;
        }

    }

    // ------------------------------------------------------------------------
    //                      I t e m
    // ------------------------------------------------------------------------

    public static class Item
            extends JsonItem {

        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String FLD_LINK = "link";
        private static final String FLD_TITLE = "title";
        private static final String FLD_DESCRIPTION = "description";
        private static final String FLD_CONTENT = "content";
        private static final String FLD_CONTENT_HTML = "content_html";
        private static final String FLD_CATEGORY = "category";
        private static final String FLD_IMAGE = "image";
        private static final String FLD_ENCLOSURE = "enclosure"; // usually are images
        private static final String FLD_PUBDATE = "pubdate";
        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public Item() {
            super();
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public String link() {
            return super.getString(FLD_LINK);
        }

        public Item link(final String value) {
            super.put(FLD_LINK, value);
            return this;
        }

        public String title() {
            return super.getString(FLD_TITLE);
        }

        public Item title(final String value) {
            super.put(FLD_TITLE, value);
            return this;
        }

        public String description() {
            return super.getString(FLD_DESCRIPTION);
        }

        public Item description(final String value) {
            super.put(FLD_DESCRIPTION, value);
            return this;
        }

        public String content() {
            return super.getString(FLD_CONTENT);
        }

        public Item content(final String value) {
            super.put(FLD_CONTENT, value);
            return this;
        }

        public String contentHtml() {
            return StringUtils.hasText(super.getString(FLD_CONTENT_HTML))
                    ? super.getString(FLD_CONTENT_HTML)
                    : this.content();
        }

        public Item contentHtml(final String value) {
            super.put(FLD_CONTENT_HTML, value);
            return this;
        }

        public String pubDate() {
            return super.getString(FLD_PUBDATE);
        }

        public Item pubDate(final String value) {
            super.put(FLD_PUBDATE, value);
            return this;
        }

        public String image() {
            return super.getString(FLD_IMAGE);
        }

        public Item image(final String value) {
            super.put(FLD_IMAGE, value);
            return this;
        }

        public JSONArray category() {
            if (!super.has(FLD_CATEGORY)) {
                super.put(FLD_CATEGORY, new JSONArray());
            } else {
                final Object existing = super.get(FLD_CATEGORY);
                if (!(existing instanceof JSONArray)) {
                    super.put(FLD_CATEGORY, new JSONArray());
                    super.getJSONArray(FLD_CATEGORY).put(existing);
                }
            }
            return super.getJSONArray(FLD_CATEGORY);
        }

        public Item category(final JSONArray value) {
            super.put(FLD_CATEGORY, value);
            return this;
        }

        public Collection<String> categories() {
            return JsonWrapper.toListOfString(this.category());
        }

        public JSONArray enclosure() {
            if (!super.has(FLD_ENCLOSURE)) {
                super.put(FLD_ENCLOSURE, new JSONArray());
            } else {
                final Object existing = super.get(FLD_ENCLOSURE);
                if (!(existing instanceof JSONArray)) {
                    super.put(FLD_ENCLOSURE, new JSONArray());
                    super.getJSONArray(FLD_ENCLOSURE).put(existing);
                }
            }
            return super.getJSONArray(FLD_ENCLOSURE);
        }

        public Item enclosure(final JSONArray value) {
            super.put(FLD_ENCLOSURE, value);
            return this;
        }

        public Collection<String> enclosures() {
            return JsonWrapper.toListOfString(this.enclosure());
        }

        public String enclosure(final int index) {
            return CollectionUtils.get(this.enclosures(), index, "");
        }

    }


}
