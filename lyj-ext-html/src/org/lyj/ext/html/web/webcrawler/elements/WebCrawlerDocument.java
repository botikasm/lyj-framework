package org.lyj.ext.html.web.webcrawler.elements;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.html.web.WebKeywordDetector;
import org.lyj.ext.html.web.webcrawler.IWebCrawlerConstants;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Document wrapper
 */
public class WebCrawlerDocument {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final WebCrawlerSettings.WebCrawlerDocumentSettings _settings;
    private final URL _url;
    private final String _id;
    private final String _domain;
    private final String _path;
    private final String _query;
    private final String[] _path_tokens;

    private final Set<String> _link_set;
    private final Set<String> _link_ext_set;
    private final Set<String> _media_set;
    private final Set<String> _import_set;
    private final Set<String> _image_set;
    private final Map<String, String> _microdata;
    private final Map<String, Set<String>> _titles_map;

    private String _html;  // raw html
    private String _page_title; // page title
    private String _text;   // all text in body
    private String _content; // text content (usefull for articles)

    // microdatata informations
    private String _share_title;
    private String _share_description;
    private String _share_image;
    private String _share_site;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebCrawlerDocument(final WebCrawlerSettings.WebCrawlerDocumentSettings settings,
                              final URL url) throws IOException {
        _settings = settings;
        _url = url;
        _link_set = new HashSet<>();
        _link_ext_set = new HashSet<>();
        _media_set = new HashSet<>();
        _import_set = new HashSet<>();
        _image_set = new HashSet<>();
        _titles_map = new HashMap<>();
        _microdata = new HashMap<>();

        _domain = this.getDomain(url);
        _path = this.getPath(url);
        _path_tokens = StringUtils.split(_path, "/", true, true);
        _query = url.getQuery();

        _id = MD5.encode(_domain + _path + (null != _query ? _query : ""));

        this.init(url);
    }

