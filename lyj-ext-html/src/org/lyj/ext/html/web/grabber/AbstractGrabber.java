package org.lyj.ext.html.web.grabber;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.FixedBlockingPool;
import org.lyj.commons.util.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public abstract class AbstractGrabber<T> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int THREAD_COUNT = 5;
    private static final long PAUSE_TIME = 1000;

    private static final int MAX_DEPTH = 1000;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _uuid;

    private final Set<URL> _master_list;
    private final List<Future<T>> _futures;
    //private ExecutorService _pool = Executors.newFixedThreadPool(THREAD_COUNT);
    private final FixedBlockingPool _pool;
    private final Set<DocItem> _documents;

    private final CrawlerSettings _settings;
    private final int _max_depth;
    private final int _max_url;

    private String _url_base;
    private boolean _finished;


    private Delegates.Callback<DocItem> _callback_document;
    private Delegates.Callback<AbstractGrabber<T>> _callback_finish;
    private Delegates.CallbackEntry<URL, Throwable> _callback_error;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractGrabber() {
        this(new CrawlerSettings());
    }

    public AbstractGrabber(final CrawlerSettings settings) {
        _uuid = RandomUtils.randomUUID();

        _settings = settings;
        _max_depth = MAX_DEPTH;
        _max_url = settings.linkLimit();

        _master_list = new HashSet<>();
        _futures = new ArrayList<>();
        _pool = GrabberTaskPool.instance();
        _documents = new HashSet<>();

        _finished = true; // not started yet
    }

    protected abstract Callable<T> buildTask(final URL url, final int depth) throws Exception;

    protected abstract boolean iterateFutureTasks() throws InterruptedException;

    protected abstract void startURL(final URL startURL);

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String uuid() {
        return _uuid;
    }

    public CrawlerSettings settings() {
        return _settings;
    }

    public String urlBase() {
        return _url_base;
    }

    public Set<URL> urls() {
        return _master_list;
    }

    public Set<DocItem> documents() {
        return _documents;
    }

    public boolean finished() {
        return _finished;
    }

    // ------------------------------------------------------------------------
    //                      h a n d l e r s
    // ------------------------------------------------------------------------

    public AbstractGrabber onResult(final Delegates.Callback<DocItem> callback) {
        _callback_document = callback;
        return this;
    }

    public AbstractGrabber onFinish(final Delegates.Callback<AbstractGrabber<T>> callback) {
        _callback_finish = callback;
        return this;
    }

    public AbstractGrabber onError(final Delegates.CallbackEntry<URL, Throwable> callback) {
        _callback_error = callback;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void start(final String start) throws IOException, InterruptedException {
        this.start(new URL(start));
    }

    public void start(final URL startURL) throws IOException, InterruptedException {

        _finished = false;

        // stay within same site
        _url_base = startURL.toString().replaceAll("(.*//.*/).*", "$1");


        this.startURL(startURL); // invoke abstract, this.processURL(startURL, 0);

        // wait finish grab
        while (true) {
            Thread.sleep(PAUSE_TIME);
            // invoke abstract process of futures tasks
            if (!this.iterateFutureTasks()) {
                break;
            }
        }

        // END
        this.doFinish();
    }

    public void startAsync(final String start) throws MalformedURLException {
        this.startAsync(new URL(start));
    }

    public void startAsync(final URL startURL) {
        _finished = false;
        Async.invoke((args) -> {
            try {
                this.start(startURL);
            } catch (Throwable t) {
                this.doError(startURL, t);
            }
        });
    }

    public void stop() {
        if (!_finished && !_pool.isTerminated()) {
            _pool.stop(true);
        }
    }

    public void join() {
        try {
            while (!_finished) {
                Thread.sleep(1000);
            }
        } catch (Throwable ignored) {

        }
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected boolean processURL(final URL url,
                                 final int depth) {
        if (this.shouldVisit(url, depth)) {
            _master_list.add(url);

            try {
                final Callable<T> task = this.buildTask(url, depth); // BUILD THE FUTURE TASK
                if (null != task) {
                    final Future<T> future = _pool.submit(task);
                    _futures.add(future);
                }

                return true;
            } catch (Throwable ignored) {
                // error building the task
            }
        }
        return false;
    }

    protected Set<URL> clearURLs(final Set<URL> urls) {
        final Set<URL> response = new HashSet<>();
        for (final URL url : urls) {
            response.add(this.clearURL(url));
        }
        return response;
    }

    protected URL clearURL(final URL url) {
        if (url.toString().contains("#")) {
            try {
                return new URL(StringUtils.substringBefore(url.toString(), "#"));
            } catch (MalformedURLException e) {
            }
        }
        return url;
    }

    protected List<Future<T>> futures() {
        return _futures;
    }

    protected boolean shouldVisit(final URL url,
                                  final int depth) {
        if (_master_list.contains(url)) {
            return false;
        }

        if (url.toString().endsWith(".pdf")) {
            return false;
        }
        if (depth > _max_depth) {
            return false;
        }
        if (_max_url > 0 && _master_list.size() >= _max_url) {
            return false;
        }
        if (!this.canParse(url.toString())) {
            return false;
        }
        if (!PathUtils.pathMatch(url.toString(), _url_base) && this.isExcludedPath(url.toString())) {
            return false;
        }
        if (!settings().allowExternalLinks()) {
            if (!this.matchDomain(url.toString())) {
                return false;
            }
            /*
            if (!url.toString().startsWith(_url_base)) {
                return false;
            }*/
        }


        return true;
    }

    //-- handlers invocation --//

    protected void doResult(final DocItem doc) {
        _documents.add(doc);
        if (null != _callback_document) {
            _callback_document.handle(doc);
        }
    }

    protected void doFinish() {
        _finished = true;
        if (null != _callback_finish) {
            _callback_finish.handle(this);
        }
    }

    protected void doError(final URL url, final Throwable error) {
        if (null != _callback_error) {
            _callback_error.handle(url, error);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

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

    private boolean matchDomain(final String url) {
        try {
            return this.matchDomain(new URL(url));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean matchDomain(final URL url) throws MalformedURLException {
        return this.getDomain(url).equalsIgnoreCase(this.getDomain(new URL(_url_base)));
    }

    private String getDomain(final URL url) {
        final String host = url.getHost().toLowerCase();
        return host.replace("www.", "");
    }
}