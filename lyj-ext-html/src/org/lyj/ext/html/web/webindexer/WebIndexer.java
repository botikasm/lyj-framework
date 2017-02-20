package org.lyj.ext.html.web.webindexer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.async.Async;
import org.lyj.commons.util.*;
import org.lyj.ext.html.web.WebKeywordDetector;
import org.lyj.ext.html.web.webcrawler.WebCrawler;
import org.lyj.ext.html.web.webcrawler.elements.WebCrawlerDocument;
import org.lyj.ext.html.web.webcrawler.elements.WebCrawlerPathList;
import org.lyj.ext.html.web.webcrawler.exceptions.MissingUrlException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Inheritable class to create a web indexer
 */
public abstract class WebIndexer {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final List<WebIndexerSettings> _sites;

    private int _count_indexed_pages;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * Creates new indexer instance
     * @param settings Array of JSON objects (WebIndexerSettings)
     */
    public WebIndexer(final JSONArray settings) {
        _sites = new ArrayList<>();
        _count_indexed_pages = 0;

        this.init(settings);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Async indexing of a list of urls
     */
    public void startIndexing() {
        for (final WebIndexerSettings site : _sites) {
            Async.invoke((args) -> {
                try {
                    this.index(site);
                } catch (Throwable t) {
                    this.onError(t);
                }
            });
        }
    }

    public int countIndexedPages() {
        return _count_indexed_pages;
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    protected abstract void onError(final Throwable t);

    protected abstract void onTaskError(final String task_id, final String url, final Throwable t);

    protected abstract void onTaskStarted(final String task_id, final WebIndexerSettings site);

    protected abstract void onTaskFinished(final String task_id, final WebCrawlerPathList paths);

    protected abstract void onTaskIndex(final String task_id, final WebIndexerSettings site,
                                        final Map<String, Double> keywords, final String title,
                                        final String description, final String image, final String url);

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final JSONArray settings) {
        _sites.clear();
        CollectionUtils.forEach(settings, (item) -> {
            if (item instanceof JSONObject) {
                _sites.add(new WebIndexerSettings(item));
            }
        });
    }

    private void index(final WebIndexerSettings setting)
            throws InterruptedException, MissingUrlException, MalformedURLException {

        final String task_id = RandomUtils.randomUUID();
        final String param_url = setting.url();
        if (StringUtils.hasText(param_url)) {

            this.onTaskStarted(task_id, setting);

            // creates new crawler for each site to index
            final WebCrawler crawler = new WebCrawler();

            crawler.settings().document().minKeywordSize(setting.keySize());
            crawler.settings().document().autodetectContentThreashold(setting.contentSize());

            crawler.onError((url, err) -> {
                onTaskError(task_id, url.toString(), err);
            });
            crawler.onResult((document) -> {
                index(task_id, crawler, document, setting);
            });

            crawler.onFinish((url_list) -> {
                this.onTaskFinished(task_id, url_list);
            });

            crawler.start(param_url);
        }
    }

    private void index(final String task_id,
                       final WebCrawler crawler,
                       final WebCrawlerDocument document,
                       final WebIndexerSettings setting) {
        final List<String> param_exclude = JsonWrapper.toListOfString(setting.exclude());

        if (!PathUtils.pathMatchOne(document.url(), param_exclude)) {

            final int page_limit = setting.pageLimit();

            if (page_limit==-1 || (page_limit > 0 && _count_indexed_pages < page_limit)) {
                _count_indexed_pages++;

                final String title = document.bestTitle();
                final String description = document.bestDescription();
                final String image = document.bestImage();

                final WebKeywordDetector keyword_detector = document.keywords();
                final Map<String, Double> keywords = keyword_detector.level(keyword_detector.detect(title),
                        keyword_detector.detect(description));

                if (!keywords.isEmpty()) {
                    this.onTaskIndex(task_id, setting, keywords,
                            title, description, image, document.url());
                }
            } else {
                // STOP INDEXING
                crawler.stop();
            }
        }
    }

}
