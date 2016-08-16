package org.lyj.commons.network.http.client;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.ByteUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Buffer
 */
public class HttpBuffer {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _encoding;
    private byte[] _buffer_bytes;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpBuffer() {
        this(CharEncoding.getDefault());
    }

    public HttpBuffer(final String encoding) {
        _encoding = CharEncoding.isSupported(encoding) ? encoding : CharEncoding.getDefault();
        _buffer_bytes = new byte[0];
    }

    public HttpBuffer(final InputStream io) throws IOException {
        this(io, null);
    }

    public HttpBuffer(final InputStream io,
                      final String encoding) throws IOException {
        _buffer_bytes = new byte[0];
        _encoding = CharEncoding.isSupported(encoding) ? encoding : CharEncoding.getDefault();
        if (null != io) {
            this.read(io);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _buffer_bytes = null;
        } catch (Exception ignored) {
        }
        super.finalize();
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int size() {
        return this.readBytes().length;
    }

    public void clear() {
        _buffer_bytes = new byte[0];
    }

    public HttpBuffer write(final String data) {
        _buffer_bytes = CollectionUtils.merge(_buffer_bytes, getBytes(data, _encoding));
        return this;
    }

    public HttpBuffer write(final byte[] data) {
        _buffer_bytes = CollectionUtils.merge(_buffer_bytes, data);
        return this;
    }

    public String[] getStringChunks(final int chunkSize) {
        return StringUtils.chunk(this.read(), chunkSize);
    }

    public String read() {
        return new String(_buffer_bytes, Charset.forName(_encoding));
    }

    public byte[] readBytes() {
        return _buffer_bytes;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void read(final InputStream io) throws IOException {
        ByteUtils.read(io, 4096, this::write);
    }

    private static byte[] getBytes(final String text, final String encoding) {
        if (StringUtils.hasText(text)) {
            try {
                return text.getBytes(encoding);
            } catch (Throwable ignored) {
                return text.getBytes();
            }
        }
        return new byte[0];
    }


    public static byte[] getBytes(final InputStream is, final int bufferSize) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
        try {
            byte[] buffer = new byte[bufferSize];
            int len;

            while ((len = is.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
            }
        } finally {
            out.close();
        }
        return out.toByteArray();
    }

}
