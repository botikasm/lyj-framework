package org.lyj.ext.html.web.grabber;

import org.lyj.ext.html.IHtmlConstants;

/**
 *
 */
public interface IWebCrawlerConstants
        extends IHtmlConstants{



    String[] NO_PARSE = new String[]{
            "jpg", "jpeg", "gif", "bmp", "wav",
            "ogv", "mp3", "pdf", "png"
    };

    String[] HTML_TITLE_TAGS = new String[]{
            "h1", "h2", "h3", "h4", "h5", "h6"
    };

}
