package org.lyj.ext.netty.client.web;


import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.IHttpConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class HttpClientRequest {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    private final Map<String, String> _headers;
    private final Map<String, String> _cookies;

    private HttpRequest _request;
    private boolean _initialized;
    private URI _uri;
    private String _method = IHttpConstants.METHOD_GET;
    private boolean _gzip;
    private boolean _keep_alive;

    private String _content_file_name;
    private String _content_file_path;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClientRequest() {
        _initialized = false;
        _keep_alive = false;
        _gzip = true;

        _content_file_name = "";

        _headers = new HashMap<>();
        _cookies = new HashMap<>();
    }

    public HttpClientRequest(final String uri) throws URISyntaxException {
        this(new URI(uri));
    }

    public HttpClientRequest(final URL url) throws URISyntaxException {
        this(url.toURI());
    }

    public HttpClientRequest(final URI uri) {
        this();
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Map<String, String> headers() {
        return _headers;
    }

    public Map<String, String> cookies() {
        return _headers;
    }

    public String method() {
        return _method;
    }

    public HttpClientRequest method(final String method) {
        _method = method;
        return this;
    }

    public URI uri() {
        return _uri;
    }

    public HttpClientRequest uri(final String uri) throws URISyntaxException {
        return this.uri(new URI(uri));
    }

    public HttpClientRequest uri(final URL url) throws URISyntaxException {
        return this.uri(url.toURI());
    }

    public HttpClientRequest uri(final URI uri) {
        _uri = uri;
        return this;
    }

    public String scheme() {
        if (null != _uri) {
            return _uri.getScheme();
        }
        return "";
    }

    public boolean isSsl() {
        return this.scheme().equalsIgnoreCase("https");
    }

    public int port() {
        if (null != _uri) {
            int port = _uri.getPort();
            if (port == -1) {
                if (!this.isSsl()) {
                    return 80;
                } else {
                    return 443;
                }
            }
        }
        return 80;
    }

    public String host() {
        if (null != _uri) {
            return StringUtils.hasText(_uri.getHost()) ? _uri.getHost() : "127.0.0.1";
        }
        return "127.0.0.1";
    }

    public String rawPath() {
        if (null != _uri) {
            return _uri.getRawPath();
        }
        return "";
    }

    public HttpRequest nativeHttpRequest() {
        this.init();
        return _request;
    }

    /**
     * Content temp file name. Default value is empty string.
     * If assigned the content bytes are saved in a temp file.
     *
     * @param name file name. ex: "file.dat"
     * @return
     */
    public HttpClientRequest contentFile(final String name) {
        _content_file_name = name;
        if (StringUtils.hasText(name)) {
            _content_file_path = PathUtils.getTemporaryFile(name);
        } else {
            _content_file_path = "";
        }
        return this;
    }

    public String contentFile() {
        return _content_file_path;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!_initialized) {
            _request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, this.getHttpMethod(), this.rawPath());
            _request.headers().set(HttpHeaderNames.HOST, this.host());

            // add headers
            if (_keep_alive) {
                _headers.put(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.CLOSE.toString());
            }
            if (_gzip) {
                _headers.put(HttpHeaderNames.ACCEPT_ENCODING.toString(), HttpHeaderValues.GZIP.toString());
            }

            final Set<String> header_names = _headers.keySet();
            for (final String name : header_names) {
                if (!_request.headers().contains(name)) {
                    _request.headers().add(name, _headers.get(name));
                }
            }

            if (!_cookies.isEmpty()) {
                final List<Cookie> data = new ArrayList<>();
                final Set<String> cookie_names = _cookies.keySet();
                for (final String name : cookie_names) {
                    data.add(new io.netty.handler.codec.http.cookie.DefaultCookie(name, _cookies.get(name)));
                }
                _request.headers().set(HttpHeaderNames.COOKIE,
                        io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(data));
            }
        }
    }

    private HttpMethod getHttpMethod() {
        if (IHttpConstants.METHOD_GET.equalsIgnoreCase(method())) {
            return HttpMethod.GET;
        } else if (IHttpConstants.METHOD_POST.equalsIgnoreCase(method())) {
            return HttpMethod.POST;
        } else if (IHttpConstants.METHOD_DELETE.equalsIgnoreCase(method())) {
            return HttpMethod.DELETE;
        } else if (IHttpConstants.METHOD_PUT.equalsIgnoreCase(method())) {
            return HttpMethod.PUT;
        } else if (IHttpConstants.METHOD_OPTIONS.equalsIgnoreCase(method())) {
            return HttpMethod.OPTIONS;
        } else if (IHttpConstants.METHOD_CONNECT.equalsIgnoreCase(method())) {
            return HttpMethod.CONNECT;
        } else if (IHttpConstants.METHOD_TRACE.equalsIgnoreCase(method())) {
            return HttpMethod.TRACE;
        }
        return HttpMethod.GET;
    }

}
