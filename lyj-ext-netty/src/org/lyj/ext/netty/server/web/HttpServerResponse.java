package org.lyj.ext.netty.server.web;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.network.http.client.HttpBuffer;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.MimeTypeUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.controllers.CacheController;
import org.lyj.ext.netty.server.web.controllers.HttpServerRequestContext;
import org.lyj.ext.netty.server.web.utils.CookieUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 *
 */
public class HttpServerResponse
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerRequestContext _context;
    private final HttpServerConfig _config;
    private final CacheController _cache;
    private final HttpBuffer _buffer;
    private final Map<String, String> _headers;

    private boolean _handled; // when true chain handlers is interrupted

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServerResponse(final HttpServerRequestContext context) {
        _context = context;
        _config = context.config();

        _cache = new CacheController(_config);
        _buffer = new HttpBuffer(_config.encoding());
        _headers = new HashMap<>();

        _handled = false;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpServerConfig config() {
        return _config;
    }

    public HttpBuffer buffer() {
        return _buffer;
    }

    public Map<String, String> headers() {
        return _headers;
    }

    public HttpServerResponse write(final String text) {
        _buffer.write(text);
        return this;
    }

    /**
     * Write and flush everything from buffer to response.
     * Close connection if not 'keep-alive'.
     * Set response as 'handled': no further chain handlers will be invoked.
     */
    public void flush() {
        this.writeAndFlushBuffer();
        this.handled(true);
    }

    public HttpServerResponse handled(final boolean value) {
        _handled = value;
        return this;
    }

    public boolean handled() {
        return _handled;
    }

    // ------------------------------------------------------------------------
    //                      h e a d e r s
    // ------------------------------------------------------------------------

    public void setContentTypeHeader(final File file) {
        final String mime_type = MimeTypeUtils.getMimeType(file.getPath());
        _headers.put(IHeaderNames.CONTENT_TYPE, mime_type);
    }

    public String getContentTypeHeader() {
        return _headers.get(IHeaderNames.CONTENT_TYPE);
    }

    public void setContentLength(final File file) {
        final long length = file.length();
        _headers.put(IHeaderNames.CONTENT_LENGTH, length + "");
    }

    public void removeContentLength() {
        _headers.remove(IHeaderNames.CONTENT_LENGTH);
    }

    public boolean isContentLengthSet() {
        return _headers.containsKey(IHeaderNames.CONTENT_LENGTH);
    }

    // ------------------------------------------------------------------------
    //                      f i l e s
    // ------------------------------------------------------------------------

    public void writeFile(final File file) {
        if (file.exists()) {
            this.setContentTypeHeader(file);
            this.setContentLength(file);

            try {
                if (this.getContentTypeHeader().startsWith("text")) {
                    final String text = FileUtils.readFileToString(file, this.config().encoding());
                    _buffer.write(text);
                } else {
                    final byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
                    _buffer.write(bytes);
                }
            } catch (Throwable t) {
                this.writeErrorINTERNAL_SERVER_ERROR(t);
            }
        } else {
            this.writeErrorNOT_FOUND();
        }
    }

    // ------------------------------------------------------------------------
    //                      e r r o r s
    // ------------------------------------------------------------------------

    public void writeErrorFORBIDDEN() {
        this.writeError(HttpResponseStatus.FORBIDDEN);
    }

    public void writeErrorNOT_FOUND() {
        final String not_found_404 = this.config().uri(this.config().notFound404());
        if (StringUtils.hasText(not_found_404)) {
            this.writeRedirect(not_found_404);
        } else {
            this.writeError(HttpResponseStatus.NOT_FOUND);
        }
    }

    public void writeOK() {
        this.writeStatus(HttpResponseStatus.OK);
    }

    public void writeErrorINTERNAL_SERVER_ERROR() {
        this.writeError(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public void writeErrorINTERNAL_SERVER_ERROR(final Throwable cause) {
        this.writeError(HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.toString());
    }

    // ------------------------------------------------------------------------
    //                      s p e c i a l
    // ------------------------------------------------------------------------

    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
     */
    public void writeNotModified() {
        final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
        _cache.setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        _context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public void writeRedirect(final String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);

        // Close the connection as soon as the error message is sent.
        _context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void writeError(final HttpResponseStatus status) {
        this.writeError(status, "");
    }

    private void writeError(final HttpResponseStatus status, final String content) {
        final FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", _context.charset()));

        _headers.put(CONTENT_TYPE.toString(), "text/plain; charset=UTF-8");

        _buffer.clear();
        this.removeContentLength();

        if (StringUtils.hasText(content)) {
            _buffer.write(content);
        }

        // Close the connection as soon as the error message is sent.
        _context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void writeStatus(final HttpResponseStatus status) {
        this.writeStatus(status, null);
    }

    private void writeStatus(final HttpResponseStatus status,
                             final String content) {
        // reset buffer
        _buffer.clear();
        this.removeContentLength();

        final FullHttpResponse response;
        if(StringUtils.hasText(content)){
            response = new DefaultFullHttpResponse( HTTP_1_1, status, Unpooled.copiedBuffer(content.getBytes()) );
            _headers.put(CONTENT_TYPE.toString(), "text/plain; charset=UTF-8");
            _buffer.write(content);
        } else {
            response = new DefaultFullHttpResponse( HTTP_1_1, status );
        }

        // Close the connection as soon as the error message is sent.
        _context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    private void writeAndFlushBuffer() {

        final boolean keep_alive = _context.keepAlive();

        // Build the response object.
        final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, _context.status(),
                Unpooled.copiedBuffer(_buffer.readBytes()));

        if (keep_alive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            if (!this.isContentLengthSet()) {
                _headers.put(CONTENT_LENGTH.toString(), response.content().readableBytes() + "");
            }
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            _headers.put(CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
        }

        // add headers
        for (final Map.Entry<String, String> e : _headers.entrySet()) {
            final String name = e.getKey();
            final String value = e.getValue();
            response.headers().set(name, value);
        }

        // Encode the cookie.
        CookieUtil.encodeCookies(_context.nativeHttpRequest(), response);

        // Write the response.
        _context.write(response);

        // should close connection with client?
        if (!keep_alive) {
            _context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }



}
