package org.lyj.ext.netty.server.websocket.impl.sessions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.websocket.Response;
import org.lyj.ext.netty.server.websocket.impl.service.MessageService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Remote Client
 */
public class SessionClient {

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final String _uid;
    private final HttpServerConfig _config;
    private final List<Delegates.CallbackEntry<String, Object>> _listeners;
    private final ChannelGroup _channel_group;

    private WebSocketServerHandshaker _handshaker;

    private boolean _initialized;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public SessionClient(final String uid,
                         final HttpServerConfig config,
                         final List<Delegates.CallbackEntry<String, Object>> listeners) {
        _uid = uid;
        _config = config;
        _listeners = listeners;
        _channel_group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        _initialized = false;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String uid() {
        return _uid;
    }

    public void add(final Channel channel) {
        _channel_group.add(channel);
    }

    public void remove(final Channel channel) {
        _channel_group.remove(channel);
    }

    public void close() {
        _channel_group.close();
        _channel_group.clear();
    }

    public String address() {
        final Channel channel = this.channel(0);
        return null != channel ? channel.remoteAddress().toString() : "";
    }

    public void read(final ChannelHandlerContext ctx, final Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private List<Channel> channels() {
        final List<Channel> channels = new LinkedList<>();
        _channel_group.forEach(channels::add);
        return channels;
    }

    private Channel channel(final int idx) {
        return CollectionUtils.get(this.channels(), idx);
    }

    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest req) {
        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (req.method() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        if ("/favicon.ico".equals(req.uri()) || ("/".equals(req.uri()))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        // parse params and throw exception if
        try {
            this.parseParams(req);
        } catch (Throwable t) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        // Handshake
        _handshaker = this.handshaker(req);
        if (_handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            final ChannelFuture channelFuture = _handshaker.handshake(ctx.channel(), req);

            if (channelFuture.isSuccess()) {
                // ok, ready
                _initialized = true;
            }
        }
    }

    private void handleWebSocketFrame(final ChannelHandlerContext ctx, final WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            _handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        // handle supported frames
        if ((frame instanceof TextWebSocketFrame) || (frame instanceof BinaryWebSocketFrame)) {
            if (!_initialized) {
                this.sendBackError(ctx);
            } else {

                this.notifyListeners(frame);

                broadcast(ctx, frame);
            }
        }

        throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
    }

    private void notifyListeners(final WebSocketFrame frame) {
        if (null != _listeners && !_listeners.isEmpty()) {
            _listeners.forEach((callback) -> {
                try {
                    callback.handle(_uid, content(frame));
                } catch (Throwable ignored) {
                }
            });
        }
    }

    private static Object content(final WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            return ((TextWebSocketFrame) frame).text();
        } else {
            return frame.content().array();
        }
    }

    private void sendBackError(final ChannelHandlerContext ctx) {
        Response response = new Response(1001, "ERROR: CHANNEL NOT INITIALIZED");
        String msg = new JSONObject(response).toString();
        ctx.channel().write(new TextWebSocketFrame(msg));
    }

    private void broadcast(final ChannelHandlerContext ctx, final WebSocketFrame frame) {

        String request = ((TextWebSocketFrame) frame).text();
        System.out.println(" CHANNEL " + ctx.channel() + request);

        Response response = MessageService.sendMessage(_uid, request);
        String msg = new JSONObject(response).toString();

        _channel_group.writeAndFlush(new TextWebSocketFrame(msg));
    }


    private String getWebSocketLocation(FullHttpRequest req) {
        final String location = PathUtils.concat(req.headers().get(HOST), _config.websocketPath());
        return "ws://" + location;
    }

    private WebSocketServerHandshaker handshaker(final FullHttpRequest request) {
        final WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request), null, true);
        return factory.newHandshaker(request);
    }

    private void parseParams(final FullHttpRequest req) throws Exception {
        final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
        final Map<String, List<String>> parameters = queryStringDecoder.parameters();

        /*
        if (parameters.size() == 0 || !parameters.containsKey(HTTP_REQUEST_STRING)) {
            System.err.printf(HTTP_REQUEST_STRING + "INVALID REQUEST");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }
        */

        // throw exception if missing token param
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static void sendHttpResponse(ChannelHandlerContext ctx, final FullHttpRequest req, final FullHttpResponse res) {
        if (res.status().code() != 200) {
            final ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        final ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}


