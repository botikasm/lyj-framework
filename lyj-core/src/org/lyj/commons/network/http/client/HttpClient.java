package org.lyj.commons.network.http.client;

import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.ByteUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Simple HttpClient
 */
public class HttpClient {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final static int DEF_CONN_TIMEOUT = 60000; // 1 minute
    private final static int DEF_IDLE_TIMEOUT = 15000;
    private final static int DEF_PORT = 80;
    private final static int DEF_CHUNK_SIZE = 4096;

    private final static String POST = "POST";
    private final static String GET = "GET";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _use_ssl; // NOT IMPLEMENTED
    private String _char_encoding;
    private int _chunk_size;
    private boolean _do_chunk_body;
    private int _connection_timeout;
    private int _idle_timeout;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClient() {
        _use_ssl = false;
        _char_encoding = CharEncoding.UTF_8;
        _chunk_size = DEF_CHUNK_SIZE;
        _do_chunk_body = true;
        _connection_timeout = DEF_CONN_TIMEOUT;
        _idle_timeout = DEF_IDLE_TIMEOUT;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpClient setUseSSL(final boolean value) {
        _use_ssl = value;
        return this;
    }

    public boolean isUseSSL() {
        return _use_ssl;
    }

    public HttpClient setEncoding(final String value) {
        _char_encoding = value;
        return this;
    }

    public String getEncoding() {
        return _char_encoding;
    }

    public void post(final String surl, final JSONObject params,
                     final Delegates.SingleResultCallback<String> callback) throws Exception {
        this.post(surl, JsonWrapper.toMap(params), callback);
    }

    public void post(final String surl, final Map<String, Object> params,
                     final Delegates.SingleResultCallback<String> callback){

        try{
            final String response = this.post(surl, params);
            Delegates.invoke(callback, null, response);
        } catch(Throwable t){
            Delegates.invoke(callback, t, "");
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean exceedBodyLimit(final String body) {
        return _chunk_size > 0 && StringUtils.hasText(body) && body.length() > _chunk_size;
    }

    private String post(final String surl, final Map<String, Object> params) throws Exception{
        final URL url = new URL(surl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        final String body = StringUtils.toQueryString(params, _char_encoding);
        final boolean is_chunked = _do_chunk_body && this.exceedBodyLimit(body);

        conn.setReadTimeout(_idle_timeout);
        conn.setConnectTimeout(_connection_timeout);
        conn.setRequestMethod(POST);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        if(is_chunked) {
            conn.setChunkedStreamingMode(_chunk_size);
        }

        try (OutputStream os = conn.getOutputStream()) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, _char_encoding))) {
                if(is_chunked) {
                    final String[] chunks = StringUtils.chunk(body, _chunk_size);
                    for (String chunk : chunks) {
                        writer.write(chunk);
                    }
                } else {
                    writer.write(body);
                }
                writer.flush();
            }
        }

        try {
            conn.connect();
            final int code = conn.getResponseCode();
            final String message = conn.getResponseMessage();
            if (code == HttpURLConnection.HTTP_OK) {
                try (InputStream in = conn.getInputStream()) {
                    final byte[] bytes = ByteUtils.getBytes(in);
                    if (bytes.length > 0) {
                        return new String(bytes);
                    }
                }
            } else {
                throw new Exception(FormatUtils.format("", code, message));
            }
        }finally{
            conn.disconnect();
        }

        return "";
    }


}
