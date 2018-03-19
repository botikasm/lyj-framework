package org.ly.commons.network.socket.basic.message;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Message wrapper
 */
public class SocketMessage {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public enum MessageType {

        Binary((byte) 0),
        Text((byte) 1),
        Undefined((byte) 99);

        private final byte _value;

        private MessageType(byte value) {
            _value = value;
        }

        public byte getValue() {
            return _value;
        }

        public static MessageType getEnum(byte value) {
            for (MessageType v : values())
                if (v.getValue() == value) return v;
            throw new IllegalArgumentException();
        }
    }

    private static final byte[] MSG_START = "<".getBytes();     // 1 byte
    private static final byte[] MSG_END = ">".getBytes();       // 1 byte

    private static final int TYPE_SIZE = 1;
    private static final int LEN_SIZE = 10;

    private static final int LEN_POS_START = MSG_START.length;
    private static final int LEN_POS_END = LEN_POS_START + LEN_SIZE;
    private static final int TYPE_POS = LEN_POS_END;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private long _length;
    private MessageType _type;
    private byte[] _body;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessage() {
        this.init();
    }

    public SocketMessage(final byte[] message) {
        this.init();
        this.parse(message);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName()).append(" {");
        sb.append("length=").append(_length).append(", ");
        sb.append("type=").append(_type.toString()).append(", ");
        sb.append("body=").append(new String(CollectionUtils.subArray(_body, 0, 15)) + "...");
        sb.append("}");

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    /**
     * Returns entire message size, headers included.
     * Shortcut to "bytes().length"
     */
    public long size() {
        return this.bytes().length;
    }

    /**
     * Get body message length.
     * No headers are included.
     */
    public long length() {
        return _length;
    }

    public MessageType type() {
        return _type;
    }

    public byte[] body() {
        return _body;
    }

    public SocketMessage body(final String value,
                              final String charset) throws UnsupportedEncodingException {
        this.body(value.getBytes(charset));
        return this;
    }

    public SocketMessage body(final String value) {
        if (StringUtils.hasText(value)) {
            _type = MessageType.Text;
            try {
                this.body(value.getBytes(CharEncoding.UTF_8));
            } catch (Throwable t) {
                this.body(value.getBytes());
            }
        }

        return this;
    }

    public SocketMessage body(final byte[] value) {
        this.setBody(value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public byte[] bytes() {
        try {
            return this.encode();
        } catch (Throwable t) {

        }
        return new byte[0];
    }

    public void parse(final byte[] message) {
        this.decode(message);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        this.body(new byte[0]);
        _type = MessageType.Undefined;
    }

    private void setBody(final byte[] data) {
        _body = data;
        _length = _body.length;
        // set type if not assigned
        if (MessageType.Undefined.equals(_type)) {
            _type = MessageType.Binary;
        }
    }

    private byte[] encodeLength() {
        final String s_len = StringUtils.fillString("" + this.length(), " ", LEN_SIZE);
        return s_len.getBytes();
    }

    private byte[] encodeType() {
        final byte[] response = new byte[1];
        response[0] = _type.getValue();
        ;
        return response;
    }

    private byte[] encode() {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // START
            out.write(MSG_START);

            // body length  ()
            out.write(this.encodeLength());
            // type
            out.write(this.encodeType());

            // body
            out.write(_body);

            // END
            out.write(MSG_END);

            return out.toByteArray();
        } catch (Throwable ignored) {
        }
        return new byte[0];
    }

    private void decode(final byte[] message) {
        // check if message is valid message
        if (message.length > MSG_START.length + MSG_END.length
                && hasStart(message)
                && hasEnd(message)) {

            // length
            final long length = decodeLength(message);
            // type
            _type = decodeType(message);

            if (length > -1 && !MessageType.Undefined.equals(_type)) {
                // message is valid
                _length = length;

                this.setBody(decodeBody(message));
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static boolean hasStart(final byte[] message) {
        if (message.length > 1) {
            return startWith(message, MSG_START);
        }
        return false;
    }

    public static boolean hasEnd(final byte[] message) {
        if (message.length > 1) {
            return endWith(message, MSG_END);
        }
        return false;
    }

    public static long decodeLength(final byte[] message) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, LEN_POS_START, LEN_POS_END);
            if (bytes.length == LEN_SIZE) {
                final String text = new String(bytes).trim();
                return Long.parseLong(text);
            }
        }
        return -1;
    }

    public static MessageType decodeType(final byte[] message) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, TYPE_POS, TYPE_POS + TYPE_SIZE);
            if (bytes.length == 1) {
                return MessageType.getEnum(bytes[0]);
            }
        }
        return MessageType.Undefined;
    }

    public static byte[] decodeBody(final byte[] message) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, TYPE_POS + TYPE_SIZE, message.length - MSG_END.length);
            return bytes;
        }
        return new byte[0];
    }

    private static boolean startWith(final byte[] message,
                                     final byte[] value) {
        return contains(message, value, 0);
    }

    private static boolean endWith(final byte[] message,
                                   final byte[] value) {
        return contains(message, value, message.length - value.length);
    }

    private static boolean contains(final byte[] message,
                                    final byte[] value,
                                    final int start_index) {
        if (message.length > start_index) {
            final byte[] token = CollectionUtils.subArray(message, start_index, start_index + value.length);
            if (token.length > 0 && token.length == value.length) {
                for (int i = 0; i < token.length; i++) {
                    if (token[i] != value[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

}
