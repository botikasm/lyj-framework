package org.ly.commons.network.socket.basic.message.impl;

import org.ly.commons.network.socket.basic.message.SocketMessageHeader;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.cryptograph.mixed.MixedCipher;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.ByteUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
        File((byte) 2),
        Handshake((byte) 98),
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
    private static final int BODY_LENGHT_SIZE = 10;
    private static final int HEADER_LENGHT_SIZE = BODY_LENGHT_SIZE;
    private static final int HASH_SIZE = 32;
    private static final int SIGNATURE_SIZE = MixedCipher.KEY_SIZE; //128;
    private static final int OWNER_SIZE = HASH_SIZE;

    private static final int LEN_POS_START = MSG_START.length;
    private static final int LEN_POS_END = LEN_POS_START + BODY_LENGHT_SIZE;
    private static final int HEADER_LENGHT_POS_START = LEN_POS_END;
    private static final int HEADER_LENGHT_POS_END = HEADER_LENGHT_POS_START + HEADER_LENGHT_SIZE;
    private static final int TYPE_POS = HEADER_LENGHT_POS_END;
    private static final int HASH_POS_START = TYPE_POS + 1;
    private static final int HASH_POS_END = HASH_POS_START + HASH_SIZE;
    private static final int SIGNATURE_POS_START = HASH_POS_END;
    private static final int SIGNATURE_POS_END = SIGNATURE_POS_START + SIGNATURE_SIZE;
    private static final int OWNER_POS_START = SIGNATURE_POS_END;
    private static final int OWNER_POS_END = OWNER_POS_START + OWNER_SIZE;

    private static final int HEADERS_POS_START = OWNER_POS_END;
    private static final int BODY_POS_START = OWNER_POS_END;

    private static final int HEADERS_SIZE = MSG_START.length + MSG_END.length +
            TYPE_SIZE + BODY_LENGHT_SIZE + HEADER_LENGHT_SIZE + HASH_SIZE + SIGNATURE_SIZE + OWNER_SIZE;

    private static final byte[] UNSIGNED = StringUtils.fillString("", " ", SIGNATURE_SIZE).getBytes();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _owner_id;
    private long _body_length;
    // private long _header_length;
    private MessageType _type;
    // hash (calculated with MD5 on body)
    private String _hash;
    private byte[] _signature;
    private SocketMessageHeader _headers;
    private byte[] _body;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessage(final String owner_id) {
        _owner_id = MD5.encode(owner_id);
        this.init();
    }

    public SocketMessage(final byte[] message) {
        this.init();
        this.parse(message);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName()).append(" [");
        sb.append("owner_id=").append(_owner_id).append(", ");
        sb.append("body_length=").append(_body_length).append(", ");
        sb.append("headers_length=").append(this.headerLength()).append(", ");
        sb.append("type=").append(_type.toString()).append(", ");
        sb.append("hash=").append(this.hash()).append(", ");
        sb.append("signature=").append(StringUtils.leftStr(new String(this.signature()).trim(), 15, true)).append(", ");
        sb.append("headers=").append(StringUtils.leftStr(_headers.toString(), 15, true)).append(", ");
        sb.append("body=").append(new String(CollectionUtils.subArray(_body, 0, 15)) + "...");
        sb.append("]");

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String ownerId() {
        return _owner_id;
    }

    /**
     * Returns entire message size, headers included.
     */
    public long size() {
        return sizeOfMessage(this.body(), _headers);
    }

    /**
     * Get body message length.
     * No headers are included.
     */
    public long length() {
        return _body_length;
    }

    public long headerLength() {
        return _headers.length();
    }

    public MessageType type() {
        return _type;
    }

    public SocketMessage type(final MessageType value) {
        _type = value;
        return this;
    }

    public String hash() {
        return _hash;
    }

    public byte[] signature() {
        return _signature;
    }

    public SocketMessage signature(final byte[] value) {
        _signature = CollectionUtils.resizeArray(value, SIGNATURE_SIZE); // StringUtils.fillString(StringUtils.leftStr(value, SIGNATURE_SIZE), " ", SIGNATURE_SIZE);
        return this;
    }

    public SocketMessageHeader headers() {
        return _headers;
    }

    public byte[] body() {
        return null != _body ? _body : new byte[0];
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

    public SocketMessage body(final File file) throws IOException {
        if (null != file) {
            _type = MessageType.File;
            this.body(ByteUtils.getBytes(file));
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

    public boolean hasSignature() {
        return null != _signature && _signature.length > 0 && new String(_signature).trim().length() > 0;
    }

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

    public boolean isValid() {
        try {
            return getHashFrom(_body).equalsIgnoreCase(this.hash());
        } catch (Throwable ignored) {
        }
        return false;
    }

    public boolean isHandShake() {
        return _type.equals(MessageType.Handshake);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        _headers = new SocketMessageHeader();
        this.body(new byte[0]);
        _type = MessageType.Undefined;
        if (null == _signature || _signature.length == 0) {
            this.signature(UNSIGNED);
        }
    }

    private void setBody(final byte[] data) {
        _body = data;
        _body_length = _body.length;
        _hash = this.getHashFromBody();
        // set type if not assigned
        if (MessageType.Undefined.equals(_type)) {
            _type = MessageType.Binary;
        }
    }

    private String getHashFromBody() {
        return getHashFrom(_body);
    }

    private byte[] encodeBodyLength() {
        final String s_len = StringUtils.fillString("" + this.length(), " ", BODY_LENGHT_SIZE);
        return s_len.getBytes();
    }

    private byte[] encodeHeadersLength() {
        final String s_len = StringUtils.fillString("" + this.headerLength(), " ", HEADER_LENGHT_SIZE);
        return s_len.getBytes();
    }

    private byte[] encodeType() {
        final byte[] response = new byte[1];
        response[0] = _type.getValue();

        return response;
    }

    private byte[] encodeHash() {
        //final String s_hash = StringUtils.leftStr(MD5.encode(_body), HASH_SIZE);
        return _hash.getBytes();
    }

    private byte[] encodeOwnerId() {
        return _owner_id.getBytes();
    }

    private byte[] encodeSignature() {
        //final String s_signature = StringUtils.leftStr(MD5.encode(_signature), HASH_SIZE);
        return _signature;
    }

    private byte[] encode() {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // START
            out.write(MSG_START);

            // body length
            out.write(this.encodeBodyLength());
            // header length
            out.write(this.encodeHeadersLength());
            // type
            out.write(this.encodeType());
            // hash
            out.write(this.encodeHash());
            // signature
            out.write(this.encodeSignature());
            // owner_id
            out.write(this.encodeOwnerId());

            // headers
            out.write(_headers.getBytes());

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
            final long header_length = decodeHeaderLength(message);
            final long body_length = decodeBodyLength(message);
            // type
            _type = decodeType(message);

            if (body_length > -1 && !MessageType.Undefined.equals(_type)) {
                // message is valid
                _body_length = body_length;

                _signature = decodeSignature(message);
                _hash = decodeHash(message);
                _owner_id = decodeOwnerId(message);

                // set the header
                _headers = new SocketMessageHeader(new String(decodeHeaders(message, (int) header_length)));

                // set the body
                this.setBody(decodeBody(message, HEADERS_POS_START + (int) header_length));

            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static boolean isComplete(final byte[] bytes) {
        if (SocketMessage.hasStart(bytes)) {
            // length
            final long body_length = SocketMessage.decodeBodyLength(bytes);
            final long header_length = SocketMessage.decodeHeaderLength(bytes);
            if (body_length > -1 && header_length > -1) {
                // type
                final SocketMessage.MessageType type = SocketMessage.decodeType(bytes);
                if (!SocketMessage.MessageType.Undefined.equals(type)) {
                    // header integrity
                    final long real_header_length = SocketMessage.decodeHeaders(bytes, (int) header_length).length;
                    if (real_header_length == header_length) {
                        // body integrity
                        final long real_body_length = SocketMessage.decodeBody(bytes, HEADERS_POS_START + (int) header_length).length;
                        if (real_body_length == body_length) {
                            // message is closed
                            return SocketMessage.hasEnd(bytes);
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Calculate the size of a message adding headers length to body length.
     * This method is used to predict the size of a message.
     */
    public static long sizeOfMessage(final byte[] body, final SocketMessageHeader header) {
        return HEADERS_SIZE + body.length + (null != header ? header.length() : 0);
    }

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

    public static long decodeBodyLength(final byte[] message) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, LEN_POS_START, LEN_POS_END);
            if (bytes.length == BODY_LENGHT_SIZE) {
                final String text = new String(bytes).trim();
                return Long.parseLong(text);
            }
        }
        return -1;
    }

    public static long decodeHeaderLength(final byte[] message) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, HEADER_LENGHT_POS_START, HEADER_LENGHT_POS_END);
            if (bytes.length == HEADER_LENGHT_SIZE) {
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

    public static byte[] decodeSignature(final byte[] message) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, SIGNATURE_POS_START, SIGNATURE_POS_END);
            if (bytes.length == SIGNATURE_SIZE) {
                return bytes;
            }
        }
        return UNSIGNED;
    }

    public static String decodeHash(final byte[] message) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, HASH_POS_START, HASH_POS_END);
            if (bytes.length == HASH_SIZE) {
                return new String(bytes);
            }
        }
        return getHashFrom(new byte[0]);
    }

    public static String decodeOwnerId(final byte[] message) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, OWNER_POS_START, OWNER_POS_END);
            if (bytes.length == OWNER_SIZE) {
                return new String(bytes);
            }
        }
        return getHashFrom(new byte[0]);
    }

    public static byte[] decodeHeaders(final byte[] message, final int headers_length) {
        if (message.length > 0) {
            final byte[] bytes = CollectionUtils.subArray(message, HEADERS_POS_START, HEADERS_POS_START + headers_length);
            return bytes;
        }
        return new byte[0];
    }

    public static byte[] decodeBody(final byte[] message, final int body_start) {
        if (message.length > 0) {
            //final byte[] bytes = CollectionUtils.subArray(message, BODY_POS_START, message.length - MSG_END.length);
            final byte[] bytes = CollectionUtils.subArray(message, body_start, message.length - MSG_END.length);
            return bytes;
        }
        return new byte[0];
    }

    private static String getHashFrom(final byte[] value) {
        return StringUtils.leftStr(MD5.encode(value), HASH_SIZE);
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
