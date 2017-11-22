package org.lyj.ext.html.web.webindexer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.async.Async;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.*;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.ext.html.web.WebKeywordDetector;
import org.lyj.ext.html.web.grabber.AbstractGrabber;
import org.lyj.ext.html.web.grabber.DocItem;
import org.lyj.ext.html.web.grabber.GrabberFactory;

import java.net.URL;
import java.util.*;

/**
 * Inheritable class to create a web indexer
 */
public abstract class WebIndexer {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final List<WebIndexerSettings> _sites;
    private final Map<String, AbstractGrabber> _crawlers;

    private int _count_indexed_pages;

    private boolean _finished;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * Creates new indexer instance
     *
     * @param settings Array of JSON objects (WebIndexerSettings)
     */
    public WebIndexer(final JSONArray settings) {
        _sites = new ArrayList<>();
        _crawlers = new HashMap<>();
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

    public boolean isFinished() {
        return _finished;
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    protected abstract void onError(final Throwable t);

    protected abstract void onTaskError(final String task_group_id, final String task_id, final String url, final Throwable t);

    protected abstract void onTaskStarted(final String task_group_id, final String task_id, final WebIndexerSettings site);

    protected abstract void onTaskFinished(final String task_group_id, final String task_id, final Set<URL> paths);

    protected abstract void onTaskIndex(final String task_group_id, final String task_id, final WebIndexerSettings config,
                                        final Map<String, Double> keywords, final String title,
                                        final String description, final String image,
                                        final Date date, final String url);

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
            throws Exception {

        final String task_id = RandomUtils.randomUUID();
        final String task_group_id = MD5.encode(setting.toString());

        final String param_url = setting.url();
        final String param_type = setting.type();

        if (StringUtils.hasText(param_url)) {

            // creates new crawler for each site to index
            final AbstractGrabber<DocItem> crawler = GrabberFactory.create(param_type);
            if (null != crawler) {

                this.onTaskStarted(task_group_id, task_id, setting);

                _crawlers.put(crawler.uuid(), crawler);

                crawler.settings().pagingMode(setting.rssPaging());     // only for RSS
                crawler.settings().pageExclude(setting.pageExclude());  // exclude from crawler
                crawler.settings().document().type(param_type);
                crawler.settings().document().minKeywordSize(setting.keySize());
                crawler.settings().document().autodetectContentThreashold(setting.contentSize());
                crawler.settings().document().keyExclude(setting.keyExclude());
                crawler.settings().document().keyReplace(setting.keyReplace());

                crawler.onError((url, err) -> {
                    onTaskError(task_group_id, task_id, url.toString(), err);
                });
                crawler.onResult((document) -> {
                    index(task_group_id, task_id, crawler, document, setting);
                });

                crawler.onFinish((instance) -> {
                    this.finish(task_group_id, task_id, crawler, crawler.urls());
                });

                crawler.startAsync(param_url);
            } else {
                this.finish(task_group_id, task_id, null, new HashSet<>());
                throw new Exception("Crawler not fount for type: " + param_type);
            }
        }
    }

    private void index(final String task_group_id,
                       final String task_id,
                       final AbstractGrabber crawler,
                       final DocItem document,
                       final WebIndexerSettings setting) {
        final List<String> param_exclude = JsonWrapper.toListOfString(setting.exclude());

        if (!PathUtils.pathMatchOne(document.urlNoHash(), param_exclude)) {

            final int page_limit = setting.pageLimit();

            if (page_limit == -1 || (page_limit > 0 && _count_indexed_pages < page_limit)) {
                _count_indexed_pages++;

                final String title = document.title();
                final String description = document.description();
                final String image = document.image();
                final Date date = document.date();

                final WebKeywordDetector keyword_detector = this.keywordTool(setting);
                final Map<String, Double> keywords = keyword_detector.level(
                        keyword_detector.detect(document.keywords()),
                        keyword_detector.detect(title),
                        keyword_detector.detect(description));

                if (!keywords.isEmpty()) {
                    this.onTaskIndex(task_group_id, task_id, setting, keywords,
                            title, description, image, date, document.urlNoHash());
                }
            } else {
                // STOP INDEXING
                crawler.stop();
            }
        }
    }

    private WebKeywordDetector keywordTool(final WebIndexerSettings settings) {
        final WebKeywordDetector detector = new WebKeywordDetector(settings.keySize());
        detector.keyReplace().putAll(settings.keyReplaceMap());
        detector.keyExclude().addAll(settings.keyExcludeSet());
        return detector;
    }

    private void finish(final String task_group_id,
                        final String task_id,
                        final AbstractGrabber crawler,
                        final Set<URL> url_list) {
        if (!_finished) {
            if (null != crawler) {
                _crawlers.remove(crawler.uuid());
            }
            _finished = _crawlers.isEmpty();

            this.onTaskFinished(task_group_id, task_id, url_list);
        }
    }
}
