package org.lyj.ext.netty.client.web.temp;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FormatUtils;
import org.lyj.ext.netty.HttpHeader;
import org.lyj.ext.netty.client.web.temp.base.HttpClientHandler;
import org.lyj.ext.netty.client.web.temp.base.HttpClientInitializer;
import org.lyj.ext.netty.server.web.IHttpConstants;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class HttpTempClient
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpTempClientRequest _request;

    private Delegates.Callback<HttpTempClientResponse> _callback_success;
    private Delegates.Callback<Throwable> _callback_fail;

    private boolean _initialized;
    private SslContext _sslCtx;
    private ChannelHandler _handler;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpTempClient() {
        _initialized = false;
        _request = new HttpTempClientRequest(CharEncoding.UTF_8);

    }

    public HttpTempClient(final HttpTempClientRequest request) {
        _initialized = false;
        _request = request;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpTempClientRequest request() {
        return _request;
    }

    public HttpHeader headers() {
        return _request.headers();
    }

    public HttpTempClient method(final String method) {
        _request.method(method);
        return this;
    }

    public HttpTempClient get() {
        _request.method(IHttpConstants.METHOD_GET);
        return this;
    }

    public HttpTempClient post() {
        _request.method(IHttpConstants.METHOD_POST);
        return this;
    }

    public HttpTempClient put() {
        _request.method(IHttpConstants.METHOD_PUT);
        return this;
    }

    public HttpTempClient delete() {
        _request.method(IHttpConstants.METHOD_DELETE);
        return this;
    }

    public HttpTempClient get(final String url) throws URISyntaxException {
        this.url(url);
        _request.method(IHttpConstants.METHOD_GET);
        return this;
    }

    public HttpTempClient post(final String url) throws URISyntaxException {
        this.url(url);
        _request.method(IHttpConstants.METHOD_POST);
        return this;
    }

    public HttpTempClient put(final String url) throws URISyntaxException {
        this.url(url);
        _request.method(IHttpConstants.METHOD_PUT);
        return this;
    }

    public HttpTempClient delete(final String url) throws URISyntaxException {
        this.url(url);
        _request.method(IHttpConstants.METHOD_DELETE);
        return this;
    }

    public HttpTempClient url(final String url) throws URISyntaxException {
        _request.uri(url);
        return this;
    }

    public HttpTempClient url(final URL url) throws URISyntaxException {
        _request.uri(url);
        return this;
    }

    public HttpTempClient url(final URI url) throws URISyntaxException {
        _request.uri(url);
        return this;
    }

    public HttpTempClient body(final JSONObject body) throws URISyntaxException {
        _request.body(body);
        return this;
    }

    public HttpTempClient body(final Map<String, String> body) throws URISyntaxException {
        _request.body(body);
        return this;
    }

    public String encoding() {
        return _request.encoding();
    }

    public HttpTempClient encoding(final String value) {
        _request.encoding(value);
        return this;
    }

    /**
     * Handle exception
     *
     * @param callback Callback Handler
     * @return
     */
    public HttpTempClient fail(final Delegates.Callback<Throwable> callback) {
        _callback_fail = callback;
        return this;
    }

    public void send(final Delegates.Callback<HttpTempClientResponse> callback) {
        _callback_success = callback;
        try {
            this.init();
        } catch (Throwable t) {
            this.onError("init", t);
        }
        try {
            this.send();
        } catch (Throwable t) {
            this.onError("send", t);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void onError(final Throwable error) {
        this.onError("internal", error);
    }

    private void onErrorHandle(final Throwable error) {
        this.onError("handle", error);
    }

    private void onError(final String errContext, final Throwable error) {
        if (null != error) {
            super.error("onError", FormatUtils.format("CONTEXT: %s. ERROR: %s", errContext, error.toString()));
            if (null != _callback_fail) {
                _callback_fail.handle(error);
            }
        }
    }

    private void onResponse(final HttpTempClientResponse response) {
        if (null != _callback_success) {
            _callback_success.handle(response);
        }
    }

    private void init() throws SSLException {
        if (!_initialized) {
            //--INIT SSL--//
            if (_request.isSsl()) {
                _sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } else {
                _sslCtx = null;
            }

            //--HANDLER--//
            _handler = new HttpClientHandler( _request, this::onResponse, this::onErrorHandle );

        }
    }

    private void send() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer(_sslCtx, _handler));

            _request.send(b);

        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }


}
