package org.lyj.ext.html.web.webcrawler;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.FixedBlockingPool;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.html.web.webcrawler.elements.WebCrawlerDocument;
import org.lyj.ext.html.web.webcrawler.elements.WebCrawlerPathList;
import org.lyj.ext.html.web.webcrawler.elements.WebCrawlerSettings;
import org.lyj.ext.html.web.webcrawler.exceptions.MissingUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main crawler controller
 */
public class WebCrawler {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Counter _loop_count;
    private final FixedBlockingPool _pool;
    private final WebCrawlerPathList _visited_paths;
    private final Set<String> _visited_documents;
    private final WebCrawlerSettings _settings;

    private Delegates.Callback<WebCrawlerDocument> _callback;
    private Delegates.CallbackEntry<URL, Throwable> _callback_error;
    private Delegates.Callback<WebCrawlerPathList> _callback_finish;

    private boolean _started;
    private boolean _finished;
    private int _active_task_count;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebCrawler() {
        _settings = new WebCrawlerSettings();

        _visited_paths = new WebCrawlerPathList();
        _visited_documents = new HashSet<>();

        _pool = new FixedBlockingPool();
        _pool.capacity(20); // 20 threads
        _pool.corePoolSize(1);
        _pool.maximumPoolSize(10);

        _finished = false;
        _started = false;
        _loop_count = new Counter(0);


        this.monitor();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public WebCrawlerSettings settings() {
        return _settings;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public WebCrawlerPathList visited() {
        return _visited_paths;
    }

    public boolean reachedLimit() {
        final int link_limit = this.settings().linkLimit();
        if (link_limit > -1) {
            return _visited_paths.size() > link_limit;
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

    public WebCrawler onFinish(final Delegates.Callback<WebCrawlerPathList> callback) {
        _callback_finish = callback;
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
                if (!_visited_documents.contains(key)) {
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

    private boolean isExcludedPath(final String url) {
        final List<String> param_exclude = JsonWrapper.toListOfString(_settings.pageExclude());
        return PathUtils.pathMatchOne(url, param_exclude);
    }

    private void run(final URL url) {
        _pool.start(() -> {

            _started = true; // START FLAG

            try {
                // start crawler
                final WebCrawlerDocument document = new WebCrawlerDocument(_settings.document(), url);

                // callback
                this.invoke(document);

                // get document links and
                document.urlLinks().forEach((link) -> {
                    try {
                        if (!this.isExcludedPath(link) &&
                                this.canParse(link) &&
                                !_visited_paths.contains(new URL(link))) {
                            this.start(link);
                        }
                    } catch (Throwable ignored) {
                    }
                });

                if (this.settings().allowExternalLinks()) {
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
                if (_loop_count.value() > this.settings().loopDetectionThreashold()) {
                    _finished = true;
                }
            }
            return false;
        }
    }

    private void close() {
        _finished = true;
        _started = false;
        _visited_documents.clear();
        if (null != _callback_finish) {
            _callback_finish.handle(_visited_paths);
            _callback_finish = null;
        }
    }

}
