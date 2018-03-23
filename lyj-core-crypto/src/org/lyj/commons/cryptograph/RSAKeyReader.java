package org.lyj.commons.cryptograph;


import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Simple utility to read private keys and generate public key
 */
public class RSAKeyReader {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String BEGIN_RSA_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n";
    public static final String END_RSA_PRIVATE_KEY = "\n-----END RSA PRIVATE KEY-----";

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public PrivateKey RSAPrivateKeyFromFile(final String pem_file) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        final String pemString = FileUtils.readFileToString(new File(pem_file));

        return RSAPrivateKeyFromText(pemString);
    }

    public PrivateKey RSAPrivateKeyFromText(final String pem_string) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        final PemReader reader = new PemReader(new StringReader(pem_string));
        PemObject crt = null;
        byte[] decoded = new byte[0];
        while ((crt = reader.readPemObject()) != null) {
            if(crt.getType().equalsIgnoreCase("RSA PRIVATE KEY")){
                decoded = crt.getContent();
            }
        }

        final String pemString = grabRSAPrivateKey(pem_string);

        //decoded = org.lyj.commons.lang.Base64.decode(pemString); //Base64.getDecoder().decode(pemString);

        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        final KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(keySpec);
    }


    public PublicKey generatePublicKey(final RSAPrivateCrtKey private_key)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        //RSAPrivateCrtKey privk = (RSAPrivateCrtKey)private_key;

        RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(private_key.getModulus(),
                private_key.getPublicExponent());

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String grabRSAPrivateKey(final String text) {
        if (StringUtils.hasText(text)) {
            final int start_index = text.indexOf(BEGIN_RSA_PRIVATE_KEY);
            final int end_index = text.indexOf(END_RSA_PRIVATE_KEY, start_index + BEGIN_RSA_PRIVATE_KEY.length());
            if (start_index > -1 && end_index > start_index) {
                final int s_i = start_index + BEGIN_RSA_PRIVATE_KEY.length();
                final int e_i = end_index;
                return text.substring(s_i, e_i);
            }
            return text;
        }
        return "";
    }

}
