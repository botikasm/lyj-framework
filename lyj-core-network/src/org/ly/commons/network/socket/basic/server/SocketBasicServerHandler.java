package org.ly.commons.network.socket.basic.server;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.SocketMessageDispatcher;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.basic.message.impl.SocketMessageHandShake;

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


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final AsynchronousServerSocketChannel _listener;
    private final SocketBasicServerDispatcher _message;

    private SocketBasicServer.OpenCloseCallback _callback_on_channel_open;
    private SocketBasicServer.OpenCloseCallback _callback_on_channel_close;
    private SocketBasicServer.MessageCallback _callback_on_channel_message;

    private boolean _closed;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicServerHandler(final AsynchronousServerSocketChannel listener) {
        _listener = listener;
        _closed = false;

        _message = new SocketBasicServerDispatcher();
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
                          final SocketContext server_context) {

        this.doChannelOpen(channel, server_context);

        // listen for nex connection
        if (null != _listener && _listener.isOpen()) {
            _listener.accept(server_context, this);
        }

        try {

            // wait for client message
            final SocketMessage request = _message.read(channel, server_context);
            if (null != request) {
                // get client id from message
                final String client_id = request.ownerId();

                if (request.isHandShake()) {
                    final String encode_key = new String(request.body(), server_context.charset());
                    
                    _message.encodeKey(client_id, encode_key);

                    final SocketMessageHandShake response = new SocketMessageHandShake(server_context.uid());
                    response.signature(_message.signature());

                    _message.write(channel, server_context, response);
                } else {
                    if (null == _callback_on_channel_message) {
                        // ECHO
                        final SocketMessage response = new SocketMessage(server_context.uid());
                        response.body(request.body());
                        _message.write(channel, server_context, response, client_id);
                    } else {
                        final SocketMessage response = this.doChannelMessage(channel, server_context, request);
                        _message.write(channel, server_context, response, client_id);
                    }
                }
            } else {
                // unable to read the request
                super.warning("completed", "UNABLE TO READ REQUEST: Invalid request format.");
            }


        } catch (Exception e) {
            this.doChannelClose(channel, server_context, e);
        }

        // this.log(ch, "\t\tEnd of conversation");

        try {
            // Close the connection if we need to
            if (channel.isOpen()) {
                channel.close();
            }
        } catch (Exception e) {
            this.doChannelClose(channel, server_context, e);
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
                                           final SocketContext context,
                                           final SocketMessage request) {
        final SocketMessage response = new SocketMessage(context.uid());
        // response.copySignature(request);
        // response.signature(_message.encodeKey(request.ownerId()));
        response.signature(_message.signature());
        response.body(new byte[0]); // initialize response with empty content
        if (null != _callback_on_channel_message) {
            _callback_on_channel_message.handle(new SocketBasicServer.ChannelInfo(ch, context), request, response);
        }
        return response;
    }

}
