package org.ly.commons.network.socket.basic.message.dispatcher;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.utils.SocketUtils;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.tokenizers.files.FileTokenizer;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketMessageDispatcherChunk
        extends SocketLogger {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final SocketMessageDispatcher _dispatcher;
    private int _chunk_size;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessageDispatcherChunk(final SocketMessageDispatcher dispatcher) {
        _dispatcher = dispatcher;
        _chunk_size = 10; // bytes
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int write(final AsynchronousSocketChannel socket,
                     final SocketMessage message,
                     final String owner_id,
                     final int timeout_ms) throws Exception {

        final Counter counter = new Counter(0);

        if (message.isHandShake()) {
            // send data with no encryption and no tokenizer
            sendData(socket, message.bytes(), timeout_ms);
        } else {
            final String real_owner_id = StringUtils.hasText(owner_id) ? owner_id : message.ownerId();
            if (message.body().length > _chunk_size) {
                // tokenize
                final String uid = RandomUtils.randomUUID(true);
                FileTokenizer.split(message.body(), _chunk_size, (index, count, progress, bytes) -> {
                    try {
                        final SocketMessage token_message = new SocketMessage(real_owner_id);
                        token_message.type(SocketMessage.MessageType.Binary);
                        token_message.body(bytes);
                        token_message.headers().chunkUid(uid);
                        token_message.headers().chunkIndex(index);
                        token_message.headers().chunkCount(count);
                        token_message.headers().chunkHeaders().putAll(message.headers().toJson());
                        // send chunk
                        sendMessage(socket, token_message, real_owner_id, timeout_ms);

                        counter.inc();
                    } catch (Exception e) {
                        // error sending token

                    }
                });
            } else {
                sendMessage(socket, message, real_owner_id, timeout_ms);
            }
        }
        return counter.valueAsInt();
    }


    public SocketMessage read(final AsynchronousSocketChannel socket,
                              final SocketContext context) throws Exception {
        // read data
        final SocketMessage message = SocketUtils.read(socket, context.timeout());
        if (null != message && !message.isHandShake()) {

            // decode
            try {
                _dispatcher.decode(message);
            } catch (Throwable t) {
                super.error("decode", t);
            }

        }
        return message;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void sendMessage(final AsynchronousSocketChannel socket,
                            final SocketMessage message,
                            final String owner_id,
                            final int timeout_ms) throws Exception {
        // encode
        try {
            _dispatcher.encode(message, owner_id);
        } catch (Throwable t) {
            super.error("sendMessage", t);
        }
        sendData(socket, message.bytes(), timeout_ms);
    }

    private void sendData(final AsynchronousSocketChannel socket,
                          final byte[] data,
                          final int timeout_ms) throws InterruptedException, ExecutionException, TimeoutException {
        final ByteBuffer send_buffer = ByteBuffer.wrap(data);
        final Future<Integer> futureWriteResult = socket.write(send_buffer);
        futureWriteResult.get(timeout_ms, TimeUnit.MILLISECONDS);
        send_buffer.clear();
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static class TimeoutTask
            extends Thread {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private long _timeout = SocketContext.DEFAULT_TIMEOUT;
        private boolean _expired = false;
        private long _last_ping = System.currentTimeMillis();

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        @Override
        public void run() {
            try {
                while (!_expired) {
                    Thread.sleep((long) (_timeout * 0.5));
                }
            } catch (Throwable ignored) {
                _expired = true;
            }
        }

        public TimeoutTask timeout(final long value) {
            _timeout = value;
            return this;
        }

        public long timeout() {
            return _timeout;
        }

        public synchronized void ping() {
            _last_ping = System.currentTimeMillis();
        }

        public synchronized boolean isExpired() {
            _expired = System.currentTimeMillis() - _last_ping > _timeout;
            return _expired;
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

    }


}
