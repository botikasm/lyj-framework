package org.lyj.ext.html.web.grabber.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.html.utils.HtmlParserUtil;
import org.lyj.ext.html.web.grabber.CrawlerSettings;
import org.lyj.ext.html.web.grabber.DocItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.Callable;

public class HtmlGrabberTask
        implements Callable<DocItem> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int TIMEOUT = 60000;   // one minute

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final CrawlerSettings _settings;
    private final DocItem _doc;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HtmlGrabberTask(final CrawlerSettings settings,
                           final URL url,
                           final int depth) {
        _settings = settings;
        _doc = new DocItem(url, depth);
    }

    @Override
    public DocItem call() throws Exception {
        this.init();

        return _doc;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() throws Exception {
        this.initDocument(Jsoup.parse(_doc.url(), TIMEOUT));
    }

    private void initDocument(final Document document) {
        if (null != document) {

            // get links from document
            _doc.links().addAll(HtmlParserUtil.getLinks(document, _doc.url()));

            _doc.content(HtmlParserUtil.getText(document, _settings.document().autodetectContentThreashold()));
            _doc.html(document.outerHtml());
            _doc.text(document.text());

            final HtmlParserUtil.Microdata microdata = HtmlParserUtil.getMicrodata(document);
            _doc.title(microdata.title);
            _doc.description(microdata.description);
            _doc.image(microdata.image);

            if (!StringUtils.hasText(_doc.title())) {
                _doc.title(HtmlParserUtil.getTitle(document));
            }
            if (!StringUtils.hasText(_doc.description())) {
                _doc.description(StringUtils.leftStr(_doc.content(), 255, true));
            }
            if (!StringUtils.hasText(_doc.image())) {
                final Set<URL> images = HtmlParserUtil.getImages(document, _doc.url());
                if (!images.isEmpty()) {
                    _doc.image(images.iterator().next().toString());
                }
            }


        }
    }

    private void processLinks(final Elements links) {
        for (Element link : links) {
            final String href = link.attr("href");
            if (!StringUtils.hasText(href)
                    || href.startsWith("#")) {
                continue;
            }

            try {
                final URL nextUrl = new URL(_doc.url(), href);
                _doc.links().add(nextUrl);
            } catch (MalformedURLException ignored) { // ignore bad urls
            }
        }
    }


}
