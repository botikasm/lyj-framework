package org.lyj.ext.html.web.webindexer;

import org.json.JSONArray;

import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class TestSiteIndexer
        extends WebIndexer {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public TestSiteIndexer(final JSONArray settings) {
        super(settings);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected synchronized void onError(final Throwable t) {
        System.out.println(t);
    }

    protected synchronized void onTaskError(final String task_id, final String url, final Throwable t) {
        System.out.println("ERROR: " + t.toString() + " " + url);
    }

    protected void onTaskStarted(final String task_id, final WebIndexerSettings setting) {
        System.out.println("STARTED: " + setting.toString());
    }

    protected void onTaskFinished(final String task_id, final Set<URL> paths) {
        System.out.println("FINISHED");
    }

    @Override
    protected void onTaskIndex(final String task_id,
                               final WebIndexerSettings site,
                               final Map<String, Double> keywords,
                               final String title, final String description,
                               final String image, final Date date,
                               final String url) {
        System.out.println("INDEXED: " + url);
        System.out.println("\tkeywords: " + keywords.keySet());
        System.out.println("\timage : " + image);
        System.out.println("\ttitle : " + title);
        System.out.println("\tdescription : " + description);
        System.out.println("\tdate : " + date);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
