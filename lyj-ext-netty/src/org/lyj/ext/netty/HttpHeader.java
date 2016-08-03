package org.lyj.ext.netty;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Header wrapper .
 * <p>
 * ALL VALUES ARE STORED LOWER CASE
 */
public class HttpHeader {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, String> _headers;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpHeader() {
        _headers = new HashMap<>();
    }

    @Override
    public String toString() {
        return _headers.toString();
    }

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    public Set<String> keys() {
        return _headers.keySet();
    }

    public boolean isEmpty() {
        return _headers.isEmpty();
    }

    public int size() {
        return _headers.size();
    }

    public HttpHeader add(final String name, final String value) {
        _headers.put(name.toLowerCase(), value);
        return this;
    }

    public String get(final String name) {
        return _headers.get(name.toLowerCase());
    }

    public HttpHeader ContentType(final String value) {
        this.add(HttpHeaderNames.CONTENT_TYPE.toString(), value);
        return this;
    }

    public String ContentType() {
        return this.get(HttpHeaderNames.CONTENT_TYPE.toString());
    }

    public String ContentTypeExtension() {
        final String type = this.ContentType();
        String[] tokens = StringUtils.split(type, "/");
        if (tokens.length == 2) {
            final String ext_enc = tokens[1];
            tokens = StringUtils.split(ext_enc, ";");
            return tokens.length > 1 ? tokens[0] : ext_enc;
        }
        return "dat";
    }

    public String ContentTypeCharset() {
        final String type = this.ContentType();
        String[] tokens = StringUtils.split(type, "/");
        if (tokens.length == 2) {
            final String ext_enc = tokens[1];
            tokens = StringUtils.split(ext_enc, ";");
            if (tokens.length > 1) {
                final String charset_tokens = tokens[1]; // "charset=UTF-8"
                tokens = StringUtils.split(ext_enc, "=");
                return tokens.length > 1 ? tokens[1] : "";
            }
        }
        return "";
    }

    public HttpHeader ContentLength(final long value) {
        this.add(HttpHeaderNames.CONTENT_LENGTH.toString(), value + "");
        return this;
    }

    public long ContentLength() {
        return ConversionUtils.toLong(this.get(HttpHeaderNames.CONTENT_LENGTH.toString()));
    }

    public HttpHeader ContentLanguage(final String value) {
        this.add(HttpHeaderNames.CONTENT_LANGUAGE.toString(), value);
        return this;
    }

    public String ContentLanguage() {
        return this.get(HttpHeaderNames.CONTENT_LANGUAGE.toString());
    }

    public HttpHeader Connection(final String value) {
        this.add(HttpHeaderNames.CONNECTION.toString(), value);
        return this;
    }

    public String Connection() {
        return this.get(HttpHeaderNames.CONNECTION.toString());
    }

    public boolean isKeepAlive() {
        return this.get(HttpHeaderNames.CONNECTION.toString()).equals(HttpHeaderValues.KEEP_ALIVE.toString());
    }

    public boolean isImage() {
        return this.ContentType().startsWith("image");
    }

    public boolean isText() {
        return this.ContentType().startsWith("text");
    }


}
