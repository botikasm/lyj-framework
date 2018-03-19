package org.ly.commons.network.socket.basic.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SocketMessageReader
        extends ByteArrayOutputStream {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    @Override
    public synchronized void write(final int b) {
        super.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        super.write(b, off, len);
    }

    @Override
    public synchronized void writeTo(OutputStream out) throws IOException {
        super.writeTo(out);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public synchronized boolean isComplete() {
        try {
            final byte[] bytes = super.toByteArray();
            return isComplete(bytes);
        } catch (Throwable t) {
            //System.out.println(t);
        }
        return false;
    }

    public synchronized SocketMessage message() {
        if (this.isComplete()) {
            return new SocketMessage(super.toByteArray());
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
