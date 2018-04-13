package org.ly.commons.network.socket.basic.message.cipher;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.cryptograph.mixed.MixedCipher;
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

    //-- asymmetric  --//

    protected byte[] encryptAsym(final byte[] data, final String encode_key) throws Exception {
        return RSAHelper.encrypt(data, encode_key);
    }

    protected byte[] decryptAsym(final byte[] data, final String decode_key) throws Exception {
        return RSAHelper.decrypt(data, decode_key);
    }

    //-- mixed --//

    protected MixedCipher.Pack encryptMix(final byte[] data, final String encode_key) throws Exception {
        return MixedCipher.encrypt(data, encode_key);
    }

    protected byte[] decryptMix(final byte[] signature, final byte[] data, final String decode_key) throws Exception {
        final MixedCipher.Pack pack = new MixedCipher.Pack();
        pack.encodedSecret(signature);
        pack.encodedData(data);
        return MixedCipher.decrypt(pack, decode_key);
    }

    protected byte[] decryptMix(final MixedCipher.Pack pack, final String decode_key) throws Exception {
        return MixedCipher.decrypt(pack, decode_key);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }


}
