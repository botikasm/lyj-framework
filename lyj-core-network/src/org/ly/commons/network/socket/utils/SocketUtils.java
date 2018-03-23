package org.ly.commons.network.socket.utils;

import org.ly.commons.network.socket.basic.message.SocketMessage;
import org.ly.commons.network.socket.basic.message.SocketMessageReader;

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

    private static final int BUFFER_SIZE = 4096;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static SocketMessage read(final AsynchronousSocketChannel channel,
                                     final int timeout_ms) {
        if (channel.isOpen()) {
            try (final SocketMessageReader reader = new SocketMessageReader();) {
                // Allocate a byte buffer (4K) to read from the client
                final ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                // Read the first line
                int count_bytes_read = read(channel, byteBuffer, timeout_ms);
                boolean running = true;
                while (count_bytes_read != -1 && running && !reader.isComplete()) {

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
                //System.out.println("SocketUtils.read() " + ignored);
            } catch(TimeoutException e){
                // System.out.println("SocketUtils.read() " + e);
            } catch (IOException | ExecutionException | InterruptedException e) {
                
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
