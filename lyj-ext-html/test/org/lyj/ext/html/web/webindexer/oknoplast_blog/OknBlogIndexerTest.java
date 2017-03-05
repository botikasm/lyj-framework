package org.lyj.ext.html.web.webindexer.oknoplast_blog;

import org.json.JSONArray;
import org.junit.Test;
import org.lyj.commons.network.URLUtils;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.ext.html.web.webindexer.TestSiteIndexer;

/**
 * Created by angelogeminiani on 22/02/17.
 */
public class OknBlogIndexerTest {

    @Test
    public void startIndexing() throws Exception {

        final String s_settings = ClassLoaderUtils.getResourceAsString(null, this.getClass(), "settings.json");
        final JSONArray settings = new JSONArray(s_settings);

        final TestSiteIndexer indexer = new TestSiteIndexer(settings);

        indexer.startIndexing();

        while (!indexer.isFinished()){
            Thread.sleep(3000);
        }
        System.out.println(indexer.countIndexedPages());
    }


    @Test
    public void getFile() throws Exception {
        final String feed = URLUtils.getUrlContent("http://blog.oknoplast.it");
        System.out.println(feed);
    }
}