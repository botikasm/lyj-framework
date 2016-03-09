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

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class WebServer extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _ssl;
    private int _port;

    private final List<Channel> _channels;
    private EventLoopGroup _bossGroup;
    private EventLoopGroup _workerGroup;
    private SslContext _sslCtx;
    private ServerBootstrap _bootstrap;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebServer() {
        _ssl = false;
        _port = 4000;

        _channels = new ArrayList<>();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public WebServer start() {
        this.run();
        return this;
    }

    public WebServer stop() {
        this.close();
        return this;
    }

    public ChannelFuture join() throws InterruptedException {
        return this.sync();
    }

    public SslContext ssl(){
        return _sslCtx;
    }

    public WebServer handler(final ChannelHandler handler){
        _bootstrap.childHandler(handler);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        try {
            // Configure SSL.
            if (_ssl) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                _sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                        .sslProvider(SslProvider.JDK).build();
            } else {
                _sslCtx = null;
            }

            _bossGroup = new NioEventLoopGroup(1);
            _workerGroup = new NioEventLoopGroup();

            _bootstrap = new ServerBootstrap();
            _bootstrap.group(_bossGroup, _workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO));

        } catch (Throwable t) {
            super.error("init", t);
        }
    }

    private void run() {
        try {

            _channels.add(_bootstrap.bind(_port).sync().channel());

            super.logger().info("Open your web browser and navigate to " +
                    (_ssl ? "https" : "http") + "://127.0.0.1:" + _port + '/');

        } catch (Throwable t) {
            super.error("run", t);
        }
    }

    private ChannelFuture sync() throws InterruptedException {
        try {
            return _channels.get(0).closeFuture().sync();
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
