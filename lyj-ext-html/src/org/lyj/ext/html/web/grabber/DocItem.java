package org.lyj.ext.html.web.grabber;

import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.DateWrapper;
import org.lyj.commons.util.URLWrapper;

import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class DocItem {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final URL _base_url;
    private final int _depth;

    private final Set<URL> _url_list;
    private final Set<String> _keywords;

    private String _title;
    private String _image;
    private String _description;
    private Date _date;

    private String _html;       // html
    private String _text;       // html to text
    private String _content;    // autodetected text

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DocItem(final URL url, final int depth) {
        _base_url = url;
        _depth = depth;

        _url_list = new HashSet<>();
        _keywords = new HashSet<>();

        _date = DateUtils.now();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public URL url() {
        return _base_url;
    }

    public String urlNoHash() {
        return new URLWrapper(_base_url.toString()).hash("").toString();
    }

    public int depth() {
        return _depth;
    }

    public Set<URL> links() {
        return _url_list;
    }

    public Set<String> keywords() {
        return _keywords;
    }

    public String title() {
        return _title;
    }

    public void title(final String value) {
        _title = value;
    }

    public String description() {
        return _description;
    }

    public void description(final String value) {
        _description = value;
    }

    public String image() {
        return _image;
    }

    public void image(final String value) {
        _image = value;
    }

    public Date date() {
        return _date;
    }

    public void date(final Date value) {
        _date = value;
    }

    public void date(final String value) {
        try{
            final Date date = DateWrapper.parse(value).getDateTime();
            this.date(date);
        } catch(Throwable ignored){
        }
    }

    public String html() {
        return _html;
    }

    public void html(final String value) {
        _html = value;
    }

    public String text() {
        return _text;
    }

    public void text(final String value) {
        _text = value;
    }

    public String content() {
        return _content;
    }

    public void content(final String value) {
        _content = value;
    }

}
