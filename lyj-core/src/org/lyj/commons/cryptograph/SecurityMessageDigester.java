/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * SecurityMessageDigester.java
 *
 *
 */
package org.lyj.commons.cryptograph;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public final class SecurityMessageDigester
        implements ICryptographConstants {

    private AlgorithmMessageDigest _algorithm = AlgorithmMessageDigest.MD5;
    private MessageDigest _md = null;

    /**
     * Creates a new instance of SecurityMessageDigester
     */
    public SecurityMessageDigester() throws NoSuchAlgorithmException {
        this(AlgorithmMessageDigest.MD5);
    }

    public SecurityMessageDigester(final AlgorithmMessageDigest algorithm)
            throws NoSuchAlgorithmException {
        setAlgorithm(algorithm);
    }

    public AlgorithmMessageDigest getAlgorithm() {
        return _algorithm;
    }

    public void setAlgorithm(AlgorithmMessageDigest algorithm) throws NoSuchAlgorithmException {
        this._algorithm = algorithm;
        // try algorithm validity
        _md = null;
        _md = MessageDigest.getInstance(_algorithm.toString());
    }

    public String getEncodedText(final String clearText) {
        if (null == _md) {
            return clearText;
        }
        // Reset the digester for further use.
        _md.reset();
        _md.update(clearText.getBytes());

        return HexString.bufferToHex(_md.digest());
    }

    public String getEncodedText(final byte[] bytes) {
        if (null == _md) {
            return "";
        }
        // Reset the digester for further use.
        _md.reset();
        _md.update(bytes);

        return HexString.bufferToHex(_md.digest());
    }

    public boolean testPassword(String clearTextTestPassword,
                                String encodedActualPassword) {
        String encodedTestPassword = this.getEncodedText(
                clearTextTestPassword);

        return (encodedTestPassword.equals(encodedActualPassword));
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static String encodeMD5(final String text,
                                   final String opvalue) {
        try {
            final SecurityMessageDigester instance = new SecurityMessageDigester();
            return instance.getEncodedText(text);
        } catch (Throwable t) {
            return opvalue;
        }
    }

    public static String encodeMD5(final String text)
            throws NoSuchAlgorithmException {
        final SecurityMessageDigester instance = new SecurityMessageDigester();
        return instance.getEncodedText(text);
    }

    public static String encodeMD2(final String text)
            throws NoSuchAlgorithmException {
        final SecurityMessageDigester instance = new SecurityMessageDigester();
        instance.setAlgorithm(AlgorithmMessageDigest.MD2);
        return instance.getEncodedText(text);
    }

    public static String encodeSHA(final String text)
            throws NoSuchAlgorithmException {
        final SecurityMessageDigester instance = new SecurityMessageDigester();
        instance.setAlgorithm(AlgorithmMessageDigest.SHA);
        return instance.getEncodedText(text);
    }

    public static String encodeSHA_1(final String text)
            throws NoSuchAlgorithmException {
        final SecurityMessageDigester instance = new SecurityMessageDigester();
        instance.setAlgorithm(AlgorithmMessageDigest.SHA_1);
        return instance.getEncodedText(text);
    }

    public static String encodeSHA_256(final String value, final String secret) {
        String result;
        try {
            final Mac hmacSHA512 = Mac.getInstance("HmacSHA256");
            final SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            hmacSHA512.init(secretKeySpec);

            byte[] digest = hmacSHA512.doFinal(value.getBytes());
            BigInteger hash = new BigInteger(1, digest);
            result = hash.toString(16);
            if ((result.length() % 2) != 0) {
                result = "0" + result;
            }
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Problemas calculando HMAC", ex);
        }
        return result;
    }

    public static String encodeSHA_512(final String value, final String secret) {
        String result;
        try {
            final Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            final SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
            hmacSHA512.init(secretKeySpec);

            byte[] digest = hmacSHA512.doFinal(value.getBytes());
            BigInteger hash = new BigInteger(1, digest);
            result = hash.toString(16);
            if ((result.length() % 2) != 0) {
                result = "0" + result;
            }
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Problemas calculando HMAC", ex);
        }
        return result;
    }


}
