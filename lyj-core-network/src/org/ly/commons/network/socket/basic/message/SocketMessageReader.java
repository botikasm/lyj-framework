package org.ly.commons.network.socket.basic.message;

import org.lyj.commons.util.ByteUtils;

import java.io.*;

/**
 * Read a message and check message integrity.
 */
public class SocketMessageReader
        implements AutoCloseable{

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final OutputStream _out;
    private final File _out_file;

    private boolean _initialized;
    private boolean _complete;
    private Exception _error;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessageReader() {
        _out = new ByteArrayOutputStream();
        _out_file = null;
    }

    /**
     * Use this constructor for very big messages you do not want to store in memory.
     * Passing an out_file parameter, the file will be created and all data will be written in file.
     */
    public SocketMessageReader(final File out_file) throws FileNotFoundException {
        _out = new FileOutputStream(out_file);
        _out_file = out_file;
    }

    // ------------------------------------------------------------------------
    //                      s t r e a m
    // ------------------------------------------------------------------------

    public byte[] toByteArray() {
        try {
            // flush buffer
            this.flush();

            if (_out instanceof ByteArrayOutputStream) {
                return ((ByteArrayOutputStream) _out).toByteArray();
            } else if (_out instanceof FileOutputStream && null != _out_file) {
                return ByteUtils.getBytes(_out_file);
            }
        } catch (Throwable ignored) {
        }
        return new byte[0];
    }

    public synchronized void write(final int b) throws IOException {
        _out.write(b);
    }

    public void write(byte[] b) throws IOException {
        _out.write(b);
    }

    public synchronized void write(byte[] b, int off, int len) throws IOException {
        _out.write(b, off, len);
    }

    public void flush() throws IOException {
        _out.flush();
    }

    public void close() throws IOException {
        _out.flush();
        _out.close();
        if (null != _out_file) {
            if (!_out_file.delete()) {
                _out_file.deleteOnExit();
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public synchronized boolean hasError() {
        return null != _error;
    }

    public synchronized String errorMessage() {
        return this.hasError() ? _error.toString() : "";
    }

    public synchronized boolean isComplete() {
        try {
            if (!_complete) {
                _initialized = true;
                final byte[] bytes = this.toByteArray();
                _complete = isComplete(bytes);
            }
        } catch (Exception e) {
            _error = e;
        }
        return _complete;
    }

    public synchronized SocketMessage message() {
        if (!this.hasError()) {
            if (!_complete && !_initialized) {
                this.isComplete();
            }
            if (_complete) {
                return new SocketMessage(this.toByteArray());
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static boolean isComplete(final byte[] bytes) {
        if (SocketMessage.hasStart(bytes)) {
            // length
            final long length = SocketMessage.decodeLength(bytes);
            if (length > -1) {
                // type
                final SocketMessage.MessageType type = SocketMessage.decodeType(bytes);
                if (!SocketMessage.MessageType.Undefined.equals(type)) {
                    // body integrity
                    if (SocketMessage.decodeBody(bytes).length == length) {
                        // message is closed
                        return SocketMessage.hasEnd(bytes);
                    }
                }
            }
        }
        return false;
    }

}
