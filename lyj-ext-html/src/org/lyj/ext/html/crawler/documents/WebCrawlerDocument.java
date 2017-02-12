package org.lyj.ext.html.crawler.documents;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.html.crawler.IWebCrawlerConstants;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Document wrapper
 */
public class WebCrawlerDocument {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int AUTODETECT_CONTENT_THREASHOLD = 200; // min content size;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

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
    private final Map<String, Set<String>> _titles;

    private String _html;  // raw html
    private String _title; // page title
    private String _text;   // all text in body
    private String _content; // text content (usefull for articles)

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebCrawlerDocument(final URL url) throws IOException {
        _url = url;
        _link_set = new HashSet<>();
        _link_ext_set = new HashSet<>();
        _media_set = new HashSet<>();
        _import_set = new HashSet<>();
        _titles = new HashMap<>();

        _domain = this.getDomain(url);
        _path = url.getPath();
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

    public String html() {
        return _html;
    }

    public String title() {
        return _title;
    }

    public String text() {
        return _text;
    }

    public String content() {
        return null != _content ? _content : "";
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

    public boolean containsPath(final String path) {
        final String[] tokens = StringUtils.split(path, "/", true, true);
        for (final String token : tokens) {
            if (!CollectionUtils.contains(_path_tokens, token)) {
                return false;
            }
        }
        return true;
    }

    public Set<String> h1() {
        if (_titles.containsKey("h1")) {
            return _titles.get("h1");
        }
        return new HashSet<>();
    }

    public Set<String> h2() {
        if (_titles.containsKey("h2")) {
            return _titles.get("h2");
        }
        return new HashSet<>();
    }

    public Set<String> h3() {
        if (_titles.containsKey("h3")) {
            return _titles.get("h3");
        }
        return new HashSet<>();
    }

    public Map<String, Set<String>> titles() {
        return _titles;
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
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final URL url) throws IOException {
        final Document doc = Jsoup.connect(url.toString()).get().normalise();

        _html = doc.outerHtml();
        _title = doc.title();
        _text = doc.text();

        // detect content. 2 modes: auto, with template
        this.autodetectContent(doc);

        // links
        this.enumLinks(doc);

        // titles
        this.enumTitles(doc);
    }

    private String getDomain(final URL url) {
        final String host = url.getHost().toLowerCase();
        return host.replace("www.", "");
    }

    private void enumLinks(final Document doc) {
        final Elements links = doc.select("a[href]");
        final Elements media = doc.select("[src]");
        final Elements imports = doc.select("link[href]");

        for (final Element src : media) {
            _media_set.add(src.attr("abs:src").toLowerCase());
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

    private void enumTitles(final Document doc) {
        for (final String tag : IWebCrawlerConstants.TAG_TITLE) {
            this.addTitles(tag, doc.select(tag));
        }
    }

    private void addTitles(final String tag,
                           final Elements elements) {
        if (!elements.isEmpty()) {
            if (!_titles.containsKey(tag)) {
                _titles.put(tag, new HashSet<>());
            }
            for (final Element element : elements) {
                final String text = element.text();
                if (StringUtils.hasText(text)) {
                    _titles.get(tag).add(text);
                }
            }
        }
    }

    private void autodetectContent(final Document doc) {
        final StringBuilder sb = new StringBuilder();
        this.autodetectContent(doc.select("p"), sb);
        if (null == _content || _content.length() < AUTODETECT_CONTENT_THREASHOLD) {
            this.autodetectContent(doc.select("div"), sb);
        }
        _content = sb.toString();
    }

    private void autodetectContent(final Elements elements, final StringBuilder content) {
        if (null != elements && !elements.isEmpty()) {
            for (final Element element : elements) {
                final String text = element.text();
                if (text.length() > AUTODETECT_CONTENT_THREASHOLD) {
                    content.append(" ");
                    content.append(text);
                }
            }
        }
    }


}
