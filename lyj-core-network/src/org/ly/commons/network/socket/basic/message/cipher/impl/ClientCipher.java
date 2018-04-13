package org.ly.commons.network.socket.basic.message.cipher.impl;

import org.ly.commons.network.socket.basic.SocketSettings;
import org.ly.commons.network.socket.basic.message.cipher.AbstractMessageCipher;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.cryptograph.mixed.MixedCipher;
import org.lyj.commons.util.StringUtils;

public class ClientCipher
        extends AbstractMessageCipher {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _encode_key; // [public] used to encode outbound message
    private String _decode_key; // [private] used to decode inbound messages

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ClientCipher() {
        this("client");
    }

    public ClientCipher(String name) {
        super(name);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public String encodeKey() {
        return _encode_key;
    }

    public ClientCipher encodeKey(final String value) {
        _encode_key = value;

        return this;
    }

    @Override
    public void decode(final SocketMessage message) throws Exception {
        try {
            if (!message.isHandShake() && message.hasSignature()) {

                final String encode_key = this.encodeKey();
                if (StringUtils.hasText(encode_key)) {

                    if(SocketSettings.CHUNK_SIZE>100) {
                        //-- mixed --//
                        final MixedCipher.Pack pack = new MixedCipher.Pack();
                        pack.encodedSecret(message.signature());
                        pack.encodedData(message.body());
                        message.body(super.decryptMix(pack, _decode_key));
                    } else {
                        // encrypt the body using a public key
                        message.body(super.decryptAsym(message.body(), _decode_key));
                    }
                }

            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void encode(final SocketMessage message, final String owner_id) throws Exception {
        if (!message.isHandShake()) {

            final String encode_key = this.encodeKey();
            if ( StringUtils.hasText(encode_key) ) {

                if(SocketSettings.CHUNK_SIZE>100){
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


}
