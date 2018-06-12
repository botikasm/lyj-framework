package org.lyj.ext.netty.server.websocket.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.NotSslRecordException;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.websocket.impl.sessions.SessionClientController;

public class WebSocketServerHandler
        extends SimpleChannelInboundHandler<Object> {

    // --------------------------------------------------------------------
    //               const
    // --------------------------------------------------------------------

    private final String _uuid;
    private final Logger _logger;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public WebSocketServerHandler(final HttpServer server) {
        _uuid = RandomUtils.randomUUID();
        _logger = LoggingUtils.getLogger(this);
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        // client added (before channelRead0)
        SessionClientController.instance().open(_uuid).add(ctx.channel());
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final Object msg) {
        // read message
        SessionClientController.instance().open(_uuid).read(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        // client unregistered (before handlerRemoved)
    }

    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        // client removed
        SessionClientController.instance().close(_uuid, ctx.channel());
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if(cause instanceof NotSslRecordException){
            // request is not SSL
        } else {
            _logger.debug(FormatUtils.format("[%s.%s]: %s", this.getClass().getName(), "exceptionCaught", cause.toString()));
        }
        ctx.close();
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


}
