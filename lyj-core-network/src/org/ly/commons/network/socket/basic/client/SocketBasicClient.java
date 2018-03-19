package org.ly.commons.network.socket.basic.client;

import org.ly.commons.network.socket.utils.SocketUtils;
import org.lyj.commons.async.Async;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ReadPendingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketBasicClient {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _host;
    private int _port;

    private Socket _socket;
    private AsynchronousSocketChannel _client;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicClient() {

    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

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

    public SocketBasicClient open() throws Exception {
        this.openSocket();
        return this;
    }

    public SocketBasicClient close() {
        this.closeSocket();
        return this;
    }

    public void send(final String message) throws Exception {
        this.write(message);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void openSocket() throws Exception {
        this.closeSocket();

        _client = AsynchronousSocketChannel.open();
        _client.connect(new InetSocketAddress(_host, _port)).get(5, TimeUnit.SECONDS);

        this.listen();
    }

    private void closeSocket() {
        if (null != _client) {
            try {
                _client.close();
            } catch (Throwable ignored) {
            } finally {
                _client = null;
            }
        }
    }

    private void listen() {
        Async.invoke((args) -> {
            while (null != _client && _client.isOpen()) {
                try {

                    final byte[] data = SocketUtils.read(_client, 2000);
                    if (data.length > 0) {
                        String text = new String(data).trim();

                        System.out.println("CLIENT RECEIVED: '" + text + "'");
                    }

                } catch (Exception e) {
                    System.out.println("Client listen I/O exception: " + e);
                }
            }
        });

    }

    private void close(final Closeable stream) {
        try {
            stream.close();
        } catch (Throwable ignored) {
        }
    }

    private void write(final String message) throws Exception {

        if (null != _client && _client.isOpen()) {
            byte[] byteMsg = new String(message).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
            Future<Integer> writeResult = _client.write(buffer);

            // send write
            writeResult.get();
        }
    }

}
