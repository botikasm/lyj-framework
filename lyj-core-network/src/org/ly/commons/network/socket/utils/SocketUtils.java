package org.ly.commons.network.socket.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketUtils {


    public static byte[] read(final AsynchronousSocketChannel channel,
                              final int timeout_ms) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Allocate a byte buffer (4K) to read from the client
            final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            // Read the first line
            int count_bytes_read = read(channel, byteBuffer, timeout_ms);
            boolean running = true;
            while (count_bytes_read != -1 && running) {

                // Make sure that we have data to read
                if (byteBuffer.position() > 2) {
                    // Make the buffer ready to read
                    byteBuffer.flip();

                    // add to stream
                    out.write(byteBuffer.array(), 0, count_bytes_read);

                    // Make the buffer ready to write
                    byteBuffer.clear();

                    // Read the next line
                    count_bytes_read = read(channel, byteBuffer, timeout_ms);
                } else {
                    // An empty line signifies the end of the conversation in our protocol
                    running = false;
                }
            }
        } catch (Exception ignored) {
            System.out.println(ignored);
        }

        final byte[] response = out.toByteArray();
        close(out);

        return response;
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
