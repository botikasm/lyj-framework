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

    private static final String UPLOAD_ROUTE = "/upload/*"; // POST path to upload a file
    private static final String UPLOAD_DIR = "./upload";    // path on server to store files (relative or absolute)
    private static final String DOWNLOAD_ROOT = "http://localhost:8080"; // http path for (optional) static file download

    private static final String SSL_PATH = "./ssl"; // keystore path (put here keystore file generated)
    private static final String KEY_FILE = "server.key"; // keystore file name (only name, not path)
    private static final String PEM_FILE = "server.pem"; // certificate file name (only name, not path)
    private static final String P12_FILE = "server.p12";

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
    private boolean _use_compression;
    private int _cache_seconds;
    // ssl
    private boolean _use_ssl;
    private String _ssl_path;
    private String _ssl_key_file;
    private String _ssl_pass_key;
    private String _ssl_pem_file;
    private String _ssl_p12_file;  // X.509
    // cross origin requests
    private String _cors_allow_origin; // CORS
    private String _cors_allow_methods; // CORS
    private String _cors_allow_headers; // CORS
    // upload request routing path
    private String _upload_routing; // "/upload"
    private String _upload_dir;
    private String _download_root;
    // 404
    private String _not_found_404;
    // index pages
    private final List<String> _index_files;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServerConfig() {
        _encoding = CharEncoding.UTF_8;
        _max_chunk_size = 1048576; //65536;
        _port = 4000;
        _root = SystemPropertyUtil.get("user.dir");
        _use_compression = true;
        _port_autodect = false;
        _port_detection_try = 100; // try 100 times to get a free port
        _cache_seconds = 60;
        _host = "localhost";
        _not_found_404 = "";

        _use_ssl = false;
        _ssl_path = SSL_PATH;
        _ssl_key_file = KEY_FILE;
        _ssl_pem_file = PEM_FILE;
        _ssl_p12_file = P12_FILE;
        _ssl_pass_key = "";

        _upload_routing = UPLOAD_ROUTE;
        _upload_dir = UPLOAD_DIR;
        _download_root = DOWNLOAD_ROOT;

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
        result.put("ssl_path", _ssl_path);
        result.put("ssl_key_file", _ssl_key_file);
        result.put("ssl_cert_file", _ssl_pem_file);
        result.put("ssl_p12_file", _ssl_p12_file);
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

    public HttpServerConfig sslPath(final String value) {
        _ssl_path = value;
        return this;
    }

    public String sslPath() {
        return _ssl_path;
    }

    public HttpServerConfig sslPassKey(final String value) {
        _ssl_pass_key = value;
        return this;
    }

    public String sslPassKey() {
        return _ssl_pass_key;
    }

    public HttpServerConfig sslKeyFileName(final String value) {
        _ssl_key_file = value;
        return this;
    }

    public String sslKeyFileName() {
        return _ssl_key_file;
    }

    public String sslKeyFilePath() {
        return PathUtils.concat(PathUtils.getAbsolutePath(_ssl_path), _ssl_key_file);
    }

    public File sslKeyFile() {
        final String path = this.sslKeyFilePath();
        final File response = new File(path);
        if (response.exists()) {
            return response;
        }
        return null;
    }

    public HttpServerConfig sslCertFileName(final String value) {
        _ssl_pem_file = value;
        return this;
    }

    public String sslCertFileName() {
        return _ssl_pem_file;
    }

    public String sslCertFilePath() {
        return PathUtils.concat(PathUtils.getAbsolutePath(_ssl_path), _ssl_pem_file);
    }

    public File sslPEMFile() {
        final String path = this.sslCertFilePath();
        final File response = new File(path);
        if (response.exists()) {
            return response;
        }
        return null;
    }

    public HttpServerConfig sslP12FileName(final String value) {
        _ssl_p12_file = value;
        return this;
    }

    public String sslP12FileName() {
        return _ssl_p12_file;
    }

    public String sslP12FilePath() {
        return PathUtils.concat(PathUtils.getAbsolutePath(_ssl_path), _ssl_p12_file);
    }

    public File sslP12File() {
        final String path = this.sslP12FilePath();
        final File response = new File(path);
        if (response.exists()) {
            return response;
        }
        return null;
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

    public String uploadRoute() {
        return _upload_routing;
    }

    public HttpServerConfig uploadRoute(final String value) {
        if (StringUtils.hasText(value)) {
            _upload_routing = value;
        }
        return this;
    }

    public String uploadDir() {
        return _upload_dir;
    }

    public HttpServerConfig uploadDir(final String value) {
        if (StringUtils.hasText(value)) {
            _upload_dir = value;
        }
        return this;
    }

    public String downloadRoot() {
        return _download_root;
    }

    public HttpServerConfig downloadRoot(final String value) {
        if (StringUtils.hasText(value)) {
            _download_root = value;
        }
        return this;
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
