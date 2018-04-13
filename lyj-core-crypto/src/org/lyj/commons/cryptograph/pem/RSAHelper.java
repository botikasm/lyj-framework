package org.lyj.commons.cryptograph.pem;


import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
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
import java.security.spec.X509EncodedKeySpec;

/**
 * Simple utility to read private keys and generate public key
 */
public class RSAHelper
        implements IRSAConstants {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int KEY_SIZE = 1024;
    private static final String DEFAULT_CHIPHER = "RSA/ECB/PKCS1Padding";
    private static final String DEFAULT_KEY_FACTORY = "RSA";

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    //-- e n c r y p t / d e c r y p t --//

    public static byte[] encrypt(final byte[] data, final String public_key)
            throws BadPaddingException,
            IllegalBlockSizeException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException, IOException, InvalidKeySpecException {

        return encrypt(data, readRSAPublicKeyFromText(public_key));
    }

    public static byte[] encrypt(final byte[] data, final Key public_key)
            throws BadPaddingException,
            IllegalBlockSizeException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        if (null != data && data.length > 0) {
            final Cipher encrypt = Cipher.getInstance(DEFAULT_CHIPHER);
            // init with the *public key*!
            encrypt.init(Cipher.ENCRYPT_MODE, public_key);

            return encrypt.doFinal(data);
        }
        return new byte[0];
    }

    public static byte[] decrypt(final byte[] data, final String private_key)
            throws BadPaddingException,
            IllegalBlockSizeException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException, IOException, InvalidKeySpecException {

        return decrypt(data, readRSAPrivateKeyFromText(private_key));
    }

    public static byte[] decrypt(final byte[] data, final Key private_key)
            throws BadPaddingException,
            IllegalBlockSizeException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        if (null != data && data.length > 0) {
            final Cipher encrypt = Cipher.getInstance(DEFAULT_CHIPHER);
            // init with the *private key*!
            encrypt.init(Cipher.DECRYPT_MODE, private_key);

            return encrypt.doFinal(data);
        }
        return new byte[0];
    }

    //-- r e a d --//

    public static String readKeyFile(final String pem_file,
                                     final boolean remove_description) throws IOException {
        final String pem_string = FileUtils.readFileToString(new File(pem_file));
        return remove_description ? removeDescription(pem_string) : pem_string;
    }

    public static PrivateKey readRSAPrivateKeyFromFile(final String pem_file) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        final String pemString = FileUtils.readFileToString(new File(pem_file));

        return readRSAPrivateKeyFromText(pemString);
    }

    public static PublicKey readRSAPublicKeyFromFile(final String pem_file) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        final String pemString = FileUtils.readFileToString(new File(pem_file));

        return readRSAPublicKeyFromText(pemString);
    }

    public static PrivateKey readRSAPrivateKeyFromText(final String raw_pem_string) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        final String pem_string = raw_pem_string.contains(RSA_PRIVATE_KEY) ? raw_pem_string : BEGIN_RSA_PRIVATE_KEY.concat(raw_pem_string).concat(END_RSA_PRIVATE_KEY);
        final byte[] content = getContent(pem_string, RSA_PRIVATE_KEY);

        //final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
        final KeyFactory kf = KeyFactory.getInstance(DEFAULT_KEY_FACTORY);

        return kf.generatePrivate(keySpec);
    }

    public static PublicKey readRSAPublicKeyFromText(final String raw_pem_string) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        final String pem_string = raw_pem_string.contains(RSA_PUBLIC_KEY) ? raw_pem_string : BEGIN_RSA_PUBLIC_KEY.concat(raw_pem_string).concat(END_RSA_PUBLIC_KEY);
        final byte[] content = getContent(pem_string, RSA_PUBLIC_KEY);

        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(content);
        final KeyFactory kf = KeyFactory.getInstance(DEFAULT_KEY_FACTORY);

        return kf.generatePublic(keySpec);
    }

    //-- g e n e r a t e --//

    public static KeyPair generateRSAKeyPair()
            throws NoSuchAlgorithmException,
            NoSuchProviderException {

        // Security.addProvider(new BouncyCastleProvider());
        final KeyPairGenerator generator = KeyPairGenerator.getInstance(DEFAULT_KEY_FACTORY);
        generator.initialize(KEY_SIZE);

        return generator.generateKeyPair();
    }

    public static PublicKey generatePublicKey(final RSAPrivateCrtKey private_key)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException {

        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(private_key.getModulus(),
                private_key.getPublicExponent());

        final KeyFactory keyFactory = KeyFactory.getInstance(DEFAULT_KEY_FACTORY);
        return keyFactory.generatePublic(publicKeySpec);
    }

    //-- i o --//

    public static String[] writePemFiles(final KeyPair key_pair,
                                         final String root)
            throws IOException {

        final String[] response = new String[2];
        // private
        response[0] = PathUtils.getAbsolutePath(PathUtils.concat(root, "private.pem"));
        //public
        response[1] = PathUtils.getAbsolutePath(PathUtils.concat(root, "public.pem"));

        FileUtils.mkdirs(response[0]); // ensure parent exusts

        // write private
        writePemFile(key_pair.getPrivate(), RSA_PRIVATE_KEY, response[0]);

        // write public
        writePemFile(key_pair.getPublic(), RSA_PUBLIC_KEY, response[1]);

        return response;
    }

    public static String[] writePemFiles(final KeyPair key_pair,
                                         final String private_key_file_name,
                                         final String public_key_file_name)
            throws IOException {

        final String[] response = new String[2];
        // private
        response[0] = private_key_file_name;
        //public
        response[1] = public_key_file_name;

        FileUtils.mkdirs(response[0]); // ensure parent exusts

        // write private
        writePemFile(key_pair.getPrivate(), RSA_PRIVATE_KEY, response[0]);

        // write public
        writePemFile(key_pair.getPublic(), RSA_PUBLIC_KEY, response[1]);

        return response;
    }


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

    public static String toString(final PublicKey key) {
        return writePemString(key, RSA_PUBLIC_KEY);
    }

    public static String toString(final PrivateKey key) {
        return writePemString(key, RSA_PRIVATE_KEY);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static PemObject getPemObject(final String pem_string,
                                          final String description) throws IOException {
        final PemReader reader = new PemReader(new StringReader(pem_string));
        PemObject pem_object;
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
