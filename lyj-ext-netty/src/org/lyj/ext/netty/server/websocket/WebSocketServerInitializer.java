package org.lyj.ext.netty.server.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.websocket.impl.WebSocketServerHandler;


public class WebSocketServerInitializer
        extends ChannelInitializer<SocketChannel> {

    private final HttpServer _server;

    public WebSocketServerInitializer(final HttpServer server) {
        _server = server;
    }

    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());

        pipeline.addLast(new WebSocketServerHandler(_server));
    }
}
