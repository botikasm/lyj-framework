package org.lyj.ext.netty.server.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.*;
import org.lyj.ext.netty.server.web.base.web.WebServerInitializer;
import org.lyj.ext.netty.server.web.controllers.HttpServerRequestDelegates;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

import javax.net.ssl.KeyManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HttpServer extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private HttpServerConfig _config;

    private final List<Channel> _channels;
    private final List<ChannelHandler> _channel_handlers;

    private EventLoopGroup _bossGroup;
    private EventLoopGroup _workerGroup;
    private SslContext _sslCtx;
    private ServerBootstrap _bootstrap;

    private final HttpServerRequestDelegates _request_delegates;

    private boolean _initialized;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServer() {
        _config = new HttpServerConfig();

        _channels = new ArrayList<>();
        _channel_handlers = new ArrayList<>();
        _channel_handlers.add(new WebServerInitializer(this)); // default handler

        _request_delegates = new HttpServerRequestDelegates(_config);

        _initialized = false;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpServer start() {
        this.init();
        this.run();
        return this;
    }

    public HttpServer stop() {
        this.close();
        return this;
    }

    public ChannelFuture join() throws InterruptedException {
        return this.sync();
    }

    public SslContext ssl() {
        return _sslCtx;
    }

    public HttpServerConfig config() {
        return _config;
    }

    public HttpServer handler(final AbstractRequestHandler handler) {
        _request_delegates.add(handler);
        return this;
    }

    public HttpServer handler(final Class<? extends AbstractRequestHandler> handler) {
        _request_delegates.add(handler);
        return this;
    }

    public HttpServerRequestDelegates handlers() {
        return _request_delegates;
    }

    public String path(final String relative_path) {
        if (StringUtils.hasText(relative_path)) {
            return PathUtils.concat(this.config().root(), relative_path);
        }
        return this.config().root();
    }

    public String uri(final String relative_path) {
        if (StringUtils.hasText(relative_path)) {
            return PathUtils.concat(this.config().uri(), relative_path);
        }
        return this.config().uri();
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected List<ChannelHandler> channels() {
        return _channel_handlers;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (_initialized) {
            return;
        }
        _initialized = true;

        try {
            // Configure SSL.
            if (_config.useSsl()) {
                _sslCtx = createSSL();
            } else {
                _sslCtx = null;
            }

            if (null == _sslCtx) {
                _config.useSsl(false);
            }

            _bossGroup = new NioEventLoopGroup(1);
            _workerGroup = new NioEventLoopGroup();

            _bootstrap = new ServerBootstrap();
            _bootstrap.group(_bossGroup, _workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG));

        } catch (Throwable t) {
            super.error("init", t);
        }
    }

    private SslContext createSSL() throws CertificateException, IOException,
            NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        try {
            if (null != _config.sslKeyFile()
                    && (null != _config.sslPEMFile() || null != _config.sslP12File())) {
                try {
                    // .p12 (X.509)
                    if (null != _config.sslP12File()) {
                        final char[] psw = _config.sslPassKey().toCharArray();
                        final byte[] cert = ByteUtils.getBytes(_config.sslP12File());

                        final KeyStore ks = KeyStore.getInstance("PKCS12");
                        ks.load(new ByteArrayInputStream(cert), psw);

                        final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                        kmf.init(ks, psw);

                        return SslContextBuilder.forServer(kmf).build();
                    } else {
                        // .pem
                        return SslContextBuilder.forServer(_config.sslPEMFile(), _config.sslKeyFile()).build(); //.sslProvider(SslProvider.JDK).build();
                    }
                } catch (Throwable t) {
                    super.warning("createSSL",
                            FormatUtils.format("Due to error a self signed certificate will be loaded. Error: %s", ExceptionUtils.getMessage(t)));
                    SelfSignedCertificate ssc = new SelfSignedCertificate();
                    return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                            .sslProvider(SslProvider.JDK).build();
                }
            } else {
                super.warning("createSSL", "Starting with Self-Signed Certificate.");
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                        .sslProvider(SslProvider.JDK).build();
            }
        } catch (Throwable t) {
            super.error("createSSL", t);
        }
        return null;
    }

    private void run() {
        try {

            // channel handlers
            if (_channel_handlers.size() > 0) {
                for (final ChannelHandler handler : _channel_handlers) {
                    _bootstrap.childHandler(handler);
                }
            } else {
                // default
                _bootstrap.childHandler(new WebServerInitializer(this));
            }

            // request handlers
            /*
            if (_request_delegates.isEmpty()) {
                throw new Exception("Missing request handlers: al least one handler is required!");
            }
            */

            // start listening on port
            if (_config.portAutodetect()) {
                boolean exit = false;
                int count = 0;
                while (!exit) {
                    try {
                        if (count < _config.portDetectTry()) {
                            // error if port is busy
                            _channels.add(_bootstrap.bind(_config.port()).sync().channel());
                        }
                        exit = true; // no error if here
                    } catch (Throwable t) {
                        // next port
                        _config.port(_config.port() + 1);
                    }
                    count++;
                }
            } else {
                _channels.add(_bootstrap.bind(_config.port()).sync().channel());
            }

            if (_channels.size() > 0) {
                // server started
                super.logger().info("Opened Web Channel: " + _config.toString());
            } else {
                // server not started

            }

        } catch (Throwable t) {
            super.error("run", t);
        }
    }

    private ChannelFuture sync() throws InterruptedException {
        try {
            if (_channels.size() > 0) {
                return _channels.get(0).closeFuture().sync();
            } else {
                return null;
            }
        } finally {
            _bossGroup.shutdownGracefully();
            _workerGroup.shutdownGracefully();
        }
    }

    private void close() {
        try {
            for (final Channel channel : _channels) {
                channel.close();
            }
        } finally {
            _bossGroup.shutdownGracefully();
            _workerGroup.shutdownGracefully();
        }
    }

}
