package org.lyj.ext.html.web.webcrawler.elements;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Visited paths
 */
public class WebCrawlerPathList {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Set<String> _visited_paths;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebCrawlerPathList() {
        _visited_paths = new HashSet<>();
    }

    @Override
    public String toString() {
        return _visited_paths.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int size() {
        return _visited_paths.size();
    }

    public boolean contains(final URL url) {
        final String surl = url.toString();
        return _visited_paths.contains(surl);
    }

    public void add(final URL url) {
        final String surl = url.toString();
        _visited_paths.add(surl);
    }

}


