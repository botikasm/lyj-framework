/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.lyj.ext.netty.server.web.base.samples.sample;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.lyj.ext.netty.server.web.HttpServer;

public class HttpSnoopServerInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpServer _server;

    public HttpSnoopServerInitializer(final HttpServer server) {
        _server = server;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        final ChannelPipeline p = ch.pipeline();

        // SSL
        if (_server.ssl() != null) {
            p.addLast(_server.ssl().newHandler(ch.alloc()));
        }

        // DECODER
        p.addLast(new HttpRequestDecoder());

        // CHUNKS: Uncomment the following line if you don't want to handle HttpChunks.
        p.addLast(new HttpObjectAggregator(_server.config().maxChunkSize()));

        // ENCODER
        p.addLast(new HttpResponseEncoder());

        // COMPRESSOR
        if(_server.config().useCompression()) {
            p.addLast(new HttpContentCompressor());
        }

        // FINAL HANDLER
        p.addLast(new HttpSnoopServerHandler());
    }
}
