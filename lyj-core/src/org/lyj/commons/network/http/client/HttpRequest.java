package org.lyj.commons.network.http.client;

import org.lyj.commons.Delegates;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.network.http.client.exceptions.BadStateException;
import org.lyj.commons.network.http.client.exceptions.ConnectionException;
import org.lyj.commons.network.http.client.exceptions.UnsupportedMethodException;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.MimeTypeUtils;
import org.lyj.commons.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * http://stackoverflow.com/questions/21996190/how-can-i-write-a-post-statement-to-an-httpurlconnection-thats-zipped
 */
public class HttpRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    private final static String POST = HttpClient.POST;
    private final static String GET = HttpClient.GET;
    private final static String[] METHODS = new String[]{POST, GET};

    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static  final String HEADER_CHARSET = "charset";
    public static  final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_TYPE_FORM = MimeTypeUtils.MIME_FORM;
    public static final String HEADER_CONTENT_TYPE_JSON = MimeTypeUtils.MIME_JSON;

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
    private boolean _use_cache;

    private HttpURLConnection _connection;

    private Map<String, String> _headers;
    private HttpBuffer _buffer;

    // handlers
    private Delegates.Callback<HttpBuffer> _bodyHandler;
    private Delegates.Callback<Throwable> _errorHandler;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpRequest(final String method,
                       final String url) throws IOException, UnsupportedMethodException {
        _method = method;
        _url = url;
        _mimeType = MimeTypeUtils.MIME_FORM;

        _chunk_size = HttpClient.DEF_CHUNK_SIZE;
        _do_chunk_body = true;
        _connection_timeout = HttpClient.DEF_CONN_TIMEOUT;
        _idle_timeout = HttpClient.DEF_IDLE_TIMEOUT;
        _char_encoding = CharEncoding.UTF_8;

        _use_cache = false;

        _headers = new HashMap<>();
        _buffer = new HttpBuffer();

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    //-- p r o p e r t i e s --//

    public HttpRequest bodyHandler(final Delegates.Callback<HttpBuffer> value) {
        _bodyHandler = value;
        return this;
    }

    public HttpRequest errorHandler(final Delegates.Callback<Throwable> value) {
        _errorHandler = value;
        return this;
    }

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

    public HttpRequest setUseCache(final boolean value) {
        _use_cache = value;
        return this;
    }

    public boolean isUseCache() {
        return _use_cache;
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

    public int getChunkSize() {
        return _chunk_size;
    }

    public HttpRequest setChunkSize(final int value) {
        _chunk_size = value;
        return this;
    }

    //-- a c t i o n --//

    public HttpRequest putHeader(final String key, final String value) {
        _headers.put(key, value);
        return this;
    }

    public HttpRequest write(final String data) {
        _buffer.write(data);
        return this;
    }

    public void end(final String data) {
        this.write(data).end();
    }

    public void end() {
        try {
            this.flush();
        } catch (Throwable t) {
            this.handleError(t);
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

            _connection.setRequestMethod(_method);
            _connection.setDoInput(true);
            _connection.setDoOutput(!_method.equals(GET)); // no output for get

        } else {
            // method not supported
            throw new UnsupportedMethodException(_method);
        }
    }

    private void handleError(final Throwable t) {
        Delegates.invoke(_errorHandler, t);
    }

    private void handleBuffer(final HttpBuffer data) {
        Delegates.invoke(_bodyHandler, data);
    }

    private boolean exceedBodyLimit(final int bodySize) {
        return _chunk_size > 0 && bodySize > _chunk_size;
    }

    private boolean doChunkBody(final int bodySize) {
        /**
         * always chunk body or does not works
         */
        return _do_chunk_body || this.exceedBodyLimit(bodySize);
    }

    private boolean isSupportedMethod(final String method) {
        return CollectionUtils.contains(METHODS, method);
    }

    private boolean containsHeader(final String akey) {
        if (null != _headers) {
            Set<String> keys = _headers.keySet();
            for (final String key : keys) {
                if (key.toLowerCase().equals(akey.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void writeHeaders(final int bodySize, final boolean writeBody, final boolean writeChuncked) {
        // write all headers
        Set<String> keys = _headers.keySet();
        for (final String key : keys) {
            _connection.setRequestProperty(key, _headers.get(key));
        }

        if (writeBody) {
            // content lenght
            if (!this.containsHeader(HEADER_CONTENT_LENGTH)) {
                _connection.setRequestProperty(HEADER_CONTENT_LENGTH, bodySize + "");
            }
            if (!this.containsHeader(HEADER_CONTENT_TYPE) && StringUtils.hasText(_mimeType)) {
                _connection.setRequestProperty(HEADER_CONTENT_TYPE, _mimeType);
            }
            if (!this.containsHeader(HEADER_CHARSET) && StringUtils.hasText(_char_encoding)) {
                _connection.setRequestProperty(HEADER_CHARSET, _char_encoding);
            }
        }
    }

    private void flush() {
        // check connection exists
        if (null == _connection) {
            try {
                this.init();
            } catch (Throwable t) {
                this.handleError(t);
                return;
            }
        }

        if (null != _connection) {

            _connection.setUseCaches(_use_cache); // cache

            boolean write_body = false;
            boolean write_chunked = false;
            int bodySize = null != _buffer ? _buffer.size() : 0;

            // check if need write the body and if it is chunked
            if (!_method.equals(GET)) {
                // body
                if (null != _buffer && _buffer.size() > 0) {
                    write_body = true;
                    // chunked?
                    if (this.doChunkBody(_buffer.size())) {
                        _connection.setChunkedStreamingMode(_chunk_size);
                        write_chunked = true;
                    }
                }
            }

            // headers
            this.writeHeaders(bodySize, write_body, write_chunked);

            // body
            try {
                // write the body to stream
                if (write_body && null != _buffer) {
                    try (OutputStream os = _connection.getOutputStream()) {
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, _char_encoding))) {
                            if (write_chunked) {
                                final String[] chunks = _buffer.getStringChunks(_chunk_size);
                                for (String chunk : chunks) {
                                    writer.write(chunk);
                                }
                            } else {
                                writer.write(_buffer.toString());
                            }
                            writer.flush();
                        }
                    }
                }

                // connect and get response
                _connection.connect();

                final int code = _connection.getResponseCode();
                final String message = _connection.getResponseMessage();

                if (code == HttpURLConnection.HTTP_OK) {
                    try (InputStream in = _connection.getInputStream()) {
                        this.handleBuffer(new HttpBuffer(in));
                    }
                } else {
                    this.handleError(new ConnectionException(code, message));
                }
            } catch (Throwable t) {
                this.handleError(t);
            } finally {
                _connection.disconnect();
                _connection = null;
                _buffer.clear();
                _headers.clear();
            }

        } else {
            // connection is not active, may be url was broken
            this.handleError(new BadStateException("Missing Connection or Connection is not properly configured."));
        }
    }


}
