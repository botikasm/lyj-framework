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
package org.ly.commons.cryptograph;

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

    public String getEncodedText(String clearText) {
        if (null == _md) {
            return clearText;
        }
        // Reset the digester for further use.
        _md.reset();
        _md.update(clearText.getBytes());

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
    private static SecurityMessageDigester _instance;

    public static SecurityMessageDigester getInstance()
            throws NoSuchAlgorithmException {
        if (null == _instance) {
            _instance = new SecurityMessageDigester(AlgorithmMessageDigest.MD5);
        }
        return _instance;
    }

    public static String encodeMD5(final String text,
                                   final String opvalue) {
        try {
            final SecurityMessageDigester instance = getInstance();
            return instance.getEncodedText(text);
        } catch (Throwable t) {
            return opvalue;
        }
    }

    public static String encodeMD5(final String text)
            throws NoSuchAlgorithmException {
        SecurityMessageDigester instance = getInstance();
        return instance.getEncodedText(text);
    }

    public static String encodeMD2(final String text)
            throws NoSuchAlgorithmException {
        SecurityMessageDigester instance = getInstance();
        instance.setAlgorithm(AlgorithmMessageDigest.MD2);
        return instance.getEncodedText(text);
    }

    public static String encodeSHA(final String text)
            throws NoSuchAlgorithmException {
        SecurityMessageDigester instance = getInstance();
        instance.setAlgorithm(AlgorithmMessageDigest.SHA);
        return instance.getEncodedText(text);
    }

    public static String encodeSHA_1(final String text)
            throws NoSuchAlgorithmException {
        SecurityMessageDigester instance = getInstance();
        instance.setAlgorithm(AlgorithmMessageDigest.SHA_1);
        return instance.getEncodedText(text);
    }
}
