package org.lyj.commons.cryptograph.mixed;

import org.lyj.commons.cryptograph.AESCipher;
import org.lyj.commons.cryptograph.pem.RSAHelper;
import org.lyj.commons.util.ByteUtils;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Use this cipher helper to encode large amount of data using a mixed approach:
 * - Use symmetric key to encode data
 * - Use asymmetric key to encode key
 */
public class MixedCipher {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final int KEY_SIZE = 128;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static Pack encrypt(final File file,
                               final PublicKey public_key) throws Exception {
        final byte[] data = ByteUtils.getBytes(file);
        return encrypt(data, public_key);
    }

    public static Pack encrypt(final byte[] data,
                               final PublicKey public_key) throws Exception {
        final SecretKey secret = AESCipher.createKey(KEY_SIZE);
        final AESCipher cipher = new AESCipher(secret);
        final byte[] encoded_data = cipher.encrypt(data);
        final byte[] encoded_key = RSAHelper.encrypt(secret.getEncoded(), public_key);

        final Pack response = new Pack();
        response.encodedData(encoded_data);
        response.encodedKey(encoded_key);

        return response;
    }

    public static byte[] decrypt(final Pack pack,
                                 final PrivateKey private_key) throws Exception {
        final SecretKey secret = AESCipher.createKey(RSAHelper.decrypt(pack.encodedKey(), private_key));
        final AESCipher cipher = new AESCipher(secret);
        return cipher.decrypt(pack.encodedData());
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class Pack {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private byte[] _encoded_key;
        private byte[] _encoded_data;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public Pack() {

        }

        public Pack(final byte[] encoded_key, final byte[] encoded_data) {
            _encoded_key = encoded_key;
            _encoded_data = encoded_data;
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public byte[] encodedKey() {
            return _encoded_key;
        }

        public void encodedKey(final byte[] value) {
            _encoded_key = value;
        }

        public byte[] encodedData() {
            return _encoded_data;
        }

        public void encodedData(final byte[] value) {
            _encoded_data = value;
        }

    }

}
