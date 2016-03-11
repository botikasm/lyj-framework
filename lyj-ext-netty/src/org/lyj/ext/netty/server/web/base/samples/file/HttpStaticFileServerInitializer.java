package org.lyj.ext.netty.server.web.base.samples.file;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.lyj.ext.netty.server.web.HttpServer;

public class HttpStaticFileServerInitializer
        extends ChannelInitializer<SocketChannel> {

    private final HttpServer _server;

    public HttpStaticFileServerInitializer(final HttpServer server) {
        _server = server;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (_server.ssl() != null) {
            pipeline.addLast(_server.ssl().newHandler(ch.alloc()));
        }
        pipeline.addLast("codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(_server.config().maxChunkSize()));
        pipeline.addLast("chunk-handler", new ChunkedWriteHandler());
        pipeline.addLast("directory-list", new HttpStaticFileServerHandler());
    }
}
