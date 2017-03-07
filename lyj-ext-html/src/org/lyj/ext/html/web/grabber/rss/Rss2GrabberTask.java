package org.lyj.ext.html.web.grabber.rss;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.lyj.commons.network.URLUtils;
import org.lyj.commons.util.DateWrapper;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.html.utils.HtmlParserUtil;
import org.lyj.ext.html.utils.RssParser;
import org.lyj.ext.html.web.grabber.CrawlerSettings;
import org.lyj.ext.html.web.grabber.DocItem;

import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class Rss2GrabberTask
        implements Callable<List<DocItem>> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int TIMEOUT = 60000;   // one minute

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final List<DocItem> _docs;
    private final CrawlerSettings _settings;
    private final URL _url;
    private final int _depth;
    private final String _xml;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Rss2GrabberTask(final CrawlerSettings settings,
                           final URL url,
                           final int depth) throws Exception {
        _docs = new LinkedList<>();
        _settings = settings;
        _url = url;
        _depth = depth;

        _xml = URLUtils.getUrlContent(_url.toString(), TIMEOUT);
        // validate xml
        if (!StringUtils.hasText(_xml) || !StringUtils.isXml(_xml)) {
            throw new Exception("Invalid XML");
        }
    }

    @Override
    public List<DocItem> call() throws Exception {
        this.init();

        return _docs;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() throws Exception {
        final RssParser parser = new RssParser(_settings.document().type(), _xml);
        parser.forEachItem((item) -> {
            try {

                final DocItem doc = new DocItem(new URL(item.link()), _depth);

                doc.title(item.title());
                doc.description(item.description());
                doc.image(item.image());

                doc.html(item.contentHtml());
                doc.text(item.content());
                doc.content(item.content());

                doc.keywords().addAll(item.categories());

                if (!StringUtils.hasText(doc.image())) {
                    final Document html = Jsoup.parse(doc.html());
                    final Set<URL> images = HtmlParserUtil.getImages(html, doc.url());
                    if (!images.isEmpty()) {
                        doc.image(images.iterator().next().toString());
                    }

                    if (!StringUtils.hasText(doc.image())) {
                        // custom parameter
                        doc.image(item.enclosure(0));
                    }
                }

                if(StringUtils.hasText(item.pubDate())){
                    doc.date(item.pubDate());
                }

                _docs.add(doc);

            } catch (Throwable ignord) {
                // bad url
            }
        });


    }


}
