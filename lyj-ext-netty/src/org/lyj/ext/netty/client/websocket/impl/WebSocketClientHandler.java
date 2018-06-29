package org.lyj.ext.netty.client.websocket.impl;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.lyj.commons.Delegates;

public class WebSocketClientHandler
        extends SimpleChannelInboundHandler<Object> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final WebSocketClientHandshaker _handshaker;
    private ChannelPromise handshakeFuture;

    // callbacks
    private Delegates.Callback<String> _text_callback;
    private Delegates.Handler _pong_callback;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebSocketClientHandler(final WebSocketClientHandshaker handshaker) {
        _handshaker = handshaker;
    }

    // ------------------------------------------------------------------------
    //                      h a n d l e r s
    // ------------------------------------------------------------------------

    public void onReceiveText(final Delegates.Callback<String> callback) {
        _text_callback = callback;
    }

    public void onReceivePong(final Delegates.Handler callback) {
        _pong_callback = callback;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        _handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,
                             final Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!_handshaker.isHandshakeComplete()) {
            try {
                _handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                System.out.println("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                System.out.println("WebSocket Client failed to connect");
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.getStatus() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            if (null != _text_callback) {
                Delegates.invoke(_text_callback, textFrame.text());
            } else {
                System.out.println("WebSocket Client received message: " + textFrame.text());
            }
        } else if (frame instanceof PongWebSocketFrame) {
            if (null != _pong_callback) {
                Delegates.invoke(_pong_callback);
            } else {
                System.out.println("WebSocket Client received pong");
            }
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket Client received closing");
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
