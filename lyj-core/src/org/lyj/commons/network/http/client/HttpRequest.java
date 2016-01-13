package org.lyj.commons.network.http.client;

import org.lyj.commons.Delegates;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.network.http.client.exceptions.BadStateException;
import org.lyj.commons.network.http.client.exceptions.UnsupportedMethodException;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.MimeTypeUtils;
import org.lyj.commons.util.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post
 */
public class HttpRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final static int DEF_CHUNK_SIZE = 4096;
    private final static int DEF_CONN_TIMEOUT = 60000; // 1 minute
    private final static int DEF_IDLE_TIMEOUT = 15000;

    private final static String POST = "POST";
    private final static String GET = "GET";
    private final static String[] METHODS = new String[]{POST, GET};

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private int _chunk_size;
    private boolean _do_chunk_body;
    private String _method;
    private String _url;
    private String _mimeType;
    private String _char_encoding;
    private int _connection_timeout;
    private int _idle_timeout;

    private HttpURLConnection _connection;

    private Map<String, String> _headers;
    private HttpBuffer _buffer;

    // handlers
    private Delegates.SingleResultCallback<HttpBuffer> _bodyHandler;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpRequest(final String method,
                       final String url) throws IOException, UnsupportedMethodException {
        _method = method;
        _url = url;
        _mimeType = MimeTypeUtils.MIME_FORM;

        _chunk_size = DEF_CHUNK_SIZE;
        _do_chunk_body = true;
        _connection_timeout = DEF_CONN_TIMEOUT;
        _idle_timeout = DEF_IDLE_TIMEOUT;
        _char_encoding = CharEncoding.UTF_8;

        _headers = new HashMap<>();
        _buffer = new HttpBuffer();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    //-- p r o p e r t i e s --//

    public HttpRequest setMimeType(final String value) {
        _mimeType = value;
        return this;
    }

    public String getMimeType() {
        return _mimeType;
    }

    public HttpRequest setChunkBody(final boolean value) {
        _do_chunk_body = value;
        return this;
    }

    public boolean isChunkBody() {
        return _do_chunk_body;
    }

    public HttpRequest setEncoding(final String value) {
        _char_encoding = value;
        return this;
    }

    public String getEncoding() {
        return _char_encoding;
    }

    public int getConnectionTimeout() {
        return _connection_timeout;
    }

    public HttpRequest setConnectionTimeout(final int value) {
        _connection_timeout = value;
        return this;
    }

    public int getIdleTimeout() {
        return _idle_timeout;
    }

    public HttpRequest setIdleTimeout(final int value) {
        _idle_timeout = value;
        return this;
    }

    //-- a c t i o n --//

    public HttpRequest putHeader(final String key, final String value) {
        _headers.put(key, value);
        return this;
    }

    public HttpRequest write(final String data) {
        _buffer.add(data);
        return this;
    }

    public void end(final String data) {
        this.write(data).end();
    }

    public void end() {
        try {
            this.flush();
        } catch(Throwable t){

        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() throws IOException, UnsupportedMethodException {
        if (isSupportedMethod(_method)) {
            final URL url = new URL(_url);
            _connection = (HttpURLConnection) url.openConnection();
            _connection.setConnectTimeout(_connection_timeout);
            _connection.setReadTimeout(_idle_timeout);
        } else {
            // method not supported
            throw new UnsupportedMethodException(_method);
        }
    }

    private boolean exceedBodyLimit(final String body) {
        return _chunk_size > 0 && StringUtils.hasText(body) && body.length() > _chunk_size;
    }

    private boolean doChunkBody(final String body) {
        return _do_chunk_body && this.exceedBodyLimit(body);
    }

    private boolean isSupportedMethod(final String method) {
        return CollectionUtils.contains(METHODS, method);
    }

    private void flush() throws BadStateException {
        if (null != _connection) {

        } else {
            // connection is not active, may be url was broken
            throw new BadStateException("Missing Connection or Connection is not properly configured.");
        }
    }

}
