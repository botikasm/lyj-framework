package org.ly.commons.network.socket.basic.client;

import org.ly.commons.network.socket.basic.AbstractMessageDispatcher;
import org.ly.commons.network.socket.basic.SocketSettings;
import org.ly.commons.network.socket.basic.message.chunks.ChunkManager;
import org.ly.commons.network.socket.basic.message.cipher.impl.ClientCipher;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.basic.message.impl.SocketMessageHandShake;
import org.ly.commons.network.socket.utils.SocketUtils;
import org.lyj.commons.lang.ValueObject;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SocketBasicClientDispatcher
        extends AbstractMessageDispatcher {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicClientDispatcher() {
        super(new ClientCipher());

        this.init();

    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void handShake(final SocketSettings context) throws Exception {
        final byte[] public_key = super.cipher().publicKey().getBytes();
        final SocketMessageHandShake handshake = new SocketMessageHandShake(context.uid());
        handshake.signature(public_key);

        final SocketMessage response = this.send(handshake, context);
        if (response.isHandShake()) {
            ((ClientCipher) super.cipher()).encodeKey(new String(response.body(), context.charset()));
        }
    }

    public SocketMessage send(final String text,
                              final Map<String, Object> headers,
                              final SocketSettings context) throws Exception {

        // creates message
        final SocketMessage message = this.newMessage(context);
        message.body(text);
        message.headers().putAll(headers);

        return this.send(message, context);
    }

    public SocketMessage send(final File file,
                              final Map<String, Object> headers,
                              final SocketSettings context) throws Exception {

        // creates message
        final SocketMessage message = this.newMessage(context);
        message.body(file);
        message.headers().putAll(headers);

        return this.send(message, context);
    }

    public SocketMessage send(final SocketMessage message,
                              final SocketSettings context) throws Exception {
        if (!StringUtils.hasText(message.ownerId())) {
            message.ownerId(context.uid());
        }
        return this.write(message, context);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }

    private AsynchronousSocketChannel openSocket(final SocketSettings context) throws Exception {
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress(context.host(), context.port())).get(context.timeout(), TimeUnit.MILLISECONDS);

        //client.setOption(StandardSocketOptions.SO_RCVBUF, 2 * MESSAGE_INPUT_SIZE);
        //client.setOption(StandardSocketOptions.SO_SNDBUF, 2 * MESSAGE_INPUT_SIZE);
        //client.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        client.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        return client;
    }

    private SocketMessage newMessage(final SocketSettings context) {
        final SocketMessage message = new SocketMessage(context.uid());
        //message.signature(_message.signature());

        return message;
    }

    private SocketMessage write(final SocketMessage message,
                                final SocketSettings context) throws Exception {

        final ValueObject<SocketMessage> response = new ValueObject<>();

        if (message.isHandShake()) {
            // send data with no encryption and no tokenizer
            response.content(sendData(message.bytes(), context));
        } else {

            if (message.bodyLength() > super.chunkSize() || message.isFile()) {
                // tokenize
                ChunkManager.split(message, super.chunkSize(), (final SocketMessage token_message) -> {
                    // send chunk
                    try {
                        final SocketMessage chunk_response = sendMessage(token_message, context);
                        if (chunk_response.isChunk()) {
                            if (null == response.content()) {
                                response.content(chunk_response);
                            }
                        } else {
                            response.content(chunk_response);
                        }
                    } catch (Exception e) {
                        // error sending token
                    }
                });

                //Async.maxConcurrent(threads, 5);
                //Async.joinAll(threads);
            } else {
                response.content(sendMessage(message, context));
            }
        }

        return response.content();
    }

    private SocketMessage sendMessage(final SocketMessage message,
                                      final SocketSettings context) throws Exception {
        // encode
        try {
            super.cipher().encode(message, message.ownerId());
        } catch (Throwable t) {
            super.error("sendMessage", t);
        }
        return sendData(message.bytes(), context);
    }

    private SocketMessage sendData(final byte[] data,
                                   final SocketSettings context) throws Exception {
        try (final AsynchronousSocketChannel socket = this.openSocket(context)) {
            final ByteBuffer send_buffer = ByteBuffer.wrap(data);
            final Future<Integer> futureWriteResult = socket.write(send_buffer);
            futureWriteResult.get(context.timeout(), TimeUnit.MILLISECONDS);
            send_buffer.clear();

            return this.readData(socket, context);
        }

    }

    private SocketMessage readData(final AsynchronousSocketChannel socket,
                                   final SocketSettings context) throws Exception {
        // read data
        final SocketMessage message = SocketUtils.read(socket, context.timeout());
        if (null != message && !message.isHandShake()) {

            // decode
            try {
                super.cipher().decode(message);
            } catch (Throwable t) {
                super.error("decode", t);
            }

        }
        return message;
    }


}
