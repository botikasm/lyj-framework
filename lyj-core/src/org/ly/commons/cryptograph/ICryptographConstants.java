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
 * ICryptographConstants.java
 *
 */

package org.ly.commons.cryptograph;

/**
 * @author Angelo Geminiani ( angelo.geminiani@gmail.com )
 */
public interface ICryptographConstants {

    /**
     * Supported Algorithm for MessageDigest. Type: MessageDigest
     */
    public enum AlgorithmMessageDigest {
        MD2("MD2"),
        MD5("MD5"),
        SHA("SHA"),
        SHA_1("SHA-1");
        private final String _value;

        AlgorithmMessageDigest(String value) {
            _value = value;
        }

        public String toString() {
            return _value;
        }
    }

    /**
     * Supported Algorithm for Digital Signature. Type: Signature
     */
    public enum AlgorithmDigitalSignature {
        SHA1withDSA("SHA1withDSA"),
        MD2withRSA("MD2withRSA"),
        MD5withRSA("MD5withRSA "),
        SHA1withRSA("SHA1withRSA-1");
        private final String _value;

        AlgorithmDigitalSignature(String value) {
            _value = value;
        }

        public String toString() {
            return _value;
        }
    }

    /**
     * Supported Algorithm for Key Pair. Type: KeyPairGenerator
     */
    public enum AlgorithmKeyPair {
        DSA("DSA"),
        RSA("RSA");
        private final String _value;

        AlgorithmKeyPair(String value) {
            _value = value;
        }

        public String toString() {
            return _value;
        }
    }

}
