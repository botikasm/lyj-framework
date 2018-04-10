package org.ly.commons.network.socket.basic.message.dispatcher;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.basic.message.cipher.KeyManager;
import org.lyj.commons.cryptograph.pem.RSAHelper;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class SocketMessageDispatcher
        extends SocketLogger {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "./keyStore";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _name;
    private final KeyManager _keys;
    private final SocketMessageDispatcherChunk _chunks;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessageDispatcher(final String name) {
        _name = name;
        _keys = new KeyManager(ROOT, name);
        _chunks = new SocketMessageDispatcherChunk(this);

        this.init();
    }

    public abstract void decode(final SocketMessage message) throws Exception;

    public abstract void encode(final SocketMessage message, final String owner_id) throws Exception;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String name() {
        return _name;
    }

    public String publicKey() {
        return this.keys().publicKeyString();
    }

    public String privateKey() {
        return this.keys().privateKeyString();
    }

    public byte[] signature() {
        return this.publicKey().getBytes();
    }



    public void write(final AsynchronousSocketChannel socket,
                      final SocketContext context,
                      final SocketMessage message) throws Exception {

        // write data
        this.write(socket, context, message, "");

    }

    public void write(final AsynchronousSocketChannel socket,
                      final SocketContext context,
                      final SocketMessage message,
                      final String owner_id) throws Exception {

        // write data
        this.writeData(socket, context, message, owner_id, context.timeout());

    }

    public SocketMessage read(final AsynchronousSocketChannel socket,
                              final SocketContext context) throws Exception {
        // read data
        return this.readData(socket, context);

    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected KeyManager keys() {
        return _keys;
    }

    protected byte[] encrypt(final byte[] data, final String encode_key) throws Exception {
        final byte[] encrypted = RSAHelper.encrypt(data, encode_key);
        return encrypted;
    }

    protected byte[] decrypt(final byte[] data, final String decode_key) throws Exception {
        final byte[] decrypted = RSAHelper.decrypt(data, decode_key);
        return decrypted;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }

    private SocketMessage readData(final AsynchronousSocketChannel socket,
                                  final SocketContext context) throws Exception {
        // read data
        /*
        final SocketMessage message = SocketUtils.read(socket, context.timeout());
        if (null != message && !message.isHandShake()) {

            // decode
            try {
                this.decode(message);
            } catch (Throwable t) {
                super.error("decode", t);
            }

        }
        return message; */

        return _chunks.read(socket, context);
    }

    private void writeData(final AsynchronousSocketChannel socket,
                           final SocketContext context,
                           final SocketMessage message,
                           final String owner_id,
                           final int timeout_ms) throws Exception {
        /*
        if (!message.isHandShake()) {

            // encode
            try {
                this.encode(message, StringUtils.hasText(owner_id) ? owner_id : message.ownerId());
            } catch (Throwable t) {
                super.error("encode", t);
            }
        }

        final ByteBuffer send_buffer = ByteBuffer.wrap(message.bytes());
        final Future<Integer> futureWriteResult = socket.write(send_buffer);
        futureWriteResult.get(timeout_ms, TimeUnit.MILLISECONDS);
        send_buffer.clear();
        */
        _chunks.write(socket, message, owner_id, timeout_ms);
    }


}
