package org.lyj.commons.network.http.client;

import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.network.http.IHttpConstants;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;

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

    public final static String POST = IHttpConstants.METHOD_POST;
    public final static String GET = IHttpConstants.METHOD_GET;

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
                     final Delegates.SingleResultCallback<String> callback) {

        try {
            this.doPost(surl, params, (err, totalBuffer) -> {
                if (null != err) {
                    Delegates.invoke(callback, err, "");
                } else {
                    Delegates.invoke(callback, null, totalBuffer.read());
                }
            });
        } catch (Throwable t) {
            Delegates.invoke(callback, t, "");
        }
    }

    public void get(final String surl, final Map<String, Object> params,
                    final Delegates.SingleResultCallback<String> callback) {

        try {
            this.doGet(surl, params, (err, totalBuffer) -> {
                if (null != err) {
                    Delegates.invoke(callback, err, "");
                } else {
                    Delegates.invoke(callback, null, totalBuffer.read());
                }
            });
        } catch (Throwable t) {
            Delegates.invoke(callback, t, "");
        }
    }

    public Task<String> post(final String surl, final Map<String, Object> params) {
        return new Task<String>((t) -> {
            this.post(surl, params, (err, response) -> {
                if (null != err) {
                    t.fail(err);
                } else {
                    t.success(response);
                }
            });
        });
    }

    public Task<String> post(final String surl, final JSONObject params) {
        return new Task<String>((t) -> {
            this.post(surl, params, (err, response) -> {
                if (null != err) {
                    t.fail(err);
                } else {
                    t.success(response);
                }
            });
        });
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

            final String body = StringUtils.toQueryString(params, _char_encoding, false);


            request.errorHandler((err) -> {
                Delegates.invoke(callback, err, null);
            });

            request.bodyHandler((totalBuffer) -> {
                Delegates.invoke(callback, null, totalBuffer);
            });

            request.write(body).end();
        } catch (Throwable t) {
            Delegates.invoke(callback, t, null);
        }
    }

    private void doGet(final String surl, final Map<String, Object> params,
                       final Delegates.SingleResultCallback<HttpBuffer> callback) {
        try {
            final String body = StringUtils.toQueryString(params, _char_encoding, true);
            final String url = StringUtils.hasLength(body) ? surl.concat("?").concat(body) : surl;

            HttpRequest request = new HttpRequest(GET, url)
                    .setEncoding(_char_encoding)
                    .setChunkBody(_do_chunk_body)
                    .setChunkSize(_chunk_size)
                    .setConnectionTimeout(_connection_timeout)
                    .setIdleTimeout(_idle_timeout);

            request.errorHandler((err) -> {
                Delegates.invoke(callback, err, null);
            });

            request.bodyHandler((totalBuffer) -> {
                Delegates.invoke(callback, null, totalBuffer);
            });

            request.write(body).end();

        } catch (Throwable t) {
            Delegates.invoke(callback, t, null);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static boolean isPOST(final String method) {
        return StringUtils.hasText(method) && method.toUpperCase().equals(POST);
    }

    public static boolean isGET(final String method) {
        return StringUtils.hasText(method) && method.toUpperCase().equals(GET);
    }

    public static String get(final String url) throws Exception {
        return get(url, null, 60 * 1000);
    }

    public static String get(final String url,
                             final Map<String, Object> params,
                             final int connection_timeout) throws Exception {
        final Task<String> task = new Task<String>(t -> {

            final HttpClient client = new HttpClient();
            client.setDefaultConnectionTimeout(connection_timeout);
            client.get(url, params, (err, result) -> {
                if (null != err) {
                    t.fail(err);
                    //System.out.println(err);
                } else {
                    t.success(result);
                    //System.out.println(result);
                }
            });

        }).run();
        return task.get();
    }

}
