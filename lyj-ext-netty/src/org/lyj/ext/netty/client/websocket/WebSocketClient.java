package org.lyj.ext.netty.client.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.lyj.commons.Delegates;
import org.lyj.ext.netty.client.websocket.impl.WebSocketClientHandler;

import javax.net.ssl.SSLException;
import java.net.URI;

public class WebSocketClient {

    private String _end_point;
    private URI _uri;
    private String _scheme;
    private String _host;
    private int _port;

    private Channel _channel;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebSocketClient(final String end_point) {
        _end_point = end_point;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String scheme() {
        return _scheme;
    }

    public boolean useSSL() {
        return "wss".equalsIgnoreCase(this.scheme());
    }

    public Channel open() throws Exception {
        this.close();
        this.init();

        _channel = new Channel(this.getSsl(), _uri, _host, _port);

        return _channel;
    }

    public void close() {
        if (null != _channel) {
            _channel.close();
            _channel = null;
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() throws Exception {
        _uri = new URI(_end_point);

        _scheme = _uri.getScheme() == null ? "ws" : _uri.getScheme();
        _host = _uri.getHost() == null ? "127.0.0.1" : _uri.getHost();
        if (_uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(_scheme)) {
                _port = 80;
            } else if ("wss".equalsIgnoreCase(_scheme)) {
                _port = 443;
            } else {
                _port = -1;
            }
        } else {
            _port = _uri.getPort();
        }

        if (!"ws".equalsIgnoreCase(_scheme) && !"wss".equalsIgnoreCase(_scheme)) {
            throw new Exception("Only WS(S) is supported.");
        }
    }

    private SslContext getSsl() throws SSLException {
        final boolean ssl = this.useSSL();
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
        return sslCtx;
    }


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------


    public static class Channel {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private EventLoopGroup _group;
        private WebSocketClientHandler _handler;
        private io.netty.channel.Channel _ch;

        // callbacks
        private Delegates.Callback<String> _text_callback;
        private Delegates.Handler _pong_callback;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        Channel(final SslContext sslCtx,
                final URI uri,
                final String host,
                final int port) throws InterruptedException {

            this.init(sslCtx, uri, host, port);
        }

        // ------------------------------------------------------------------------
        //                      h a n d l e r s
        // ------------------------------------------------------------------------

        public Channel onReceiveText(final Delegates.Callback<String> callback) {
            _text_callback = callback;
            if (null != _handler) {
                _handler.onReceiveText(_text_callback);
            }
            return this;
        }

        public Channel onReceivePong(final Delegates.Handler callback) {
            _pong_callback = callback;
            if (null != _handler) {
                _handler.onReceivePong(_pong_callback);
            }
            return this;
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public void close() {
            if (null != _group) {
                _group.shutdownGracefully();
                _group = null;
            }
        }

        public Channel send(final String message) {
            WebSocketFrame frame = new TextWebSocketFrame(message);
            _ch.writeAndFlush(frame);

            return this;
        }

        public Channel ping() {
            WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
            _ch.writeAndFlush(frame);

            return this;
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private void init(final SslContext sslCtx,
                          final URI uri,
                          final String host,
                          final int port) throws InterruptedException {
            _group = new NioEventLoopGroup();
            try {
                // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
                // If you change it to V00, ping is not supported and remember to change
                // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
                _handler = new WebSocketClientHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                                uri, WebSocketVersion.V13, null, false,
                                new DefaultHttpHeaders()
                        )
                );

                _handler.onReceivePong(_pong_callback);
                _handler.onReceiveText(_text_callback);

                Bootstrap b = new Bootstrap();
                b.group(_group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline p = ch.pipeline();
                                if (sslCtx != null) {
                                    p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                                }
                                p.addLast(
                                        new HttpClientCodec(),
                                        new HttpObjectAggregator(8192),
                                        _handler);
                            }
                        });

                _ch = b.connect(host, port).sync().channel();
                _handler.handshakeFuture().sync();

                // read console
                /*
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String msg = console.readLine();
                    if (msg == null) {
                        break;
                    } else if ("bye".equals(msg.toLowerCase())) {
                        ch.writeAndFlush(new CloseWebSocketFrame());
                        ch.closeFuture().sync();
                        break;
                    } else if ("ping".equals(msg.toLowerCase())) {
                        WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
                        ch.writeAndFlush(frame);
                    } else {
                        WebSocketFrame frame = new TextWebSocketFrame(msg);
                        ch.writeAndFlush(frame);
                    }
                }*/

            } finally {
                // _group.shutdownGracefully();
            }
        }

    }
}
