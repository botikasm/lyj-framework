package org.ly.commons.network.socket.basic.client;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.dispatcher.impl.SocketClientDispatcher;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.basic.message.impl.SocketMessageHandShake;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketBasicClient
        extends SocketLogger {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final SocketClientDispatcher _message;
    private final String _uid;

    private String _host;
    private int _port;
    private int _timeout_ms;
    private String _charset;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicClient() {
        _uid = RandomUtils.randomUUID();
        _port = SocketContext.DEFAULT_PORT;
        _timeout_ms = SocketContext.DEFAULT_TIMEOUT;
        _charset = CharEncoding.UTF_8;

        _message = new SocketClientDispatcher();
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

    public String charset() {
        return _charset;
    }

    public SocketBasicClient charset(final String value) {
        _charset = value;
        return this;
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Client sent an handshake request to a target.
     * Use handshake to activate encryption.
     */
    public void handShake() throws Exception {
        final byte[] public_key = _message.publicKey().getBytes();
        final SocketMessageHandShake handshake = new SocketMessageHandShake(_uid);
        handshake.signature(public_key);

        final SocketMessage response = this.send(handshake);
        if (response.isHandShake()) {
            _message.encodeKey(new String(response.body(), _charset));
        }
    }

    public SocketMessage send(final String message) throws Exception {
        return this.write(message, null, this.context(_timeout_ms));
    }

    public SocketMessage send(final String message,
                              final Map<String, Object> headers) throws Exception {
        return this.write(message, headers, this.context(_timeout_ms));
    }

    public SocketMessage send(final String message,
                              final int timeout_ms) throws Exception {
        return this.write(message, null, this.context(timeout_ms));
    }

    public SocketMessage send(final String message,
                              final Map<String, Object> headers,
                              final int timeout_ms) throws Exception {
        return this.write(message, headers, this.context(timeout_ms));
    }

    public SocketMessage send(final File file) throws Exception {
        return this.write(file, null, this.context(_timeout_ms));
    }

    public SocketMessage send(final File file,
                              final Map<String, Object> headers) throws Exception {
        return this.write(file, headers, this.context(_timeout_ms));
    }

    public SocketMessage send(final File file,
                              final int timeout_ms) throws Exception {
        return this.write(file, null, this.context(timeout_ms));
    }

    public SocketMessage send(final File file,
                              final Map<String, Object> headers,
                              final int timeout_ms) throws Exception {
        return this.write(file, headers, this.context(timeout_ms));
    }

    public SocketMessage send(final SocketMessage message) throws Exception {
        return this.write(message, this.context(_timeout_ms));
    }

    public SocketMessage send(final SocketMessage message,
                              final int timeout_ms) throws Exception {
        return this.write(message, this.context(timeout_ms));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private SocketContext context() {
        return new SocketContext(_uid)
                .host(this.host())
                .port(this.port())
                .timeout(this.timeout())
                .charset(this.charset());
    }

    private SocketContext context(final int timeout) {
        return new SocketContext(_uid)
                .host(this.host())
                .port(this.port())
                .timeout(timeout)
                .charset(this.charset());
    }

    private AsynchronousSocketChannel openSocket(final SocketContext context) throws Exception {

        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress(context.host(), context.port())).get(context.timeout(), TimeUnit.MILLISECONDS);

        //client.setOption(StandardSocketOptions.SO_RCVBUF, 2 * MESSAGE_INPUT_SIZE);
        //client.setOption(StandardSocketOptions.SO_SNDBUF, 2 * MESSAGE_INPUT_SIZE);
        //client.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        client.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        return client;
    }

    private SocketMessage newMessage() {
        final SocketMessage message = new SocketMessage(_uid);
        //message.signature(_message.signature());

        return message;
    }

    private SocketMessage write(final String text,
                                final Map<String, Object> headers,
                                final SocketContext context) throws Exception {

        // creates message
        final SocketMessage message = this.newMessage();
        message.body(text);
        message.headers().putAll(headers);

        return this.write(message, context);
    }

    private SocketMessage write(final File file,
                                final Map<String, Object> headers,
                                final SocketContext context) throws Exception {

        // creates message
        final SocketMessage message = this.newMessage();
        message.body(file);
        message.headers().putAll(headers);

        return this.write(message, context);
    }

    private SocketMessage write(final SocketMessage message,
                                final SocketContext context) throws Exception {

        try ( final AsynchronousSocketChannel socket = this.openSocket(context) ) {

            _message.write(socket, context, message);

            //Now wait for return message.
            return _message.read(socket, context); //SocketUtils.read(socket, TIMEOUT_MS);

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
