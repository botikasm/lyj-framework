package org.lyj.ext.netty.server.web.controllers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.utils.ResponseUtil;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Request Wrapper
 */
public class HttpServerRequestContext {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _uuid;
    private final HttpServerConfig _config;
    private final String _encoding;

    private ChannelHandlerContext _native_context;
    private Object _native_message;
    private HttpRequest _request;
    private HttpContent _content;
    private ByteBuf _content_buffer;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServerRequestContext(final HttpServerConfig config) {
        _config = config;
        _uuid = RandomUtils.randomUUID();
        _encoding = _config.encoding();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpServerRequestContext handle(final ChannelHandlerContext ctx,
                                           final HttpObject data) {
        _native_message = data;
        _native_context = ctx;
        if (data instanceof HttpRequest) {
            _request = (HttpRequest) data;
            if (HttpUtil.is100ContinueExpected(_request)) {
                ResponseUtil.send100Continue(ctx);
            }
        }
        if (data instanceof HttpContent) {
            _content = (HttpContent) data;
            _content_buffer = _content.content();
        }

        return this;
    }

    public HttpServerConfig config() {
        return _config;
    }

    public String encoding() {
        return _encoding;
    }

    public Charset charset() {
        return Charset.forName(_encoding);
    }

    public boolean hasHttpRequest() {
        return null != _request;
    }

    public boolean hasHttpContent() {
        return null != _content;
    }

    public boolean hasLastHttpContent() {
        return _content instanceof LastHttpContent;
    }

    public HttpRequest nativeHttpRequest() {
        return _request;
    }

    public HttpContent nativeHttpContent() {
        return _content;
    }

    public LastHttpContent nativeLastHttpContent() {
        return this.hasLastHttpContent() ? (LastHttpContent) _content : null;
    }

    public Channel nativeChannel() {
        return null != _native_context ? _native_context.channel() : null;
    }

    public String method() {
        try {
            return _request.method().name().toUpperCase();
        } catch (Throwable ignored) {
            return "";
        }
    }

    public HttpResponseStatus status() {
        try {
            return _request.decoderResult().isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST;
        } catch (Throwable t) {
            return HttpResponseStatus.OK;
        }
    }

    public boolean isSuccess() {
        try {
            return _request.decoderResult().isSuccess();
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean isFailure() {
        try {
            return _request.decoderResult().isFailure();
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean isFinished() {
        try {
            return _request.decoderResult().isFinished();
        } catch (Throwable t) {
            return false;
        }
    }

    public Throwable failureCause() {
        try {
            return _request.decoderResult().cause();
        } catch (Throwable ignored) {
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String uuid() {
        return _uuid;
    }

    public String host() {
        if (null != _request && !_request.headers().isEmpty()) {
            return _request.headers().get(HttpHeaderNames.HOST, "unknown");
        }
        return "";
    }

    public HttpVersion protocolVersion() {
        if (null != _request) {
            return _request.protocolVersion();
        }
        return HttpVersion.HTTP_1_0;
    }

    public String uri() {
        if (null != _request) {
            return _request.uri();
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      h e a d e r s
    // ------------------------------------------------------------------------

    public Set<String> headerNames() {
        if (null != _request && !_request.headers().isEmpty()) {
            return _request.headers().names();
        }
        return new HashSet<>();
    }

    public String headerValue(final String name) {
        if (null != _request && !_request.headers().isEmpty()) {
            return _request.headers().get(name);
        }
        return "";
    }

    public QueryStringDecoder query() {
        if (null != _request) {
            return new QueryStringDecoder(_request.uri());
        }
        return new QueryStringDecoder("");
    }

    public Map<String, List<String>> queryParams() {
        if (null != _request) {
            return query().parameters();
        }
        return new HashMap<>();
    }

    public List<String> queryParam(final String paramName) {
        if (null != _request) {
            return query().parameters().get(paramName);
        }
        return new ArrayList<>();
    }

    /**
     * HEADER: Connection = keep-alive
     */
    public boolean keepAlive() {
        String value = headerValue("Connection");
        if (!StringUtils.hasText(value)) {
            value = headerValue("connection");
            if (!StringUtils.hasText(value)) {
                value = headerValue("CONNECTION");
            } else {
                value = "";
            }
        }
        return null != value ? value.toLowerCase().equals("keep-alive") : false;
    }

    /**
     * HEADER: Cache-Control = max-age=0
     */
    public int cacheControl() {
        String value = headerValue("Cache-Control");
        if (!StringUtils.hasText(value)) {
            value = headerValue("cache-control");
            if (!StringUtils.hasText(value)) {
                value = headerValue("CACHE-CONTROL");
            } else {
                value = "";
            }
        }
        if (StringUtils.hasText(value)) {
            final String[] tokens = StringUtils.split(value, "=", true);
            if (tokens.length == 2) {
                return ConversionUtils.toInteger(tokens[1]);
            }
        }
        return 0;
    }

    /**
     * HEADER: Content-Length = 1234
     */
    public int contentLength() {
        String value = headerValue("Content-Length");
        if (!StringUtils.hasText(value)) {
            value = headerValue("content-length");
            if (!StringUtils.hasText(value)) {
                value = headerValue("CONTENT-LENGHT");
            } else {
                value = "";
            }
        }

        return ConversionUtils.toInteger(value);
    }

    // ------------------------------------------------------------------------
    //                      c o n t e n t
    // ------------------------------------------------------------------------

    public Map<String, String> form() {
        final Map<String, String> result = new LinkedHashMap<>();
        if (null != _content_buffer && _content_buffer.isReadable()) {
            final String content = _content_buffer.toString(charset());
            try {
                if (StringUtils.isJSONObject(content)) {
                    final Map<String, Object> map = new JsonWrapper(content).toMap();
                    for (final Map.Entry<String, Object> entry : map.entrySet()) {
                        result.put(entry.getKey(), StringUtils.toString(entry.getValue()));
                    }
                } else {
                    final String[] tokens = StringUtils.split(content, "&", true);
                    for (final String token : tokens) {
                        final String[] keyvalue = StringUtils.split(token, "=", true);
                        if (keyvalue.length == 2) {
                            final String key = URLDecoder.decode(keyvalue[0], _encoding);
                            final String value = URLDecoder.decode(keyvalue[1], _encoding);
                            result.put(key, value);
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        return result;
    }

    // ------------------------------------------------------------------------
    //                      channel context
    // ------------------------------------------------------------------------

    public ChannelFuture write(final Object msg) {
        return _native_context.write(msg);
    }

    public ChannelFuture write(final Object msg, final ChannelPromise promise) {
        return _native_context.write(msg, promise);
    }

    public ChannelFuture writeAndFlush(final Object msg) {
        return _native_context.writeAndFlush(msg);
    }

    public ChannelFuture writeAndFlush(final Object msg, final ChannelPromise promise) {
        return _native_context.writeAndFlush(msg, promise);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    public ChannelHandlerContext context() {
        return _native_context;
    }

    protected void context(final ChannelHandlerContext value) {
        _native_context = value;
    }

    public Object message() {
        return _native_message;
    }

    protected void message(final Object value) {
        _native_message = value;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
