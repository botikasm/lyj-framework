package org.lyj.ext.netty.server.web;

import io.netty.util.internal.SystemPropertyUtil;
import org.json.JSONObject;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

/**
 * Web Server Configuration Helper
 */
public class HttpServerConfig {


    private static final String[] INDEX_FILES = new String[]{"index.html", "index.htm"};

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _encoding;
    private int _max_chunk_size;
    private String _host;
    private int _port;
    private boolean _port_autodect;
    private int _port_detection_try;
    private String _root;
    private boolean _use_ssl;
    private boolean _use_compression;
    private int _cache_seconds;
    private String _cors_allow_origin; // CORS
    private String _cors_allow_methods; // CORS
    private String _cors_allow_headers; // CORS
    private String _not_found_404;
    private final List<String> _index_files;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServerConfig() {
        _encoding = CharEncoding.UTF_8;
        _max_chunk_size = 1048576; //65536;
        _port = 4000;
        _root = SystemPropertyUtil.get("user.dir");
        _use_ssl = false;
        _use_compression = true;
        _port_autodect = false;
        _port_detection_try = 100; // try 100 times to get a free port
        _cache_seconds = 60;
        _host = "localhost";
        _not_found_404 = "";

        _cors_allow_origin = ""; // empty=none, "*"=all

        _index_files = Arrays.asList(INDEX_FILES);
    }

    @Override
    public String toString() {
        final JsonWrapper result = new JsonWrapper(new JSONObject());
        result.put("root", _root);
        result.put("host", _host);
        result.put("port", _port);
        result.put("ssl", _use_ssl);
        result.put("compression", _use_compression);
        result.put("uri", this.uri());

        return result.getJSONObject().toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String[] indexFiles() {
        return _index_files.toArray(new String[_index_files.size()]);
    }

    public HttpServerConfig addIndexFile(final String value) {
        if (!_index_files.contains(value)) {
            _index_files.add(value);
        }
        return this;
    }

    public HttpServerConfig encoding(final String value) {
        _encoding = value;
        return this;
    }

    public String encoding() {
        return _encoding;
    }

    public HttpServerConfig maxChunkSize(final int value) {
        _max_chunk_size = value;
        return this;
    }

    public int maxChunkSize() {
        return _max_chunk_size;
    }


    public HttpServerConfig host(final String value) {
        _host = value;
        return this;
    }

    public String host() {
        return _host;
    }

    public HttpServerConfig port(final int value) {
        _port = value;
        return this;
    }

    public int port() {
        return _port;
    }

    public HttpServerConfig portAutodetect(final boolean value) {
        _port_autodect = value;
        return this;
    }

    public boolean portAutodetect() {
        return _port_autodect;
    }

    public HttpServerConfig root(final String value) {
        _root = value;
        return this;
    }

    public HttpServerConfig notFound404(final String value) {
        _not_found_404 = value;
        return this;
    }

    public String notFound404() {
        return _not_found_404;
    }

    public HttpServerConfig portDetectTry(final int value) {
        _port_detection_try = value;
        return this;
    }

    public int portDetectTry() {
        return _port_detection_try;
    }

    public String root() {
        return _root;
    }

    public HttpServerConfig useSsl(final boolean value) {
        _use_ssl = value;
        return this;
    }

    public boolean useSsl() {
        return _use_ssl;
    }

    public HttpServerConfig useCompression(final boolean value) {
        _use_compression = value;
        return this;
    }

    public boolean useCompression() {
        return _use_compression;
    }

    public HttpServerConfig cacheSeconds(final int value) {
        _cache_seconds = value;
        return this;
    }

    public int cacheSeconds() {
        return _cache_seconds;
    }

    /**
     * Set "Access-Control-Allow-Origin" header for all response
     *
     * @param value If empty the header is not setted. "*" is for ALL
     * @return
     */
    public HttpServerConfig corsAllowOrigin(final String value) {
        _cors_allow_origin = value;
        return this;
    }

    public String corsAllowOrigin() {
        return _cors_allow_origin;
    }

    public HttpServerConfig corsAllowMethods(final String value) {
        _cors_allow_methods = value;
        return this;
    }

    public String corsAllowMethods() {
        return _cors_allow_methods;
    }

    public HttpServerConfig corsAllowHeaders(final String value) {
        _cors_allow_headers = value;
        return this;
    }

    public String corsAllowHeaders() {
        return _cors_allow_headers;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uri() {
        return this.buildUri();
    }

    public String uri(final String relativePath) {
        return PathUtils.concat(this.uri(), relativePath);
    }

    public String filePath() {
        return this.root();
    }

    public String filePath(final String relativePath) {
        return this.filePath(relativePath, true);
    }

    public String filePath(final String relativePath, final boolean sanitize) {
        return PathUtils.concat(this.root(), sanitize ? sanitizeUri(relativePath) : relativePath);
    }

    public File file404() {
        try {
            if (StringUtils.hasText(_not_found_404)) {
                final File file = new File(this.filePath(this._not_found_404, false));
                if (file.exists()) {
                    return file;
                }
            }
        } catch (Throwable ignored) {
            System.out.println(ignored);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String buildUri() {
        final StringBuilder sb = new StringBuilder();
        if (_use_ssl) {
            sb.append("https://");
        } else {
            sb.append("http://");
        }
        sb.append(_host).append(":").append(_port);

        return sb.toString();
    }

    private String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                IHttpConstants.INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        return uri;
    }


}
