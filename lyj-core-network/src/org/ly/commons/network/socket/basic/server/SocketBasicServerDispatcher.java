package org.ly.commons.network.socket.basic.server;

import org.ly.commons.network.socket.basic.AbstractMessageDispatcher;
import org.ly.commons.network.socket.basic.SocketSettings;
import org.ly.commons.network.socket.basic.message.chunks.ChunkManager;
import org.ly.commons.network.socket.basic.message.cipher.impl.ServerCipher;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.utils.SocketUtils;
import org.lyj.commons.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SocketBasicServerDispatcher
        extends AbstractMessageDispatcher {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicServerDispatcher() {
        super(new ServerCipher());

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public SocketMessage read(final AsynchronousSocketChannel socket,
                              final SocketSettings context) throws Exception {
        // read data
        return this.readData(socket, context);

    }

    public void write(final AsynchronousSocketChannel socket,
                      final SocketSettings context,
                      final SocketMessage message,
                      final String owner_id) throws Exception {

        if (message.bodyLength() > super.chunkSize() || message.isFile()) {

            // tokenize data into cache
            final SocketMessage download_message = ChunkManager.instance().splitToCache(message, super.chunkSize());

            // write download message data
            this.writeData(socket, context, download_message, owner_id, context.timeout());
        } else {
            // write data
            this.writeData(socket, context, message, owner_id, context.timeout());
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }

    private SocketMessage readData(final AsynchronousSocketChannel socket,
                                   final SocketSettings context) throws Exception {
        // read data

        final SocketMessage message = SocketUtils.read(socket, context.timeout());
        if (null != message && !message.isHandShake()) {

            // decode
            try {
                super.cipher().decode(message);
            } catch (Throwable t) {
                super.error("decode", t);
            }

        }
        return message;

    }

    private void writeData(final AsynchronousSocketChannel socket,
                           final SocketSettings context,
                           final SocketMessage message,
                           final String owner_id,
                           final int timeout_ms) throws Exception {

        if (!message.isHandShake()) {

            // encode
            try {
                super.cipher().encode(message, StringUtils.hasText(owner_id) ? owner_id : message.ownerId());
            } catch (Throwable t) {
                super.error("encode", t);
            }
        }

        final ByteBuffer send_buffer = ByteBuffer.wrap(message.bytes());
        final Future<Integer> futureWriteResult = socket.write(send_buffer);
        futureWriteResult.get(timeout_ms, TimeUnit.MILLISECONDS);
        send_buffer.clear();

    }

}
