package org.lyj.ext.netty.client.web;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.lyj.commons.Delegates;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FormatUtils;
import org.lyj.ext.netty.client.web.base.HttpClientHandler;
import org.lyj.ext.netty.client.web.base.HttpClientInitializer;
import org.lyj.ext.netty.server.web.IHttpConstants;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpClient extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpClientRequest _request;

    private Delegates.Callback<HttpClientResponse> _callback_success;
    private Delegates.Callback<Throwable> _callback_fail;

    private boolean _initialized;
    private SslContext _sslCtx;
    private ChannelHandler _handler;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClient() {
        _initialized = false;
        _request = new HttpClientRequest();
    }

    public HttpClient(final HttpClientRequest request) {
        _initialized = false;
        _request = request;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpClientRequest request() {
        return _request;
    }

    public HttpClient get() {
        _request.method(IHttpConstants.METHOD_GET);
        return this;
    }

    public HttpClient post() {
        _request.method(IHttpConstants.METHOD_POST);
        return this;
    }

    public HttpClient put() {
        _request.method(IHttpConstants.METHOD_PUT);
        return this;
    }

    public HttpClient delete() {
        _request.method(IHttpConstants.METHOD_DELETE);
        return this;
    }

    public HttpClient get(final String url) throws URISyntaxException {
        this.url(url);
        _request.method(IHttpConstants.METHOD_GET);
        return this;
    }

    public HttpClient post(final String url) throws URISyntaxException {
        this.url(url);
        _request.method(IHttpConstants.METHOD_POST);
        return this;
    }

    public HttpClient put(final String url) throws URISyntaxException {
        this.url(url);
        _request.method(IHttpConstants.METHOD_PUT);
        return this;
    }

    public HttpClient delete(final String url) throws URISyntaxException {
        this.url(url);
        _request.method(IHttpConstants.METHOD_DELETE);
        return this;
    }

    public HttpClient url(final String url) throws URISyntaxException {
        _request.uri(url);
        return this;
    }

    public HttpClient url(final URL url) throws URISyntaxException {
        _request.uri(url);
        return this;
    }

    public HttpClient url(final URI url) throws URISyntaxException {
        _request.uri(url);
        return this;
    }

    /**
     * Handle exception
     *
     * @param callback Callback Handler
     * @return
     */
    public HttpClient fail(final Delegates.Callback<Throwable> callback) {
        _callback_fail = callback;
        return this;
    }

    public HttpClient send(final Delegates.Callback<HttpClientResponse> callback) {
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
        return this;
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

    private void onResponse(final HttpClientResponse response) {
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
            _handler = new HttpClientHandler( this::onResponse, this::onErrorHandle, _request.contentFile());
        }
    }

    private void send() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer(_sslCtx, _handler));

            // Make the connection attempt.
            Channel ch = b.connect(_request.host(), _request.port()).sync().channel();

            // Prepare the HTTP request.
            HttpRequest request = _request.nativeHttpRequest();

            // Send the HTTP request.
            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }


}
