package org.ly.commons.network.socket.basic.server;

import org.ly.commons.network.socket.utils.SocketUtils;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.cryptograph.SecurityMessageDigester;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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

    private Delegates.Callback<ChannelInfo> _callback_on_channel_open;
    private Delegates.Callback<ChannelInfo> _callback_on_channel_close;

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

    public SocketBasicServerHandler onChannelOpen(Delegates.Callback<ChannelInfo> value) {
        _callback_on_channel_open = value;
        return this;
    }

    public SocketBasicServerHandler onChannelClose(Delegates.Callback<ChannelInfo> value) {
        _callback_on_channel_close = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    //@Override
    public void _completed(final AsynchronousSocketChannel ch,
                          final Object attachment) {

        this.doChannelOpen(ch, attachment);

        // listen for nex connection
        if(null!=_listener && _listener.isOpen()){
            _listener.accept(attachment, this);
        }

        // Greet the client
        this.write(ch, "Hello, I am Echo Server 2020, let's have an engaging conversation!\n");

        try {

            // Allocate a byte buffer (4K) to read from the client
            ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

            // Read the first line
            int count_bytes_read = ch.read(byteBuffer).get(20, TimeUnit.SECONDS);

            boolean running = true;
            while (count_bytes_read != -1 && running) {
                //System.out.println("bytes read: " + count_bytes_read);

                // Make sure that we have data to read
                if (byteBuffer.position() > 2) {
                    // Make the buffer ready to read
                    byteBuffer.flip();

                    // Convert the buffer into a line
                    byte[] lineBytes = new byte[count_bytes_read];
                    byteBuffer.get(lineBytes, 0, count_bytes_read);
                    String line = new String(lineBytes);

                    // Debug
                    //this.log(ch, line);

                    // Echo back to the caller
                    this.write(ch, line);

                    // Make the buffer ready to write
                    byteBuffer.clear();

                    // Read the next line
                    count_bytes_read = ch.read(byteBuffer).get(20, TimeUnit.SECONDS);
                } else {
                    // An empty line signifies the end of the conversation in our protocol
                    running = false;
                    this.log(ch, "\t\tline end");
                }
            }
        } catch (Exception e) {
            this.doChannelClose(ch, attachment, e);
        }

        this.log(ch, "\t\tEnd of conversation");

        try {
            // Close the connection if we need to
            if (ch.isOpen()) {
                ch.close();
            }
        } catch (IOException e) {
            this.doChannelClose(ch, attachment, e);
        }

    }

    @Override
    public void completed(final AsynchronousSocketChannel channel,
                          final Object attachment) {

        this.doChannelOpen(channel, attachment);

        // listen for nex connection
        if(null!=_listener && _listener.isOpen()){
            _listener.accept(attachment, this);
        }

        // Greet the client
        this.write(channel, "handshake!\n");

        try {

            final byte[] read_data = SocketUtils.read(channel, 2000);

            String line = new String(read_data);

            // Debug
            //this.log(ch, line);

            // Echo back to the caller
            this.write(channel, line);

            
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
        System.out.println("FAILED");
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void log(final AsynchronousSocketChannel ch, final String message) {
        System.out.println(FormatUtils.format("[%s] %s", ch.hashCode(), message));
    }

    private byte[] getBytes(final String text) {
        if (StringUtils.hasText(text)) {
            try {
                return text.getBytes(_charset);
            } catch (Throwable ignored) {
                return text.getBytes();
            }
        }
        return new byte[0];
    }

    private void write(final AsynchronousSocketChannel ch, final String message) {
        this.write(ch, this.getBytes(message));
    }

    private void write(final AsynchronousSocketChannel ch, final byte[] data) {
        ch.write(ByteBuffer.wrap(data));
    }

    private void doChannelOpen(final AsynchronousSocketChannel ch,
                               final Object attachment) {
        if (null != _callback_on_channel_open) {
            Delegates.invoke(_callback_on_channel_open, new ChannelInfo(ch, attachment));
        }
    }

    private void doChannelClose(final AsynchronousSocketChannel ch,
                                final Object attachment,
                                final Exception ex) {
        if (null != ex) {
            if (ex instanceof TimeoutException) {
                // The user exceeded the 20 second timeout, so close the connection
                ch.write(ByteBuffer.wrap("Good Bye\n".getBytes()));
                System.out.println("Connection timed out, closing connection");
            } else if (ex instanceof InterruptedException) {
                // interrupted
            } else if (ex instanceof ExecutionException) {
                // java.io.IOException: Connection reset by peer
            }
        }

        if (null != _callback_on_channel_close && !_closed) {
            _closed = true; // avoid double callback call
            Delegates.invoke(_callback_on_channel_close, new ChannelInfo(ch, attachment, ex));
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

        ChannelInfo(final AsynchronousSocketChannel channel,
                    final Object attachment) {
            this(channel, attachment, null);
        }

        ChannelInfo(final AsynchronousSocketChannel channel,
                    final Object attachment,
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
                          final Object attachment,
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
