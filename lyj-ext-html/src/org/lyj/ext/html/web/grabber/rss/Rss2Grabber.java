package org.lyj.ext.html.web.grabber.rss;

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.html.web.grabber.AbstractGrabber;
import org.lyj.ext.html.web.grabber.CrawlerSettings;
import org.lyj.ext.html.web.grabber.DocItem;
import org.lyj.ext.html.web.webindexer.WebIndexerSettings;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Rss2Grabber
        extends AbstractGrabber<List<DocItem>> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String RSS_PAGING_NONE = WebIndexerSettings.RSS_PAGING_NONE;
    public static final String RSS_PAGING_PAGED = WebIndexerSettings.RSS_PAGING_PAGED;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Rss2Grabber() {
        super();
    }

    public Rss2Grabber(final CrawlerSettings settings) {
        super(settings);
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    @Override
    protected void startURL(final URL startURL) {
        //-- loop on all avalilable feed pages --//
        this.loopRssPages(startURL, 0);
    }

    @Override
    protected Callable<List<DocItem>> buildTask(final URL url,
                                                final int depth) throws Exception {
        return new Rss2GrabberTask(super.settings(), url, depth);
    }

    @Override
    protected boolean iterateFutureTasks() throws InterruptedException {

        final Set<DocItem> pageSet = new HashSet<>();
        final Iterator<Future<List<DocItem>>> iterator = super.futures().iterator();

        while (iterator.hasNext()) {
            final Future<List<DocItem>> future = iterator.next();
            if (future.isDone()) {
                iterator.remove();
                try {
                    final List<DocItem> docs = future.get();
                    for (final DocItem doc : docs) {
                        pageSet.add(doc);
                        this.doResult(doc);
                    }
                } catch (InterruptedException e) {  // skip pages that load too slow
                } catch (ExecutionException e) {
                }
            }
        }

        return (super.futures().size() > 0);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void loopRssPages(final URL url, final int page_count) {

        // submit url to create future task
        if (super.processURL(url, page_count)) {
            final String page_param = this.getPagingParam();
            if (StringUtils.hasText(page_param)) {
                // NEXT PAGE
                try {
                    final String query = url.getQuery();
                    if (StringUtils.hasText(query)) {
                        final String count = CollectionUtils.getLast(StringUtils.split(query, "="));
                        final int count_n = ConversionUtils.toInteger(count, -1);
                        if (count_n > 1) {
                            final URL next = new URL(url.toString().replace(query, "") + page_param + "=" + (count_n + 1));
                            this.loopRssPages(next, count_n); // RECURSIVE
                        }
                    } else {
                        // no query, this is first page
                        final URL next = new URL(url.toString() + "?" + page_param + "=2");
                        this.loopRssPages(next, 1); // RECURSIVE
                    }
                } catch (Throwable t) {

                }
            }
        }
    }

    private String getPagingParam() {
        final String paging_mode = super.settings().pagingMode();
        final String page_param = StringUtils.hasText(paging_mode) ? paging_mode : WebIndexerSettings.RSS_PAGING_PAGED;
        return page_param.equalsIgnoreCase(WebIndexerSettings.RSS_PAGING_NONE) ? "" : page_param;
    }

}