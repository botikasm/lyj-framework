package org.ly.commons.network.socket.utils;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.message.SocketMessageReader;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ReadPendingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketUtils {

    private static final int BUFFER_SIZE = 4096*4096*8;

    private static final SocketLogger LOGGER = new SocketLogger();

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static SocketMessage read(final AsynchronousSocketChannel channel,
                                     final int timeout_ms) {
        long count_total_bytes = 0;
        if (channel.isOpen()) {
            try (final SocketMessageReader reader = new SocketMessageReader();) {
                // Allocate a byte buffer (4K) to read from the client
                final ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                // Read the first line
                int count_bytes_read = read(channel, byteBuffer, timeout_ms);
                boolean running = true;
                while (count_bytes_read != -1 && running && !reader.isComplete()) {

                    count_total_bytes += count_bytes_read;

                    // Make sure that we have data to read
                    if (byteBuffer.position() > 2) {
                        // Make the buffer ready to read
                        byteBuffer.flip();

                        // add to stream
                        reader.write(byteBuffer.array(), 0, count_bytes_read);

                        if (reader.isComplete()) {
                            break;
                        }

                        // Make the buffer ready to write
                        byteBuffer.clear();

                        // Read the next line
                        count_bytes_read = read(channel, byteBuffer, timeout_ms);
                    } else {
                        // An empty line signifies the end of the conversation in our protocol
                        running = false;
                    }
                }

                return reader.message();
            } catch (ReadPendingException e) {
                LOGGER.error("read#1. Bytes read: " + count_total_bytes, e);
            } catch (TimeoutException e) {
                LOGGER.error("read#2. Bytes read: " + count_total_bytes, e.toString());
            } catch (IOException | ExecutionException | InterruptedException e) {
                LOGGER.error("read#3. Bytes read: " + count_total_bytes, e);
            }

        }

        return null;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static int read(final AsynchronousSocketChannel channel,
                            final ByteBuffer byteBuffer,
                            final int timeout_ms) throws InterruptedException, ExecutionException, TimeoutException {
        final Future<Integer> future = channel.read(byteBuffer);
        return future.get(timeout_ms, TimeUnit.MILLISECONDS);
    }

    private static void close(final Closeable stream) {
        try {
            stream.close();
        } catch (Throwable ignored) {
        }
    }
}
