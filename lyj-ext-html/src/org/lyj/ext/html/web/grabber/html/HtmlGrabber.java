package org.lyj.ext.html.web.grabber.html;

import org.lyj.ext.html.web.grabber.CrawlerSettings;
import org.lyj.ext.html.web.grabber.AbstractGrabber;
import org.lyj.ext.html.web.grabber.DocItem;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HtmlGrabber
        extends AbstractGrabber<DocItem> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HtmlGrabber() {
        super();
    }

    public HtmlGrabber(final CrawlerSettings settings) {
        super(settings);
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    @Override
    protected void startURL(final URL startURL) {
        // submit url to create first future task
        super.processURL(startURL, 0);
    }

    @Override
    protected Callable<DocItem> buildTask(final URL url,
                                          final int depth) throws Exception{
        return new HtmlGrabberTask(settings(), url, depth);
    }

    protected boolean iterateFutureTasks() throws InterruptedException {

        final Set<DocItem> pageSet = new HashSet<>();
        final Iterator<Future<DocItem>> iterator = super.futures().iterator();

        while (iterator.hasNext()) {
            final Future<DocItem> future = iterator.next();
            if (future.isDone()) {
                iterator.remove();
                try {
                    final DocItem doc = future.get();
                    pageSet.add(doc);
                    this.doResult(doc);
                } catch (InterruptedException e) {  // skip pages that load too slow
                } catch (ExecutionException e) {
                }
            }
        }

        for (final DocItem page : pageSet) {
            this.addDocURLs(page);
        }

        return (super.futures().size() > 0); // return FALSE to exit
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void addDocURLs(final DocItem page) {
        final Set<URL> urls = page.links();
        for (URL url : urls) {
            super.processURL(super.clearURL(url), page.depth() + 1);
        }
    }


}