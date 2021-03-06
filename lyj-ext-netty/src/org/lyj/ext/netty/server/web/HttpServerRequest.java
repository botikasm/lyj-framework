package org.lyj.ext.netty.server.web;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.lyj.ext.netty.server.web.controllers.CacheController;
import org.lyj.ext.netty.server.web.controllers.HttpServerRequestContext;

import java.io.File;
import java.util.*;

/**
 * The request.
 * request is generated from an HttpServerContext
 */
public class HttpServerRequest {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerRequestContext _context;
    private final HttpServerConfig _config;
    private final CacheController _cache;
    private final Map<String, String> _headers;
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServerRequest(final HttpServerRequestContext context) {
        _context = context;
        _config = _context.config();

        _cache = new CacheController(_config);
        _headers = new HashMap<>();

        this.init(context);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Map<String, String> headers() {
        return _headers;
    }

    public HttpServerConfig config() {
        return _config;
    }

    public HttpServerRequestContext context() {
        return _context;
    }

    public HttpRequest nativeHttpRequest() {
        return _context.nativeHttpRequest();
    }

    public boolean isHttpRequest() {
        return _context.hasHttpRequest();
    }

    public boolean isHttpContent() {
        return _context.hasHttpContent();
    }

    public HttpContent nativeHttpContent() {
        return _context.nativeHttpContent();
    }

    public HttpPostRequestDecoder createDecoder(final HttpDataFactory factory) {
        if (this.isHttpRequest()) {
            return new HttpPostRequestDecoder(factory, _context.nativeHttpRequest());
        }
        return null;
    }

    public boolean isTransferEncodingChunked() {
        if (this.isHttpRequest()) {
            return HttpUtil.isTransferEncodingChunked(_context.nativeHttpRequest());
        }
        return false;
    }

    public String uuid() {
        return _context.uuid();
    }

    public String host() {
        return _context.host();
    }

    public String method() {
        return _context.method();
    }

    public HttpVersion protocolVersion() {
        return _context.protocolVersion();
    }

    public String uri() {
        return _context.uri();
    }

    public boolean keepAlive() {
        return _context.keepAlive();
    }

    public int contentLength() {
        return _context.contentLength();
    }

    public int cacheControl() {
        return _context.cacheControl();
    }

    public Set<String> headerNames() {
        return _context.headerNames();
    }

    public String headerValue(final String name) {
        return _context.headerValue(name);
    }

    public Set<String> trailingHeaderNames() {
        try {
            return _context.nativeLastHttpContent().trailingHeaders().names();
        } catch (Throwable t) {
            return new HashSet<>();
        }
    }

    public String trailingHeaderValue(final String name) {
        try {
            return _context.nativeLastHttpContent().trailingHeaders().get(name);
        } catch (Throwable t) {
            return "";
        }
    }

    public List<String> trailingHeaderAll(final String name) {
        try {
            return _context.nativeLastHttpContent().trailingHeaders().getAll(name);
        } catch (Throwable t) {
            return new ArrayList<>();
        }
    }

    public boolean isANDROID() {
        final String user_agent = this.headerValue(IHeaderNames.USER_AGENT).toLowerCase();
        return user_agent.contains("android");
    }

    public boolean isIOS() {
        final String user_agent = this.headerValue(IHeaderNames.USER_AGENT).toLowerCase();
        return user_agent.contains("ipad") || user_agent.contains("iphone") || user_agent.contains("ipod");
    }

    public Set<String> paramNames() {
        final HashSet<String> result = new HashSet<>();
        result.addAll(_context.queryParams().keySet());
        result.addAll(_context.form().keySet());

        return result;
    }

    public Map<String, Object> params() {
        final HashMap<String, Object> result = new HashMap<>();
        result.putAll(_context.queryParams());
        result.putAll(_context.form());

        return result;
    }

    public Map<String, List<String>> queryParams() {
        return _context.queryParams();
    }

    public List<String> queryParam(final String paramName) {
        return _context.queryParam(paramName);
    }

    public Map<String, String> bodyParams() {
        return _context.form();
    }

    public boolean hasLastHttpContent() {
        return _context.hasLastHttpContent();
    }

    public LastHttpContent lastHttpContent() {
        return _context.nativeLastHttpContent();
    }

    public boolean statusIsSuccess() {
        return _context.isSuccess();
    }

    public boolean statusIsFailure() {
        return _context.isFailure();
    }

    public boolean statusIsFinished() {
        return _context.isFinished();
    }

    public Throwable statusFailureCause() {
        return _context.failureCause();
    }

    public String statusFailureCauseMessage() {
        if (null != _context && null != _context.failureCause()) {
            return _context.failureCause().toString();
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      f i l e
    // ------------------------------------------------------------------------

    public boolean isModifiedSince(final File file) {
        return _cache.isModifiedSince(_context.nativeHttpRequest(), file);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final HttpServerRequestContext context) {
        try {
            if (null != context) {
                final Set<String> names = context.headerNames();
                for (final String name : names) {
                    _headers.put(name, context.headerValue(name));
                }
            }
        } catch (Throwable ignored) {
            // ignored
        }
    }


}
