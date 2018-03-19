package org.ly.commons.network.socket.basic.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;


public class SocketBasicServer {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private int _port;

    private AsynchronousServerSocketChannel _listener;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicServer() {
        _port = 5000;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public SocketBasicServer port(final int value) {
        _port = value;
        return this;
    }

    public int port() {
        return _port;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        try {
            // Create an AsynchronousServerSocketChannel that will listen on port 5000
            _listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(_port));
            _listener.accept(null, new SocketBasicServerHandler(_listener)
                    .onChannelOpen(this::onChannelOpen)
                    .onChannelClose(this::onChannelClose));

        } catch (IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

    public void close() {
        try {
            if (null != _listener) {
                _listener.close();
            }
        } catch (Throwable ignored) {
            _listener = null;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    /**
     * Invoked on new channel open
     */
    private void onChannelOpen(final SocketBasicServerHandler.ChannelInfo channel_info) {

    }

    private void onChannelClose(final SocketBasicServerHandler.ChannelInfo channel_info) {

    }

}