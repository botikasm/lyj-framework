package org.lyj.commons.network.http.client;

import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import java.util.Map;

/**
 * Simple HttpClient
 */
public class HttpClient {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public final static int DEF_CHUNK_SIZE = 4096;
    public final static int DEF_CONN_TIMEOUT = 1000 * 60 * 5; // 5 minute
    public final static int DEF_IDLE_TIMEOUT = DEF_CONN_TIMEOUT;

    public final static String POST = "POST";
    public final static String GET = "GET";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _char_encoding;
    private int _chunk_size;
    private boolean _do_chunk_body;
    private int _connection_timeout;
    private int _idle_timeout;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClient() {
        _char_encoding = CharEncoding.UTF_8;
        _chunk_size = DEF_CHUNK_SIZE;
        _do_chunk_body = true;
        _connection_timeout = DEF_CONN_TIMEOUT;
        _idle_timeout = DEF_IDLE_TIMEOUT;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    //--  p r o p e r t i e s  --//

    public HttpClient setDefaultEncoding(final String value) {
        _char_encoding = value;
        return this;
    }

    public String getDefaultEncoding() {
        return _char_encoding;
    }

    public HttpClient setDefaultChunkBody(final boolean value) {
        _do_chunk_body = value;
        return this;
    }

    public boolean isDefaultChunkBody() {
        return _do_chunk_body;
    }


    public int getDefaultConnectionTimeout() {
        return _connection_timeout;
    }

    public HttpClient setDefaultConnectionTimeout(final int value) {
        _connection_timeout = value;
        return this;
    }

    public int getDefaultIdleTimeout() {
        return _idle_timeout;
    }

    public HttpClient setDefaultIdleTimeout(final int value) {
        _idle_timeout = value;
        return this;
    }

    public int getDefaultChunkSize() {
        return _chunk_size;
    }

    public HttpClient setDefaultChunkSize(final int value) {
        _chunk_size = value;
        return this;
    }

    //--  m e t h o d s  --//

    public void post(final String surl, final JSONObject params,
                     final Delegates.SingleResultCallback<String> callback) throws Exception {
        this.post(surl, JsonWrapper.toMap(params), callback);
    }

    public void post(final String surl, final Map<String, Object> params,
                     final Delegates.SingleResultCallback<String> callback){

        try{
            this.doPost(surl, params, (err, totalBuffer)->{
                if(null!=err){
                    Delegates.invoke(callback, err, "");
                } else {
                    Delegates.invoke(callback, null, totalBuffer.read());
                }
            });
        } catch(Throwable t){
            Delegates.invoke(callback, t, "");
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void doPost(final String surl, final Map<String, Object> params,
                      final Delegates.SingleResultCallback<HttpBuffer> callback) {
        try {
            HttpRequest request = new HttpRequest(POST, surl)
                    .setEncoding(_char_encoding)
                    .setChunkBody(_do_chunk_body)
                    .setChunkSize(_chunk_size)
                    .setConnectionTimeout(_connection_timeout)
                    .setIdleTimeout(_idle_timeout);

            final String body = StringUtils.toQueryString(params, _char_encoding);


            request.errorHandler((err) -> {
                Delegates.invoke(callback, err, null);
            });

            request.bodyHandler((totalBuffer) -> {
                Delegates.invoke(callback, null, totalBuffer);
            });

            request.write(body).end();
        } catch(Throwable t) {
            Delegates.invoke(callback, t, null);
        }
    }


}
