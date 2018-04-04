package org.ly.commons.network.socket.basic.client;

import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.SocketMessageDispatcher;
import org.ly.commons.network.socket.basic.message.SocketMessagePublicKeyCache;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.util.StringUtils;

import java.nio.channels.AsynchronousSocketChannel;

public class SocketBasicClientDispatcher
        extends SocketMessageDispatcher {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _encode_key; // [public] used to encode outbound message
    private String _decode_key; // [private] used to decode inbound messages

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicClientDispatcher() {
        this("client");
    }

    public SocketBasicClientDispatcher(String name) {
        super(name);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


   public String encodeKey() {
       return _encode_key;
   }

   public SocketBasicClientDispatcher encodeKey(final String value) {
       _encode_key = value;

       return this;
   }


    @Override
    public SocketMessage read(AsynchronousSocketChannel socket, SocketContext context) throws Exception {
        return super.read(socket, context);
    }

    @Override
    public void decode(final SocketMessage message) throws Exception {
        try {
            if (!message.isHandShake() && StringUtils.hasText(message.signature().trim())) {

                final String encode_key = this.getEncodeKey(message.ownerId());
                if (StringUtils.hasText(encode_key)) {
                    // encrypt the body using a public key
                    message.body(super.decrypt(message.body(), _decode_key));
                }

            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void encode(final SocketMessage message, final String owner_id) throws Exception {
        if (!message.isHandShake()) {

            final String encode_key = this.getEncodeKey(owner_id);
            if (StringUtils.hasText(encode_key) && StringUtils.hasText(message.signature().trim())) {

                // encrypt the body using a public key
                message.body(super.encrypt(message.body(), encode_key));

                // write public key for encrypted response
                message.signature(super.signature());

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
        final String encode_key = _encode_key; // _encode_key_cache.getKey(owner_id);
        return encode_key;
    }

}
