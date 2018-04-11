package org.ly.commons.network.socket.basic.message.cipher;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.message.cipher.impl.ClientCipher;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.basic.message.cipher.KeyManager;
import org.lyj.commons.cryptograph.pem.RSAHelper;

public abstract class AbstractMessageCipher
        extends SocketLogger {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "./keyStore";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _name;
    private final KeyManager _keys;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractMessageCipher(final String name) {
        _name = name;
        _keys = new KeyManager(ROOT, name);

        this.init();
    }

    public abstract void decode(final SocketMessage message) throws Exception;

    public abstract void encode(final SocketMessage message, final String owner_id) throws Exception;


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String name() {
        return _name;
    }

    public String publicKey() {
        return this.keys().publicKeyString();
    }

    public String privateKey() {
        return this.keys().privateKeyString();
    }

    public byte[] signature() {
        return this.publicKey().getBytes();
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected KeyManager keys() {
        return _keys;
    }

    protected byte[] encrypt(final byte[] data, final String encode_key) throws Exception {
        final byte[] encrypted = RSAHelper.encrypt(data, encode_key);
        return encrypted;
    }

    protected byte[] decrypt(final byte[] data, final String decode_key) throws Exception {
        final byte[] decrypted = RSAHelper.decrypt(data, decode_key);
        return decrypted;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }


}
