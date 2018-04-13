package org.ly.commons.network.socket.basic.message.cipher.impl;

import org.ly.commons.network.socket.basic.SocketSettings;
import org.ly.commons.network.socket.basic.message.SocketMessagePublicKeyCache;
import org.ly.commons.network.socket.basic.message.cipher.AbstractMessageCipher;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.cryptograph.mixed.MixedCipher;
import org.lyj.commons.util.StringUtils;

public class ServerCipher
        extends AbstractMessageCipher {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final SocketMessagePublicKeyCache _encode_key_cache;
    private String _decode_key; // [private] used to decode inbound messages

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    public ServerCipher() {
        this("server");
    }

    public ServerCipher(String name) {
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

    public ServerCipher encodeKey(final String owner_id,
                                  final String value) {
        if (StringUtils.hasText(value)) {
            _encode_key_cache.setKey(owner_id, value);
        }
        return this;
    }

    @Override
    public void decode(final SocketMessage message) throws Exception {
        if (!message.isHandShake() && message.hasSignature()) {

            final String encode_key = this.getEncodeKey(message.ownerId());
            if (StringUtils.hasText(encode_key)) {

                if (SocketSettings.CHUNK_SIZE > 100) {
                    //-- mixed encryption --//
                    message.body(super.decryptMix(message.signature(), message.body(), _decode_key));
                } else {
                    // encrypt the body using a public key
                    message.body(super.decryptAsym(message.body(), _decode_key));
                }
            }

        }
    }

    @Override
    public void encode(final SocketMessage message,
                       final String key_index) throws Exception {
        if (!message.isHandShake()) {

            final String encode_key = this.getEncodeKey(key_index);
            if (StringUtils.hasText(encode_key)) {

                if (SocketSettings.CHUNK_SIZE > 100) {
                    //-- mixed encryption --//
                    final MixedCipher.Pack pack = super.encryptMix(message.body(), encode_key);
                    message.body(pack.encodedData());
                    message.signature(pack.encodedSecret());
                } else {
                    // encrypt the body using a public key
                    message.body(super.encryptAsym(message.body(), encode_key));
                    // write public key for encrypted response
                    message.signature(super.signature());
                }
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
