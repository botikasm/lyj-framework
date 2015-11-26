/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ly.commons.network;

import org.ly.IConstants;
import org.ly.commons.lang.CharEncoding;
import org.ly.commons.util.ByteUtils;
import org.ly.commons.util.ClassLoaderUtils;
import org.ly.commons.util.FormatUtils;
import org.ly.commons.util.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * URL shortcut methods.
 */
public class URLUtils {

    private static final String CHARSET = CharEncoding.getDefault();

    public static final String TYPE_ALL = IConstants.TYPE_ALL;
    public static final String TYPE_JSON = IConstants.TYPE_JSON;
    public static final String TYPE_TEXT = IConstants.TYPE_TEXT;
    public static final String TYPE_HTML = IConstants.TYPE_HTML;

    private URLUtils() {
    }

    public static String getUrlContent(final String uri) {
        return getUrlContent(uri, 3000);
    }

    public static String getUrlContent(final String uri, final String contentType) {
        return getUrlContent(uri, 3000, contentType);
    }

    public static String getUrlContent(final String uri, final int timeout) {
        return getUrlContent(uri, timeout, TYPE_HTML);
    }

    public static String getUrlContent(final String uri, final int timeout, final String contentType) {
        try {
            final InputStream is = getInputStream(uri, timeout, contentType);
            try {
                final byte[] bytes = ByteUtils.getBytes(is);
                return new String(bytes, CHARSET);
            } finally {
                is.close();
            }
        } catch (IOException t) {
            // file not found or connection timeout
            final String resource;
            if (TYPE_HTML.equalsIgnoreCase(contentType)) {
                resource = "error.html";
            } else if (TYPE_JSON.equalsIgnoreCase(contentType)) {
                resource = "error.json";
            } else {
                resource = "error.txt";
            }
            final String text = ClassLoaderUtils.getResourceAsString(null, URLUtils.class, resource, CHARSET);
            final Map<String, String> args = new HashMap<String, String>();
            args.put("uri", StringEscapeUtils.escapeJavaScript(uri));
            args.put("error", StringEscapeUtils.escapeJavaScript(t.toString()));
            return FormatUtils.formatTemplate(text, "{{", "}}", args);
        }
    }

    public static InputStream getInputStream(final String uri) throws IOException {
        return getInputStream(uri, 3000);
    }

    public static InputStream getInputStream(final String uri, final String contentType) throws IOException {
        final URL url = new URL(uri.trim().replaceAll(" ", "+"));
        return getInputStream(url, 3000, contentType);
    }

    public static InputStream getInputStream(final String uri, final int timeout) throws IOException {
        final URL url = new URL(uri.trim().replaceAll(" ", "+"));
        return getInputStream(url, timeout, TYPE_HTML);
    }

    public static InputStream getInputStream(final String uri, final int timeout, final String contentType) throws IOException {
        final URL url = new URL(uri.trim().replaceAll(" ", "+"));
        return getInputStream(url, timeout, contentType);
    }

    public static InputStream getInputStream(final URL url, final int timeout, final String type) throws IOException {
        final Proxy proxy = NetworkUtils.getProxy();
        final URLConnection conn = url.openConnection(proxy);
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).setRequestMethod("GET");
        }
        conn.setConnectTimeout(timeout);
        conn.addRequestProperty("Accept", type);
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:23.0) Gecko/20100101 Firefox/23.0");
        conn.addRequestProperty("Connection", "keep-alive");
        conn.connect();

        return conn.getInputStream();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
