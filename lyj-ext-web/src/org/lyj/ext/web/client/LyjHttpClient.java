package org.lyj.ext.web.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import java.util.Map;

/**
 * Http Client wrapper.
 * Keep alive is FALSE by default.
 */
public class LyjHttpClient
        extends AbstractVerticle {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final static int DEF_BODY_SIZE_LIMIT = 4096; // byte limit
    private final static int DEF_TIMEOUT = 60000; // 1 minute
    private final static int DEF_PORT = 80;
    private final static int DEF_CHUNK_SIZE = DEF_BODY_SIZE_LIMIT;


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private HttpClient __client; //lazy

    private String _host;
    private int _port;
    private int _connection_timeout;
    private int _idle_timeout;
    private boolean _keep_alive;
    private boolean _auto_chunk_body;
    private boolean _chunk_body;
    private int _body_limit;
    private int _chunk_size;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private LyjHttpClient() {
        _connection_timeout = DEF_TIMEOUT;
        _idle_timeout = 0;
        _keep_alive = false;
        _port = DEF_PORT;
        _auto_chunk_body = true;
        _chunk_body = false;
        _body_limit = DEF_BODY_SIZE_LIMIT;
        _chunk_size = DEF_CHUNK_SIZE;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public LyjHttpClient setConnectTimeout(final int value) {
        _connection_timeout = value;
        return this;
    }

    public int getConnectTimeout() {
        return _connection_timeout;
    }

    /**
     * Client inactivity timeout in milliseconds.
     * Default is ZERO, no timeout for client inactivity.<br>
     * Set this value only if you want a timeout from a request to another.
     *
     * @param value Idle timeout. Default is ZERO.
     * @return
     */
    public LyjHttpClient setIdleTimeout(final int value) {
        _idle_timeout = value;
        return this;
    }

    public int getIdleTimeout() {
        return _idle_timeout;
    }

    public LyjHttpClient setDefaultHost(final String value) {
        _host = value;
        return this;
    }

    public String getDefaultHost() {
        return _host;
    }

    public LyjHttpClient setDefaultPort(final int value) {
        _port = value;
        return this;
    }

    public int getDefaultPort() {
        return _port;
    }

    public LyjHttpClient setKeepAlive(final boolean value) {
        _keep_alive = value;
        return this;
    }

    public boolean isKeepAlive() {
        return _keep_alive;
    }

    public LyjHttpClient setAutoChunkBoby(final boolean value) {
        _auto_chunk_body = value;
        return this;
    }

    public boolean isAutoChunkBody() {
        return _auto_chunk_body;
    }

    public LyjHttpClient setChunkBody(final boolean value) {
        _chunk_body = value;
        return this;
    }

    public boolean isChunkBody() {
        return _chunk_body;
    }

    public LyjHttpClient setChunkSize(final int value) {
        _chunk_size = value;
        return this;
    }

    public int getChunkSize() {
        return _chunk_size;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c    m e t h o d s
    // ------------------------------------------------------------------------

    public Task<String> post(final String url, final JSONObject params) {
        return new Task<String>(t -> {
            this.post(url, params, (err, result) -> {
                if (null != err) {
                    t.fail(err);
                } else {
                    t.success(result);
                }
            });
        });
    }

    public void post(final String url, final JSONObject params,
                     final Delegates.SingleResultCallback<String> callback) throws Exception {
        final Map<String, Object> map = JsonWrapper.toMap(params);
        this.postString(url, StringUtils.toQueryString(map), callback);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private HttpClient client() {
        if (null == __client) {
            HttpClientOptions options = new HttpClientOptions();

            if (StringUtils.hasText(_host)) {
                options.setDefaultHost(_host);
                if (_port > 0) {
                    options.setDefaultPort(_port);
                }
            }
            options.setKeepAlive(_keep_alive)
                    .setIdleTimeout(_idle_timeout)
                    .setConnectTimeout(_connection_timeout);
            __client = vertx.createHttpClient(options);
        }
        return __client;
    }

    private boolean exceedBodyLimit(final String body) {
        return _body_limit > 0 && StringUtils.hasText(body) && body.length() > _body_limit;
    }

    private void postString(final String url, final String body, final Delegates.SingleResultCallback<String> callback) {
        HttpClientRequest request = this.client().post(url, (response) -> {
            response.bodyHandler(totalBuffer -> {
                Delegates.invoke(callback, null, totalBuffer.toString());
            });
            response.exceptionHandler(e -> {
                Delegates.invoke(callback, e, null);
            });
        });

        request.exceptionHandler(e -> {
            Delegates.invoke(callback, e, null);
        });

        //-- headers --//
        request.putHeader("content-type", "application/x-www-form-urlencoded");

        if (!this.exceedBodyLimit(body)) {
            request.putHeader("Content-Length", body.length() + "");
            request.write(body);
        } else {
            // write body chunked
            request.setChunked(true);
            String[] chunks = StringUtils.chunk(body, _chunk_size);
            for (String chunk : chunks) {
                request.write(chunk);
            }
        }

        // close request and send data
        request.end();
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static void create(final Delegates.SingleResultCallback<LyjHttpClient> callback) {
        try {
            // deploy App Server
            Vertx vertx = Vertx.vertx();
            LyjHttpClient client = new LyjHttpClient();
            vertx.deployVerticle(client, stringAsyncResult -> {
                Delegates.invoke(callback, null, client);
            });
        } catch (Exception e) {
            Delegates.invoke(callback, e, null);
        }
    }

    public static Task<LyjHttpClient> create() {
        return new Task<LyjHttpClient>(t -> {
            create((err, client) -> {
                if (null != err) {
                    t.fail(err);
                } else {
                    t.success(client);
                }
            });
        });
    }

}
