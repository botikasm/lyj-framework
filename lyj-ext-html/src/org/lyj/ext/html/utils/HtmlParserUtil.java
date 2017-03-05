package org.lyj.ext.html.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.html.web.grabber.IWebCrawlerConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class HtmlParserUtil {

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static String getText(final Document doc,
                                 final int autodetectContentThreashold) {
        StringBuilder sb = new StringBuilder();
        getText(doc.select("p"), sb, autodetectContentThreashold);
        if (sb.length() < autodetectContentThreashold) {
            sb = new StringBuilder();
            getText(doc.select("div"), sb, autodetectContentThreashold);
        }
        return sb.toString();
    }

    public static Set<URL> getLinks(final Document doc,
                                    final URL base_url) {
        final Set<URL> response = new HashSet<>();
        final Elements links = doc.select("a[href]");
        for (Element link : links) {
            final String href = link.attr("href");
            if (!StringUtils.hasText(href)
                    || href.startsWith("#")) {
                continue;
            }

            try {
                final URL nextUrl = null != base_url ? new URL(base_url, href) : new URL(href);
                response.add(nextUrl);
            } catch (MalformedURLException ignored) { // ignore bad urls
            }
        }
        return response;
    }

    public static Set<URL> getImages(final Document doc,
                                    final URL base_url) {
        final Set<URL> response = new HashSet<>();
        final Elements links = doc.select("img[src]");
        for (Element link : links) {
            final String src = link.attr("abs:src");
            if (!StringUtils.hasText(src)) {
                continue;
            }

            try {
                final URL nextUrl = null != base_url ? new URL(base_url, src) : new URL(src);
                response.add(nextUrl);
            } catch (MalformedURLException ignored) { // ignore bad urls
            }
        }
        return response;
    }

    public static String getTitle(final Document doc) {
        final Map<String, Set<String>> titles = getTitles(doc);
        if(!titles.isEmpty()){
            for(final String tag:IWebCrawlerConstants.HTML_TITLE_TAGS){
                if(titles.containsKey(tag) && !titles.get(tag).isEmpty()){
                    return titles.get(tag).iterator().next();
                }
            }
        }
        return doc.title();
    }

    public static Map<String, Set<String>> getTitles(final Document doc) {
        final Map<String, Set<String>> titles = new HashMap<>();
        for (final String tag : IWebCrawlerConstants.HTML_TITLE_TAGS) {
            addTitles(titles, tag, doc.select(tag));
        }
        return titles;
    }

    public static Microdata getMicrodata(final Document doc){
        final Microdata response = new Microdata();
        final Map<String, String> microdata = new HashMap<>();
        final Elements elements = doc.select("meta");
        for (final Element element : elements) {
            final String key = element.attributes().hasKey("name") ? element.attr("name") : element.attr("property");

            if (StringUtils.hasText(key)) {
                final String content = element.attr("content");
                if (StringUtils.hasText(content)) {
                    microdata.put(key, content);
                }
            }
        }

        response.title = microdata.get("og:title");
        if (!StringUtils.hasText(response.title)) {
            response.title = microdata.get("twitter:title");
        }
        response.description = microdata.get("og:description");
        if (!StringUtils.hasText(response.title)) {
            response.description = microdata.get("twitter:description");
        }
        response.image = microdata.get("og:image");
        if (!StringUtils.hasText(response.title)) {
            response.image = microdata.get("twitter:image");
        }

        response.site = microdata.get("og:site_name");
        if (!StringUtils.hasText(response.title)) {
            response.site = microdata.get("twitter:site");
        }

        // adjust title
        if (StringUtils.hasText(response.title) && StringUtils.hasText(response.site)) {
            if (response.title.endsWith(response.site)) {
                response.title = response.title.replace(response.site, "").replace("-", "").trim();
            }
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    private static void getText(final Elements elements,
                                final StringBuilder content,
                                final int autodetectContentThreashold) {
        if (null != elements && !elements.isEmpty()) {
            for (final Element element : elements) {
                final String text = element.text();
                if (text.length() > autodetectContentThreashold) {
                    content.append(" ");
                    content.append(text);
                }
            }
        }
    }

    private static void addTitles(final Map<String, Set<String>> map,
                                  final String tag,
                                  final Elements elements) {
        if (!elements.isEmpty()) {
            // initialize title set
            if (!map.containsKey(tag)) {
                map.put(tag, new HashSet<>());
            }

            // loop on elements
            for (final Element element : elements) {
                final String text = element.text();
                if (StringUtils.hasText(text)) {
                    map.get(tag).add(text);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class Microdata {

         public String title;
         public String description;
         public String image;
         public String site;

    }

}