    @Override
    public String toString() {
        return _html;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String id() {
        return _id;
    }

    public String url() {
        return _url.toString();
    }

    public String path() {
        return _path;
    }

    public String[] pathTokens() {
        return _path_tokens;
    }

    public String domain() {
        return _domain;
    }

    public boolean containsPath(final Collection<String> paths) {
        for (final String path : paths) {
            if (this.containsPath(path)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPath(final String path) {
        final String[] tokens = StringUtils.split(path, "/", true, true);
        for (final String token : tokens) {
            if (!CollectionUtils.contains(_path_tokens, token)) {
                return false;
            }
        }
        return true;
    }

    public String html() {
        return _html;
    }

    public String pageTitle() {
        return _page_title;
    }

    public String text() {
        return _text;
    }

    public String content() {
        return null != _content ? _content : "";
    }

    public boolean hasMicrodata() {
        return StringUtils.hasText(this.shareTitle());
    }

    public String shareTitle() {
        return _share_title;
    }

    public String shareDescription() {
        return _share_description;
    }

    public String shareImage() {
        return _share_image;
    }

    public String shareSite() {
        return _share_site;
    }

    public Set<String> titles() {
        if (!_titles_map.isEmpty()) {
            if (_titles_map.containsKey("h1")) {
                return _titles_map.get("h1");
            } else if (_titles_map.containsKey("h2")) {
                return _titles_map.get("h2");
            } else if (_titles_map.containsKey("h3")) {
                return _titles_map.get("h3");
            }
        }
        return new HashSet<>();
    }

    public Set<String> h1() {
        if (_titles_map.containsKey("h1")) {
            return _titles_map.get("h1");
        }
        return new HashSet<>();
    }

    public Set<String> h2() {
        if (_titles_map.containsKey("h2")) {
            return _titles_map.get("h2");
        }
        return new HashSet<>();
    }

    public Set<String> h3() {
        if (_titles_map.containsKey("h3")) {
            return _titles_map.get("h3");
        }
        return new HashSet<>();
    }

    public Map<String, String> microdata() {
        return _microdata;
    }

    public Map<String, Set<String>> titlesMap() {
        return _titles_map;
    }

    public Set<String> urlLinks() {
        return _link_set;
    }

    public Set<String> urlLinksExternal() {
        return _link_ext_set;
    }

    public Set<String> urlMedia() {
        return _media_set;
    }

    public Set<String> urlImports() {
        return _import_set;
    }

    public Set<String> urlImages() {
        return _image_set;
    }

    public String bestTitle() {
        if (StringUtils.hasText(this.shareTitle())) {
            // microdata
            return this.shareTitle();
        } else if (!this.titles().isEmpty()) {
            // tag titles
            return this.titles().iterator().next();
        } else if (StringUtils.hasText(this.pageTitle())) {
            // page title
            return this.pageTitle();
        }
        return "";
    }

    public String bestDescription() {
        if (StringUtils.hasText(this.shareDescription())) {
            // microdata
            return this.shareDescription();
        } else if (StringUtils.hasText(this.content())) {
            // body content
            return this.content();
        }
        return "";
    }

    public String bestImage() {
        if (StringUtils.hasText(this.shareImage())) {
            // microdata
            return this.shareImage();
        } else if (this.urlImages().isEmpty()) {
            // body content
            return this.urlImages().iterator().next();
        }
        return "";
    }

    public boolean matchDomain(final String url) {
        try {
            return this.matchDomain(new URL(url));
        } catch (Throwable ignored) {
            return false;
        }
    }

    public boolean matchDomain(final URL url) {
        return this.getDomain(url).equalsIgnoreCase(_domain);
    }

    // ------------------------------------------------------------------------
    //                      k e y w o r d s
    // ------------------------------------------------------------------------

    public WebKeywordDetector keywords() {
        return new WebKeywordDetector(_settings.minKeywordSize());
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final URL url) throws IOException {
        final Document doc = Jsoup.connect(url.toString()).get().normalise();

        _html = doc.outerHtml();
        _page_title = doc.title();
        _text = doc.text();

        // detect content. 2 modes: auto, with template
        this.autodetectContent(doc);

        // links
        this.enumLinks(doc);

        this.enumMicrodata(doc);

        // titles
        this.enumTitles(doc);

    }

    private String getDomain(final URL url) {
        final String host = url.getHost().toLowerCase();
        return host.replace("www.", "");
    }

    private String getPath(final URL url) {
        final String path = url.getPath().toLowerCase();
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    private void enumLinks(final Document doc) {
        final Elements links = doc.select("a[href]");
        final Elements media = doc.select("[src]");
        final Elements imports = doc.select("link[href]");
        final Elements images = doc.select("img[src]");

        for (final Element src : media) {
            _media_set.add(src.attr("abs:src").toLowerCase());
        }

        for (final Element element : images) {
            _image_set.add(element.attr("abs:src").toLowerCase());
        }

        for (final Element link : imports) {
            _import_set.add(link.attr("abs:href").toLowerCase());
        }

        for (final Element link : links) {
            final String href = link.attr("abs:href").toLowerCase();
            if (this.matchDomain(href)) {
                _link_set.add(href);
            } else {
                _link_ext_set.add(href);
            }
        }
    }

    private void enumMicrodata(final Document doc) {
        final Elements elements = doc.select("meta");
        for (final Element element : elements) {
            final String key = element.attributes().hasKey("name") ? element.attr("name") : element.attr("property");

            if (StringUtils.hasText(key)) {
                final String content = element.attr("content");
                if (StringUtils.hasText(content)) {
                    _microdata.put(key, content);
                }
            }
        }

        _share_title = _microdata.get("og:title");
        if (!StringUtils.hasText(_share_title)) {
            _share_title = _microdata.get("twitter:title");
        }
        _share_description = _microdata.get("og:description");
        if (!StringUtils.hasText(_share_title)) {
            _share_description = _microdata.get("twitter:description");
        }
        _share_image = _microdata.get("og:image");
        if (!StringUtils.hasText(_share_title)) {
            _share_image = _microdata.get("twitter:image");
        }

        _share_site = _microdata.get("og:site_name");
        if (!StringUtils.hasText(_share_title)) {
            _share_site = _microdata.get("twitter:site");
        }

        // adjust title
        if (StringUtils.hasText(_share_title) && StringUtils.hasText(_share_site)) {
            if (_share_title.endsWith(_share_title)) {
                _share_title = _share_title.replace(_share_site, "").replace("-", "").trim();
            }
        }
    }

    private void enumTitles(final Document doc) {
        for (final String tag : IWebCrawlerConstants.TAG_TITLE) {
            this.addTitles(tag, doc.select(tag));
        }
    }

    private void addTitles(final String tag,
                           final Elements elements) {
        if (!elements.isEmpty()) {
            // initialize title set
            if (!_titles_map.containsKey(tag)) {
                _titles_map.put(tag, new HashSet<>());
            }

            // loop on elements
            for (final Element element : elements) {
                final String text = element.text();
                if (StringUtils.hasText(text)) {
                    _titles_map.get(tag).add(text);
                }
            }
        }
    }

    private void autodetectContent(final Document doc) {
        final StringBuilder sb = new StringBuilder();
        this.autodetectContent(doc.select("p"), sb);
        if (null == _content || _content.length() < _settings.autodetectContentThreashold()) {
            this.autodetectContent(doc.select("div"), sb);
        }
        _content = sb.toString();
    }

    private void autodetectContent(final Elements elements, final StringBuilder content) {
        if (null != elements && !elements.isEmpty()) {
            for (final Element element : elements) {
                final String text = element.text();
                if (text.length() > _settings.autodetectContentThreashold()) {
                    content.append(" ");
                    content.append(text);
                }
            }
        }
    }


}
