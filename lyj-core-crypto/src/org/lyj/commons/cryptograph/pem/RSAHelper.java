package org.lyj.commons.cryptograph.pem;


import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Simple utility to read private keys and generate public key
 */
public class RSAHelper
        implements IRSAConstants {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int KEY_SIZE = 1024;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    //-- e n c r y p t / d e c r y p t --//

    public static byte[] encrypt(final byte[] data, final Key public_key)
            throws BadPaddingException,
            IllegalBlockSizeException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException {

        final Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // init with the *public key*!
        encrypt.init(Cipher.ENCRYPT_MODE, public_key);

        return encrypt.doFinal(data);
    }

    public static byte[] decrypt(final byte[] data, final Key private_key)
            throws BadPaddingException,
            IllegalBlockSizeException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException {

        final Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // init with the *private key*!
        encrypt.init(Cipher.DECRYPT_MODE, private_key);

        return encrypt.doFinal(data);
    }

    //-- r e a d --//

    public static PrivateKey readRSAPrivateKeyFromFile(final String pem_file) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        final String pemString = FileUtils.readFileToString(new File(pem_file));

        return readRSAPrivateKeyFromText(pemString);
    }

    public static PrivateKey readRSAPrivateKeyFromText(final String pem_string) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        final byte[] content = getContent(pem_string, RSA_PRIVATE_KEY);

        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
        final KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(keySpec);
    }

    //-- g e n e r a t e --//

    public static KeyPair generateRSAKeyPair()
            throws NoSuchAlgorithmException,
            NoSuchProviderException {

        // Security.addProvider(new BouncyCastleProvider());
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);

        return generator.generateKeyPair();
    }

    public static PublicKey generatePublicKey(final RSAPrivateCrtKey private_key)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException {

        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(private_key.getModulus(),
                private_key.getPublicExponent());

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }

    //-- i o --//

    /**
     * Write a pem file containing private or public key
     *
     * @param key         Key object to write to file
     * @param description "RSA PRIVATE KEY", "RSA PUBLIC KEY"
     * @param filename    name of file to write
     */
    public static void writePemFile(final Key key,
                                    final String description,
                                    final String filename)
            throws FileNotFoundException,
            IOException {

        final PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(filename)));
        try {
            final PemObject pem_object = new PemObject(description, key.getEncoded());
            pemWriter.writeObject(pem_object);
        } finally {
            pemWriter.close();
        }
    }

    public static String writePemString(final Key key,
                                        final String description) {

        final StringWriter out = new StringWriter();
        try (final PemWriter pemWriter = new PemWriter(out);) {
            final PemObject pem_object = new PemObject(description, key.getEncoded());
            pemWriter.writeObject(pem_object);
        } finally {
            out.flush();
            return StringUtils.hasText(description)
                    ? out.toString()
                    : removeDescription(out.toString());
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static PemObject getPemObject(final String pem_string,
                                          final String description) throws IOException {
        final PemReader reader = new PemReader(new StringReader(pem_string));
        PemObject pem_object = null;
        byte[] decoded = new byte[0];
        while ((pem_object = reader.readPemObject()) != null) {
            if (pem_object.getType().equalsIgnoreCase(StringUtils.hasText(description)
                    ? description
                    : RSA_PRIVATE_KEY)) {
                return pem_object;
            }
        }
        return null;
    }

    private static byte[] getContent(final String pem_string,
                                     final String description) throws IOException {
        final PemObject pem_object = getPemObject(pem_string, description);
        return null != pem_object ? pem_object.getContent() : new byte[0];
    }

    private static String removeDescription(final String text) {
        if (StringUtils.hasText(text)) {
            if (text.contains(RSA_PRIVATE_KEY)) {
                return text.replace(BEGIN_RSA_PRIVATE_KEY, "").replace(END_RSA_PRIVATE_KEY, "");
            } else if (text.contains(RSA_PUBLIC_KEY)) {
                return text.replace(BEGIN_RSA_PUBLIC_KEY, "").replace(END_RSA_PUBLIC_KEY, "");
            } else {
                return text.replace(BEGIN, "").replace(END, "");
            }
        }

        return text;
    }

}
