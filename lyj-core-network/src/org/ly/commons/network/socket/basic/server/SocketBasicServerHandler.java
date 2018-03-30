package org.ly.commons.network.socket.basic.server;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.SocketMessage;
import org.ly.commons.network.socket.basic.message.SocketMessageDispatcher;
import org.ly.commons.network.socket.basic.message.SocketMessageHandShake;
import org.lyj.commons.lang.CharEncoding;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class SocketBasicServerHandler
        extends SocketLogger
        implements CompletionHandler<AsynchronousSocketChannel, SocketContext> {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int DEF_TIMEOUT = 5000;
    private static final String DEF_CHARSET = CharEncoding.getDefault();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final AsynchronousServerSocketChannel _listener;
    private final SocketMessageDispatcher _message;

    private SocketBasicServer.OpenCloseCallback _callback_on_channel_open;
    private SocketBasicServer.OpenCloseCallback _callback_on_channel_close;
    private SocketBasicServer.MessageCallback _callback_on_channel_message;

    private boolean _closed;

    private int _timeout_ms;
    private String _charset;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicServerHandler(final AsynchronousServerSocketChannel listener) {
        _listener = listener;
        _closed = false;
        _charset = DEF_CHARSET;
        _timeout_ms = DEF_TIMEOUT;

        _message = new SocketMessageDispatcher("server");
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public int timeout() {
        return _timeout_ms;
    }

    public SocketBasicServerHandler timeout(final int value) {
        _timeout_ms = value;
        return this;
    }

    public String charset() {
        return _charset;
    }

    public SocketBasicServerHandler charset(final String value) {
        _charset = value;
        return this;
    }

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
                          final SocketContext context) {

        this.doChannelOpen(channel, context);

        // listen for nex connection
        if (null != _listener && _listener.isOpen()) {
            _listener.accept(context, this);
        }

        try {
            // send public key
            // _message.write(channel, context, new SocketMessage(), _time_out);

            // wait for client message
            final SocketMessage request = _message.read(channel, context, _timeout_ms);
            if (null != request) {
                if (request.isHandShake()) {
                    _message.encodeKey(request.signature());

                    final SocketMessageHandShake response = new SocketMessageHandShake();
                    response.signature(_message.publicKey());

                    _message.write(channel, context, response, _timeout_ms);
                } else {
                    if (null == _callback_on_channel_message) {
                        // ECHO
                        _message.write(channel, context, request, _timeout_ms);
                    } else {
                        final SocketMessage response = this.doChannelMessage(channel, context, request);
                        _message.write(channel, context, response, _timeout_ms);
                    }
                }
            } else {
                // unable to read the request
                super.warning("completed", "UNABLE TO READ REQUEST: Invalid request format.");
            }


        } catch (Exception e) {
            this.doChannelClose(channel, context, e);
        }

        // this.log(ch, "\t\tEnd of conversation");

        try {
            // Close the connection if we need to
            if (channel.isOpen()) {
                channel.close();
            }
        } catch (Exception e) {
            this.doChannelClose(channel, context, e);
        }

    }

    @Override
    public void failed(final Throwable exc, final SocketContext attachment) {
        // System.out.println("FAILED: " + exc + ". "  );
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void doChannelOpen(final AsynchronousSocketChannel ch,
                               final SocketContext attachment) {
        if (null != _callback_on_channel_open) {
            _callback_on_channel_open.handle(new SocketBasicServer.ChannelInfo(ch, attachment));
        }
    }

    private void doChannelClose(final AsynchronousSocketChannel ch,
                                final SocketContext attachment,
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
                                           final SocketContext attachment,
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
