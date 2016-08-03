package org.lyj.ext.netty.client.web;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import org.json.JSONObject;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.MimeTypeUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.HttpHeader;
import org.lyj.ext.netty.server.web.IHttpConstants;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class HttpClientRequest {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    private final HttpHeader _headers;
    private final Map<String, String> _cookies;
    private final List<File> _file_uploads;

    private boolean _initialized;
    private URI _uri;
    private String _encoding;
    private String _method = IHttpConstants.METHOD_GET;
    private boolean _gzip;
    private boolean _keep_alive;
    private boolean _multipart;
    private Object _body;

    private String _content_file_name;
    private String _content_file_path;

    //--LATE INITIALIZED--//
    private HttpRequest _request;
    private HttpDataFactory _factory;
    private HttpPostRequestEncoder _request_encoder;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClientRequest(final String encoding) {
        _initialized = false;
        _keep_alive = false;
        _multipart = false;
        _gzip = true;
        _encoding = StringUtils.hasText(encoding) ? encoding : CharEncoding.UTF_8;

        _content_file_name = "";

        _headers = new HttpHeader();
        _cookies = new HashMap<>();
        _file_uploads = new ArrayList<>();
    }

    public HttpClientRequest(final String encoding, final String uri) throws URISyntaxException {
        this(encoding, new URI(uri));
    }

    public HttpClientRequest(final String encoding, final URL url) throws URISyntaxException {
        this(encoding, url.toURI());
    }

    public HttpClientRequest(final String encoding, final URI uri) {
        this(encoding);
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpHeader headers() {
        return _headers;
    }

    public Map<String, String> cookies() {
        return _cookies;
    }

    public String encoding() {
        return _encoding;
    }

    public HttpClientRequest encoding(final String value) {
        _encoding = value;
        return this;
    }

    public String method() {
        return _method;
    }

    public HttpClientRequest method(final String value) {
        _method = value;
        return this;
    }

    public boolean multipart() {
        return _multipart;
    }

    public HttpClientRequest multipart(final boolean value) {
        _multipart = value;
        return this;
    }

    public File[] uploads() {
        return _file_uploads.toArray(new File[_file_uploads.size()]);
    }

    public HttpClientRequest upload(final File value) {
        _file_uploads.add(value);
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

    public String rawPath(final boolean include_query) {
        if (null != _uri) {
            return _uri.getRawPath() + (include_query&&StringUtils.hasText(_uri.getQuery()) ? "?" + _uri.getQuery() : "");
        }
        return "";
    }

    /**
     * Content temp file name. Default value is empty string.
     * If assigned the content bytes are saved in a temp file.
     * This options is good for very large files o big responses.
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

    public Object body() {
        return _body;
    }

    public HttpClientRequest body(final Object value) {
        _body = value;
        return this;
    }

    public void send(final Bootstrap b) throws Exception {
        if (this.init()) {
            // Make the connection attempt.
            final Channel channel = b.connect(this.host(), this.port()).sync().channel();

            if (null != _request_encoder) {
                // send request
                channel.write(_request);

                // test if request was chunked and if so, finish the write
                // could do either request.isChunked()
                if (_request_encoder.isChunked()) {
                    // either do it through ChunkedWriteHandler
                    channel.write(_request_encoder);
                }
                //it is clearly recommended to clean all files after each request
                _request_encoder.cleanFiles();

                channel.flush();

                // clean all data
                _factory.cleanAllHttpData();

            } else {
                channel.writeAndFlush(_request);
            }

            channel.closeFuture().sync();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

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

    private boolean init() throws Exception {
        if (!_initialized) {

            _initialized = true;

            // add headers
            if (_keep_alive) {
                _headers.add(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.CLOSE.toString());
            }
            if (_gzip) {
                _headers.add(HttpHeaderNames.ACCEPT_ENCODING.toString(), HttpHeaderValues.GZIP.toString());
            }

            // create request
            this.initRequest();

            return true;
        }
        return false;
    }

    private void initRequest() throws HttpPostRequestEncoder.ErrorDataEncoderException {
        // create request
        _request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, this.getHttpMethod(), this.rawPath(true));

        if (null != _body && this.method().equals(IHttpConstants.METHOD_POST)) {

            if (_body instanceof JSONObject && _request instanceof FullHttpRequest) {
                // application/json
                final byte[] bytes = _body.toString().getBytes(CharEncoding.forName(_encoding));

                final ByteBuf bbuf = ((FullHttpRequest) _request).content().clear();
                bbuf.writeBytes(bytes);

                // headers
                _headers.add(IHttpConstants.CONTENT_TYPE, MimeTypeUtils.getMimeJson());
                _headers.add(IHttpConstants.CONTENT_LENGTH, bbuf.readableBytes() + "");

                // add headers on request. it is legal to add directly header or cookie into the request until finalize
                this.initRequestHeaders();
            } else {
                // application/x-www-form-urlencoded

                // setup the factory: here using a mixed memory/disk based on size threshold
                _factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if MINSIZE exceed
                _request_encoder = new HttpPostRequestEncoder(_factory, _request, _multipart);

                // body of request
                this.initRequestBody();

                // upload file/s
                this.initUploads();

                // add headers on request. it is legal to add directly header or cookie into the request until finalize
                this.initRequestHeaders();

                // finalize request, no more actions on request are legal
                try {
                    _request = _request_encoder.finalizeRequest();
                } catch (Throwable t) {
                    System.out.println(t);
                }
            }
        }
    }

    private void initRequestBody() {
        if (null != _body) {
            if (_body instanceof Map) {
                CollectionUtils.forEach((Map) _body, this::addBodyAttribute);
            } else if (_body instanceof JSONObject) {
                CollectionUtils.forEach((JSONObject) _body, this::addBodyAttribute);
            } else if (_body instanceof Collection) {
                for (final Object item : (Collection) _body) {
                    if (item instanceof Map.Entry) {
                        final Map.Entry<String, Object> entry = (Map.Entry) item;
                        this.addBodyAttribute(entry.getValue(), 0, entry.getKey());
                    }
                }
            }
        }
    }

    private void addBodyAttribute(final Object value, final int index, final Object key) {
        try {
            if (key instanceof String && null != value) {
                _request_encoder.addBodyAttribute((String) key, value.toString());
            }
        } catch (Throwable ignored) {
            // ignored error. attribute is not valid
        }
    }

    private void initUploads() throws HttpPostRequestEncoder.ErrorDataEncoderException {
        if (!_file_uploads.isEmpty()) {
            for (final File file : _file_uploads) {
                final String filename = file.getName();
                final String fileext = PathUtils.getFilenameExtension(filename, true);
                final String name = PathUtils.getFilename(filename, false);
                final String type = MimeTypeUtils.getMimeType(fileext);
                final boolean is_text = MimeTypeUtils.isTextType(filename);
                _request_encoder.addBodyFileUpload(name, file, type, is_text);
            }
        }
    }

    private void initRequestHeaders() {
        // add host
        _request.headers().set(HttpHeaderNames.HOST, this.host());

        // headers
        final Set<String> header_names = _headers.keys();
        for (final String name : header_names) {
            if (!_request.headers().contains(name)) {
                _request.headers().add(name, _headers.get(name));
            }
        }

        // cookies
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
