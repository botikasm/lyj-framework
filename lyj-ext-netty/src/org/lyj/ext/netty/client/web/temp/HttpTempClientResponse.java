package org.lyj.ext.netty.client.web.temp;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.HttpHeader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * The response.
 */
public class HttpTempClientResponse
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _content_file_name;
    private final ByteArrayOutputStream _data;
    private final String _encoding;

    private final HttpHeader _headers;

    private boolean _is_chunked;
    private HttpVersion _version;
    private HttpResponseStatus _status;

    private boolean _handled;
    private Throwable _store_content_error;
    private long _byte_count;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpTempClientResponse(final String contentFileName,
                                  final String encoding) {
        _content_file_name = contentFileName;
        _headers = new HttpHeader();
        _is_chunked = false;
        _handled = false;
        _byte_count = 0;
        _encoding = encoding;

        if (StringUtils.hasText(contentFileName)) {
            _data = null;
        } else {
            _data = new ByteArrayOutputStream();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("status:").append(_status.toString());
        sb.append(", ");
        sb.append("version:").append(_version.toString());
        sb.append(", ");
        sb.append("chunked:").append(_is_chunked);
        sb.append(", ");
        sb.append("error: ").append(null != _store_content_error ? _store_content_error : "");
        sb.append(", ");
        sb.append("byte_count: ").append(_byte_count);
        sb.append(", ");
        sb.append("content_lenght: ").append(_headers.ContentLength());
        sb.append(", ");
        sb.append("content_type: ").append(_headers.ContentType());
        sb.append(", ");
        sb.append("content_storage: ").append(null != _data ? _data.getClass().getSimpleName() : File.class.getSimpleName());
        sb.append(", ");
        sb.append("headers: ").append(_headers.toString());

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void handle(final HttpObject msg) {
        if (msg instanceof HttpResponse) {
            this.handle((HttpResponse) msg);
        }
        if (msg instanceof HttpContent) {
            this.handle((HttpContent) msg);
        }
    }

    public boolean handled() {
        return _handled;
    }

    public byte[] content() {
        try {
            if (null != _data) {
                return _data.toByteArray();
            } else if (FileUtils.exists(_content_file_name)) {
                return FileUtils.copyToByteArray(new File(_content_file_name));
            }
        } catch (Throwable t) {
            super.error("content", t);
        }
        return new byte[0];
    }

    public HttpHeader headers() {
        return _headers;
    }

    public int statusCode() {
        return null != _status ? _status.code() : 0;
    }

    public String statusMessage() {
        return null != _status ? _status.reasonPhrase() : "";
    }

    public boolean isRedirect() {
        final int code = this.statusCode();
        return code == 300
                || code == 301
                || code == 302
                || code == 303
                || code == 305
                || code == 307;
    }

    public String getRedirectUrl() {
        try {
            if (this.isRedirect()) {
                return null != _headers ? URLDecoder.decode(_headers.get(HttpHeaderNames.LOCATION.toString()), _encoding) : "";
            }
        } catch (Throwable ignored) {

        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void onContent(final byte[] data) {
        try {
            if (data.length > 0) {
                if (null != _data) {
                    // write in buffer
                    _data.write(data, 0, data.length);
                } else {
                    // write in file
                    FileUtils.append(_content_file_name, data);
                }
            }
        } catch (Throwable t) {
            if (null == _store_content_error) {
                super.error("onContent", t);
            }
            _store_content_error = t;
        }
    }

    private void handle(final HttpResponse response) {
        _status = response.status();
        _version = response.protocolVersion();

        if (!response.headers().isEmpty()) {
            for (String name : response.headers().names()) {
                for (String value : response.headers().getAll(name)) {
                    _headers.add(name, value);
                }
            }
        }

        _is_chunked = HttpUtil.isTransferEncodingChunked(response);

    }

    private void handle(final HttpContent content) {
        try {

            final byte[] bytes = this.readBytes(content.content());
            _byte_count += content.content().capacity();

            this.onContent(bytes);

        } catch (Throwable t) {
            super.error("handle", t);
        }

        if (content instanceof LastHttpContent) {
            _handled = true;
        }
    }

    private byte[] readBytes(final ByteBuf buf) {
        if (buf.isReadable()) {
            final byte[] response = new byte[buf.readableBytes()];
            buf.readBytes(response);
            return response;
        }
        return new byte[0];
    }


}
