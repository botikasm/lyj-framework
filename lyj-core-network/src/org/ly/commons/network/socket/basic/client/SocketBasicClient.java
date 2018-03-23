package org.ly.commons.network.socket.basic.client;

import org.ly.commons.network.socket.basic.message.SocketMessage;
import org.ly.commons.network.socket.utils.SocketUtils;
import org.lyj.commons.util.RandomUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketBasicClient {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final static int TIMEOUT_MS = 5000;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _uid;
    private String _host;
    private int _port;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicClient() {
        _uid = RandomUtils.randomUUID();
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

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public SocketMessage send(final String message) throws Exception {
        return this.write(message, TIMEOUT_MS);
    }

    public SocketMessage send(final String message,
                              final int timeout_ms) throws Exception {
        return this.write(message, timeout_ms);
    }

    public SocketMessage send(final File file,
                              final int timeout_ms) throws Exception {
        return this.write(file, timeout_ms);
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


    private void close(final Closeable stream) {
        try {
            stream.close();
        } catch (Throwable ignored) {
        }
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

            // wait for server public key
            final SocketMessage public_key = SocketUtils.read(socket, TIMEOUT_MS);

            final ByteBuffer send_buffer = ByteBuffer.wrap(message.bytes());
            final Future<Integer> futureWriteResult = socket.write(send_buffer);
            futureWriteResult.get();
            send_buffer.clear();

            //Now wait for return message.
            return SocketUtils.read(socket, TIMEOUT_MS);

        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
            this.handleException(e);
        }

        return null; // no response
    }


    private void handleException(final Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }

}
