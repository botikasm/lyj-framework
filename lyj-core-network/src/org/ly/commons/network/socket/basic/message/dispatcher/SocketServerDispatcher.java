package org.ly.commons.network.socket.basic.message.dispatcher;

import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.SocketMessagePublicKeyCache;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.util.StringUtils;

import java.nio.channels.AsynchronousSocketChannel;

public class SocketServerDispatcher
        extends SocketMessageDispatcher {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final SocketMessagePublicKeyCache _encode_key_cache;
    private String _decode_key; // [private] used to decode inbound messages

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    public SocketServerDispatcher() {
        this("server");
    }

    public SocketServerDispatcher(String name) {
        super(name);
        _encode_key_cache = new SocketMessagePublicKeyCache(name);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String encodeKey(final String owner_id) {
        return _encode_key_cache.getKey(owner_id);
    }

    public SocketServerDispatcher encodeKey(final String owner_id,
                                            final String value) {
        if (StringUtils.hasText(value)) {
            _encode_key_cache.setKey(owner_id, value);
        }
        return this;
    }

    @Override
    public void write(AsynchronousSocketChannel socket, SocketContext context, SocketMessage message) throws Exception {
        super.write(socket, context, message);
    }

    @Override
    public void decode(final SocketMessage message) throws Exception {
        if (!message.isHandShake() && message.hasSignature()) {

            final String encode_key = this.getEncodeKey(message.ownerId());
            if (StringUtils.hasText(encode_key)) {
                // encrypt the body using a public key
                message.body(super.decrypt(message.body(), _decode_key));
            }

        }
    }

    @Override
    public void encode(final SocketMessage message,
                       final String key_index) throws Exception {
        if (!message.isHandShake()) {

            final String encode_key = this.getEncodeKey(key_index);
            if (StringUtils.hasText(encode_key) && message.hasSignature()) {

                // encrypt the body using a public key
                message.body(super.encrypt(message.body(), encode_key));

                // write public key for encrypted response
                message.signature(this.signature());

            }

        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        _decode_key = super.keys().privateKeyString();
    }

    private String getEncodeKey(final String owner_id) {
        final String encode_key = _encode_key_cache.getKey(owner_id);
        return encode_key; // _encode_key_cache.get(owner_id).item().publicKey();
    }

}
