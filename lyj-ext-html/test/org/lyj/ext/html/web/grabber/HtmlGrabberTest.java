package org.lyj.ext.html.web.grabber;

import org.junit.Test;
import org.lyj.commons.async.AsyncUtils;
import org.lyj.ext.html.IHtmlConstants;
import org.lyj.ext.html.web.grabber.html.HtmlGrabber;
import org.lyj.ext.html.web.grabber.rss.Rss2Grabber;

import java.net.URL;

/**
 * Created by angelogeminiani on 04/03/17.
 */
public class HtmlGrabberTest {

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Test
    public void testHTML() throws Exception {

        HtmlGrabber crawler = new HtmlGrabber();

        crawler.settings().linkLimit(-1);
        // indexing
        crawler.settings().pageExclude().put("http://blog.oknoplast.it");
        crawler.settings().pageExclude().put("http://blog.oknoplast.it/author/oknoadm/page/*");

        crawler.onResult(this::result);
        crawler.onFinish(this::finish);

        crawler.start(new URL("http://blog.oknoplast.it"));

        crawler.join();

        System.out.println(AsyncUtils.reportActiveThreads());

        Thread.sleep(500);

    }

    @Test
    public void testRSS() throws Exception {

        Rss2Grabber crawler = (Rss2Grabber) GrabberFactory.create(IHtmlConstants.TYPE_RSS_2);

        crawler.settings().linkLimit(-1);

        crawler.onResult(this::result);
        crawler.onFinish(this::finish);
        crawler.onError(this::error);

        crawler.startAsync(new URL("http://blog.oknoplast.it/feed"));

        crawler.join();

        System.out.println(AsyncUtils.reportActiveThreads());

        Thread.sleep(500);

    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void finish(final AbstractGrabber grabber) {
        System.out.println("FINISH: processed url=" + grabber.urls().size() + ", documents=" + grabber.documents().size());
    }

    private void result(final DocItem docItem) {
        System.out.println(docItem.title() + "\n" + docItem.url());
    }

    private void error(URL url, Throwable error) {
        System.out.println("ERROR: " + error.toString() + " url=" + url.toString());
    }

}