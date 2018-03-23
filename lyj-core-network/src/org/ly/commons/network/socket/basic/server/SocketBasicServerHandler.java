package org.ly.commons.network.socket.basic.server;

import org.ly.commons.network.socket.basic.message.SocketMessage;
import org.ly.commons.network.socket.utils.SocketUtils;
import org.lyj.commons.lang.CharEncoding;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class SocketBasicServerHandler
        implements CompletionHandler<AsynchronousSocketChannel, Object> {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int DEF_TIMEOUT = 1;
    private static final String DEF_CHARSET = CharEncoding.getDefault();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final AsynchronousServerSocketChannel _listener;

    private SocketBasicServer.OpenCloseCallback _callback_on_channel_open;
    private SocketBasicServer.OpenCloseCallback _callback_on_channel_close;
    private SocketBasicServer.MessageCallback _callback_on_channel_message;

    private boolean _closed;

    private int _time_out;
    private String _charset;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicServerHandler(final AsynchronousServerSocketChannel listener) {
        _listener = listener;
        _closed = false;
        _charset = DEF_CHARSET;
        _time_out = DEF_TIMEOUT;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public SocketBasicServerHandler onChannelOpen(SocketBasicServer.OpenCloseCallback value) {
        _callback_on_channel_open = value;
        return this;
    }

    public SocketBasicServerHandler onChannelClose(SocketBasicServer.OpenCloseCallback value) {
        _callback_on_channel_close = value;
        return this;
    }

    public SocketBasicServerHandler onChannelMessage(SocketBasicServer.MessageCallback value) {
        _callback_on_channel_message = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void completed(final AsynchronousSocketChannel channel,
                          final Object attachment) {

        this.doChannelOpen(channel, attachment);

        // listen for nex connection
        if (null != _listener && _listener.isOpen()) {
            _listener.accept(attachment, this);
        }

        try {
            // send public key
            this.write(channel, this.publicKey(channel).bytes());

            // wait for client message
            final SocketMessage request = SocketUtils.read(channel, 2000);

            if (null == _callback_on_channel_message) {
                // ECHO
                this.write(channel, request.bytes());
            } else {
                final SocketMessage response = this.doChannelMessage(channel, attachment, request);
                this.write(channel, response.bytes());
            }

        } catch (Exception e) {
            this.doChannelClose(channel, attachment, e);
        }

        // this.log(ch, "\t\tEnd of conversation");

        try {
            // Close the connection if we need to
            if (channel.isOpen()) {
                channel.close();
            }
        } catch (Exception e) {
            this.doChannelClose(channel, attachment, e);
        }

    }

    @Override
    public void failed(final Throwable exc, final Object attachment) {
        // System.out.println("FAILED: " + exc + ". "  );
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private SocketMessage publicKey(final AsynchronousSocketChannel channel) {
        final SocketMessage pk = new SocketMessage();
        pk.signature(channel.toString());
        pk.body(pk.signature());

        return pk;
    }

    private void write(final AsynchronousSocketChannel ch,
                       final byte[] data) {
        final SocketMessage message = new SocketMessage(data);
        ch.write(ByteBuffer.wrap(message.bytes()));
    }

    private void doChannelOpen(final AsynchronousSocketChannel ch,
                               final Object attachment) {
        if (null != _callback_on_channel_open) {
            _callback_on_channel_open.handle(new SocketBasicServer.ChannelInfo(ch, attachment));
        }
    }

    private void doChannelClose(final AsynchronousSocketChannel ch,
                                final Object attachment,
                                final Exception ex) {
        if (null != ex) {
            if (ex instanceof TimeoutException) {
                // The user exceeded the 20 second timeout, so close the connection
                System.out.println("Connection timed out, closing connection");
            } else if (ex instanceof InterruptedException) {
                // interrupted
            } else if (ex instanceof ExecutionException) {
                // java.io.IOException: Connection reset by peer
            }
        }

        if (null != _callback_on_channel_close && !_closed) {
            _closed = true; // avoid double callback call
            _callback_on_channel_close.handle(new SocketBasicServer.ChannelInfo(ch, attachment, ex));
        }
    }

    private SocketMessage doChannelMessage(final AsynchronousSocketChannel ch,
                                           final Object attachment,
                                           final SocketMessage request) {
        final SocketMessage response = new SocketMessage();
        response.copySignature(request);
        response.body(new byte[0]); // initialize response with empty content
        if (null != _callback_on_channel_message) {
            _callback_on_channel_message.handle(new SocketBasicServer.ChannelInfo(ch, attachment), request, response);
        }
        return response;
    }

}
