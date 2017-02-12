package org.lyj.ext.html.crawler;

import org.junit.Test;

import java.util.Set;

/**
 * Created by angelogeminiani on 11/02/17.
 */
public class WebCrawlerTest {

    private static final String URL_1 = "http://www.gianangelogeminiani.me/";
    private static final String URL_2 = "http://gianangelogeminiani.me/web-30-quattro-chiacchiere-con-ralf#content";

    @Test
    public void start() throws Exception {
        final String root = URL_2;

        final WebCrawler crawler = new WebCrawler();
        crawler.onError((url, err) -> {
            System.out.println("Error: " + err.toString() + " Parsing url: " + url.toString());
        });
        crawler.onResult((document) -> {
            final Set<String> links = document.urlLinks();
            if(!document.containsPath("web-30-quattro-chiacchiere-con-ralf/")){
                System.out.println("PAGE: " + document.path() + " CONTENT: " + document.content().length() + " LINKS: " + links.size() + " TITLES: " + document.h1().size());
            } else {
                System.out.println("FOUND ROOT");
                System.out.println("PAGE: " + document.path() + " CONTENT: " + document.content().length() + " LINKS: " + links.size() + " TITLES: " + document.h1().size());
            }
        })
                .start(root);

        // wait until end
        crawler.join();

        System.out.println("FINISH: " + crawler.visited().size());
    }

}