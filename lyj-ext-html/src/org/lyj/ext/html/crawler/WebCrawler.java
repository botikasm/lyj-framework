package org.lyj.ext.html.crawler;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.FixedBlockingPool;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.html.crawler.documents.WebCrawlerDocument;
import org.lyj.ext.html.crawler.exceptions.MissingUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Main crawler controller
 */
public class WebCrawler {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int MAX_LOOP = 3000;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Counter _loop_count;
    private final FixedBlockingPool _pool;
    private final WebCrawlerPathList _visited_paths;
    private final Set<String> _visited_documents;
    private Delegates.Callback<WebCrawlerDocument> _callback;
    private Delegates.CallbackEntry<URL, Throwable> _callback_error;

    private boolean _started;
    private boolean _finished;
    private int _active_task_count;

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    private boolean _navigate_external;
    private int _link_limits;
    private int _loop_detection_threashold;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebCrawler() {
        _visited_paths = new WebCrawlerPathList();
        _visited_documents = new HashSet<>();

        _pool = new FixedBlockingPool();
        _pool.capacity(20); // 20 threads
        _pool.corePoolSize(1);
        _pool.maximumPoolSize(10);

        _finished = false;
        _started = false;
        _loop_count = new Counter(0);

        _navigate_external = false; // no external links allowed
        _link_limits = -1; // not limited the number of navigated links
        _loop_detection_threashold = MAX_LOOP; // loop max

        this.monitor();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    /**
     * Allow/Disallow external link navigation
     */
    public boolean navigateExternal() {
        return _navigate_external;
    }

    /**
     * Allow/Disallow external link navigation
     */
    public WebCrawler navigateExternal(final boolean value) {
        _navigate_external = value;
        return this;
    }

    /**
     * Max number of loops on same page.
     * Once reached this limit the crawler stop working.
     */
    public int loopDetectionThreashold() {
        return _loop_detection_threashold;
    }

    /**
     * Set Max number of loops on same page.
     * Once reached this limit the crawler stop working.
     */
    public WebCrawler loopDetectionThreashold(final int value) {
        _loop_detection_threashold = value;
        return this;
    }

    /**
     * Max number of links to navigate
     * (DEFAULT no limits = -1)
     */
    public int linkLimits() {
        return _link_limits;
    }

    /**
     * Set Max number of links to navigate
     * (DEFAULT no limits = -1)
     */
    public WebCrawler linkLimits(final int value) {
        _link_limits = value;
        return this;
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public WebCrawlerPathList visited() {
        return _visited_paths;
    }

    public boolean reachedLimit() {
        if (_link_limits > -1) {
            return _visited_paths.size() < _link_limits;
        }
        return false;
    }

    public boolean finished() {
        return _finished;
    }

    public WebCrawler start(final String url) throws MalformedURLException, MissingUrlException, InterruptedException {
        if (StringUtils.hasText(url)) {
            this.start(new URL(url));
        } else {
            throw new MissingUrlException();
        }
        return this;
    }

    public WebCrawler start(final URL url) throws MissingUrlException, InterruptedException {
        // synchronized (_loop_count) {
        if (null != url) {
            if (!_finished) {
                if (this.allowScan(url)) {
                    this.run(url);
                }
            } else {
                // already finished
                _pool.stop(true);
            }
        } else {
            throw new MissingUrlException();
        }

        return this;
        //}
    }

    public void stop() {
        if (!_pool.isTerminated()) {
            _pool.stop(true);
        }
        this.close();
    }

    public void join(final long timeout) throws InterruptedException {
        if (!_pool.isTerminated()) {
            _pool.join(timeout);
        }
        this.close();
    }

    public void join() throws InterruptedException {
        try {
            final Task<Void> task = new Task<>((t) -> {
                Async.loop(() -> {
                    try {
                        if (_finished) {
                            t.success(null);
                            return true;
                        }
                        return false;
                    } catch (Throwable err) {
                        t.fail(err);
                        return true;
                    }
                });
            });
            task.run().get();
        } catch (Throwable t) {
            throw new InterruptedException(t.toString());
        }
        this.close();
    }

    public WebCrawler onResult(final Delegates.Callback<WebCrawlerDocument> callback) {
        _callback = callback;
        return this;
    }

    public WebCrawler onError(final Delegates.CallbackEntry<URL, Throwable> callback) {
        _callback_error = callback;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void error(final URL url, final Throwable error) {
        if (null != _callback_error) {
            _callback_error.handle(url, error);
        }
    }

    private void invoke(final WebCrawlerDocument document) {
        synchronized (_visited_documents) {
            if (null != _callback) {
                final String key = document.id();
                if(!_visited_documents.contains(key)){
                    _visited_documents.add(key);
                    _callback.handle(document);
                } else {
                    // already exists
                }
            }
        }
    }

    private boolean canParse(final String url) {
        try {
            final String ext = PathUtils.getFilenameExtension(url, false).toLowerCase();
            return StringUtils.hasText(ext) && !CollectionUtils.contains(IWebCrawlerConstants.NO_PARSE, ext);
        } catch (Throwable ignored) {
            // no extension
            return true;
        }
    }

    private void run(final URL url) {
        _pool.start(() -> {

            _started = true; // START FLAG

            try {
                // start crawler
                final WebCrawlerDocument document = new WebCrawlerDocument(url);

                // callback
                this.invoke(document);

                // get document links and
                document.urlLinks().forEach((link) -> {
                    try {
                        if (this.canParse(link) && !_visited_paths.contains(new URL(link))) {
                            this.start(link);
                        }
                    } catch (Throwable ignored) {
                    }
                });

                if (_navigate_external) {
                    document.urlLinksExternal().forEach((link) -> {
                        try {
                            if (this.canParse(link) && !_visited_paths.contains(new URL(link))) {
                                this.start(link);
                            }
                        } catch (Throwable ignored) {
                        }
                    });
                }
            } catch (Throwable t) {
                this.error(url, t);
            }
        });
    }

    private void monitor() {
        _pool.monitor((monitor) -> {
            _active_task_count = monitor.activeCount();
            if (_started && _active_task_count == 0) {
                this.close();
            }
        });
    }

    private boolean allowScan(final URL url) {
        synchronized (_visited_paths) {
            if (!_visited_paths.contains(url)) {
                if (!this.reachedLimit()) {
                    _visited_paths.add(url);
                    // reset loop counter
                    _loop_count.reset();

                    return true;
                } else {
                    _finished = true;
                    _pool.stop(true);
                }
            } else {
                // path already exists.
                // may be its a recursive loop
                _loop_count.inc();
                if (_loop_count.value() > _loop_detection_threashold) {
                    _finished = true;
                }
            }
            return false;
        }
    }

    private void close(){
        _finished = true;
        _started = false;
        _visited_documents.clear();
    }

}
