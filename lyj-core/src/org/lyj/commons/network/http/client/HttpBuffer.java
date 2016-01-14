package org.lyj.commons.network.http.client;

import org.lyj.commons.util.ByteUtils;
import org.lyj.commons.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Buffer
 */
public class HttpBuffer {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private StringBuilder _buffer;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpBuffer() {
        _buffer = new StringBuilder();
    }

    public HttpBuffer(final InputStream io) throws IOException {
        this();
        this.read(io);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _buffer = null;
        } catch (Exception e) {
        }
        super.finalize();
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int size(){
        return _buffer.length();
    }

    public void clear(){
        _buffer = new StringBuilder();
    }

    public HttpBuffer write(final String data){
        _buffer.append(data);
        return this;
    }

    public String[] getStringChunks(final int chunkSize){
        return StringUtils.chunk(_buffer.toString(), chunkSize);
    }

    public String read(){
        return _buffer.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void read(final InputStream io) throws IOException {
        ByteUtils.read(io, 4096, (data)->{
            _buffer.append(new String(data));
        });
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
