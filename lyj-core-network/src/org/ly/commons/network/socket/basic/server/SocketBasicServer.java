package org.ly.commons.network.socket.basic.server;

import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.SocketMessage;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.cryptograph.SecurityMessageDigester;
import org.lyj.commons.util.json.JsonItem;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;


public class SocketBasicServer
        implements AutoCloseable {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    @FunctionalInterface
    public static interface OpenCloseCallback {
        void handle(final ChannelInfo data);
    }

    @FunctionalInterface
    public static interface MessageCallback {
        void handle(final ChannelInfo data, final SocketMessage request, final SocketMessage response);
    }

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private int _port;
    private boolean _encrypt;

    private AsynchronousServerSocketChannel _listener;

    private SocketBasicServer.OpenCloseCallback _callback_on_channel_open;
    private SocketBasicServer.OpenCloseCallback _callback_on_channel_close;
    private SocketBasicServer.MessageCallback _callback_on_channel_message;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicServer() {
        _port = 5000;
        _encrypt = true;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public SocketBasicServer onChannelOpen(SocketBasicServer.OpenCloseCallback value) {
        _callback_on_channel_open = value;
        return this;
    }

    public SocketBasicServer onChannelClose(SocketBasicServer.OpenCloseCallback value) {
        _callback_on_channel_close = value;
        return this;
    }

    public SocketBasicServer onChannelMessage(SocketBasicServer.MessageCallback value) {
        _callback_on_channel_message = value;
        return this;
    }

    public SocketBasicServer port(final int value) {
        _port = value;
        return this;
    }

    public int port() {
        return _port;
    }

    public boolean encrypt() {
        return _encrypt;
    }

    public SocketBasicServer encrypt(final boolean value) {
        _encrypt = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        try {
            // Create an AsynchronousServerSocketChannel that will listen on port 5000
            _listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(_port));
            _listener.accept(
                    new SocketContext().port(this.port()).encrypt(this.encrypt()),
                    new SocketBasicServerHandler(_listener)
                            .onChannelOpen(this::handleChannelOpen)
                            .onChannelClose(this::handleChannelClose)
                            .onChannelMessage(this::handleChannelMessage)
            );

        } catch (IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

    @Override
    public void close() {
        try {
            if (null != _listener) {
                _listener.close();
            }
        } catch (Throwable ignored) {

        } finally {
            _listener = null;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    /**
     * Invoked on new channel open
     */
    private void handleChannelOpen(final ChannelInfo channel_info) {
        if (null != _callback_on_channel_open) {
            _callback_on_channel_open.handle(channel_info);
        }
    }

    private void handleChannelClose(final ChannelInfo channel_info) {
        if (null != _callback_on_channel_close) {
            _callback_on_channel_close.handle(channel_info);
        }
    }

    private void handleChannelMessage(final ChannelInfo channel_info,
                                      final SocketMessage request,
                                      final SocketMessage response) {
        if (null != _callback_on_channel_message) {
            _callback_on_channel_message.handle(channel_info, request, response);
        }
    }


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * Wrap some channel information
     */
    public static class ChannelInfo {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private final long _nonce;
        private String _uid;
        private String _signature;

        private Exception _error;
        private SocketAddress _local_address;
        private SocketAddress _remote_address;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public ChannelInfo(final AsynchronousSocketChannel channel,
                           final SocketContext attachment) {
            this(channel, attachment, null);
        }

        public ChannelInfo(final AsynchronousSocketChannel channel,
                           final SocketContext attachment,
                           final Exception error) {
            _nonce = channel.hashCode();
            this.init(channel, attachment, error);
        }

        @Override
        public String toString() {
            return this.toJson().toString();
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public JsonItem toJson() {
            final JsonItem sb = new JsonItem();
            sb.put("nonce", _nonce);
            sb.put("uid", _uid);
            sb.put("signature", _signature);
            sb.put("error", null != _error ? _error.toString() : "");
            sb.put("localAddress", this.localAddress());
            sb.put("remoteAddress", this.remoteAddress());

            return sb;
        }

        public long nonce() {
            return _nonce;
        }

        public String uid() {
            return _uid;
        }

        public String signature() {
            return _signature;
        }

        public boolean hasError() {
            return null != _error;
        }

        public Exception error() {
            return _error;
        }

        public String localAddress() {
            if (null != _local_address) {
                return _local_address.toString();
            }
            return "";
        }

        public String remoteAddress() {
            if (null != _remote_address) {
                return _remote_address.toString();
            }
            return "";
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private void init(final AsynchronousSocketChannel channel,
                          final SocketContext attachment,
                          final Exception error) {
            _error = error;
            try {
                _local_address = channel.getLocalAddress();
                _remote_address = channel.getRemoteAddress();
            } catch (Throwable ignored) {
            }

            _uid = MD5.encode(_nonce + this.localAddress() + this.remoteAddress()).toLowerCase();
            _signature = SecurityMessageDigester.encodeSHA_256(_uid, _nonce + "");

        }


    }


}