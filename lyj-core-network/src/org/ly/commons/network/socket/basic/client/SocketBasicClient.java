package org.ly.commons.network.socket.basic.client;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.SocketMessage;
import org.ly.commons.network.socket.basic.message.SocketMessageDispatcher;
import org.ly.commons.network.socket.basic.message.SocketMessageHandShake;
import org.lyj.commons.util.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketBasicClient
        extends SocketLogger{

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final static int TIMEOUT_MS = 5000;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final SocketMessageDispatcher _message;
    private final String _uid;

    private String _host;
    private int _port;
    private boolean _encrypt;
    private int _timeout_ms;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicClient() {
        _uid = RandomUtils.randomUUID();
        _encrypt = true;
        _port = 5000;
        _timeout_ms = TIMEOUT_MS;

        _message = new SocketMessageDispatcher("client");
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String uid() {
        return this._uid;
    }

    public String host() {
        return _host;
    }

    public SocketBasicClient host(final String value) {
        _host = value;
        return this;
    }

    public int port() {
        return _port;
    }

    public SocketBasicClient port(final int value) {
        _port = value;
        return this;
    }

    public int timeout() {
        return _timeout_ms;
    }

    public SocketBasicClient timeout(final int value) {
        _timeout_ms = value;
        return this;
    }

    public boolean encrypt() {
        return _encrypt;
    }

    public SocketBasicClient encrypt(final boolean value) {
        _encrypt = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Client sent an handshake request to a target
     */
    public void handShake() throws Exception {
        final String public_key = _message.publicKey();
        final SocketMessageHandShake handshake = new SocketMessageHandShake();
        handshake.signature(public_key);

        final SocketMessage response = this.send(handshake);
        if (response.isHandShake()) {
            _message.encodeKey(response.signature());
        }
    }

    public SocketMessage send(final String message) throws Exception {
        return this.write(message, _timeout_ms);
    }

    public SocketMessage send(final String message,
                              final int timeout_ms) throws Exception {
        return this.write(message, timeout_ms);
    }

    public SocketMessage send(final File file) throws Exception {
        return this.write(file, _timeout_ms);
    }

    public SocketMessage send(final File file,
                              final int timeout_ms) throws Exception {
        return this.write(file, timeout_ms);
    }

    public SocketMessage send(final SocketMessage message) throws Exception {
        return this.write(message, _timeout_ms);
    }

    public SocketMessage send(final SocketMessage message,
                              final int timeout_ms) throws Exception {
        return this.write(message, timeout_ms);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private AsynchronousSocketChannel openSocket(final int timeout) throws Exception {

        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress(_host, _port)).get(timeout, TimeUnit.MILLISECONDS);

        //client.setOption(StandardSocketOptions.SO_RCVBUF, 2 * MESSAGE_INPUT_SIZE);
        //client.setOption(StandardSocketOptions.SO_SNDBUF, 2 * MESSAGE_INPUT_SIZE);
        //client.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        client.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        return client;
    }

    private SocketMessage newMessage() {
        final SocketMessage message = new SocketMessage();
        message.signature(this.uid());

        return message;
    }

    private SocketMessage write(final String text,
                                final int timeout_ms) throws Exception {

        // creates message
        final SocketMessage message = this.newMessage();
        message.body(text);

        return this.write(message, timeout_ms);
    }

    private SocketMessage write(final File file,
                                final int timeout_ms) throws Exception {

        // creates message
        final SocketMessage message = this.newMessage();
        message.body(file);

        return this.write(message, timeout_ms);
    }

    private SocketMessage write(final SocketMessage message,
                                final int timeout_ms) throws Exception {

        try (AsynchronousSocketChannel socket = this.openSocket(timeout_ms);) {

            final SocketContext context = new SocketContext().port(this.port()).encrypt(this.encrypt());

            // wait for server public key
            //final SocketMessage remote_public_key = _message.read(socket, context, timeout_ms);

            /**
             final ByteBuffer send_buffer = ByteBuffer.wrap(message.bytes());
             final Future<Integer> futureWriteResult = socket.write(send_buffer);
             futureWriteResult.get();
             send_buffer.clear();
             **/

            _message.write(socket, context, message, timeout_ms);

            //Now wait for return message.
            return _message.read(socket, context, timeout_ms); //SocketUtils.read(socket, TIMEOUT_MS);

        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
            this.handleException(e);
        }

        return null; // no response
    }


    private void handleException(final Exception e) {
        super.error("handleException", e);
        throw new RuntimeException(e);
    }

}
