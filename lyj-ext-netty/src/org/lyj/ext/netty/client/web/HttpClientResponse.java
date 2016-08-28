package org.lyj.ext.netty.client.web;

import org.lyj.commons.logging.AbstractLogEmitter;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Response
 */
public class HttpClientResponse
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpClientInfo _info;

    private ByteArrayOutputStream __data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClientResponse(final HttpClientInfo info) {
        _info = info;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpClientResponse content(final String value) {
        try {
            return this.content(value.getBytes(_info.encoding()));
        } catch (Throwable ignored) {
            return this.content(value.getBytes());
        }
    }

    public HttpClientResponse content(final byte[] value) {
        this.data().write(value, 0, value.length);
        return this;
    }

    public byte[] content() {
        try {
            if (null != __data) {
                return __data.toByteArray();
            }
        } catch (Throwable t) {
            super.error("content", t);
        }
        return new byte[0];
    }

    public String contendAsString() {
        final byte[] content = this.content();
        try {
            return new String(content, 0, content.length, _info.encoding());
        } catch (UnsupportedEncodingException e) {
            return new String(content);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private ByteArrayOutputStream data() {
        if (null == __data) {
            __data = new ByteArrayOutputStream();
        }
        return __data;
    }


}
