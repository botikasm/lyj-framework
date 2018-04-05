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
 *
 */
package org.lyj.commons.cryptograph;

import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Simple AES encoder/decoder
 *
 * @author angelo.geminiani
 */
public final class AESCipher {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String DEFAULT_PROVIDER = "AES/CBC/PKCS7Padding";
    private static final String AES = "AES";

    private static final Map<Integer, String> PROVIDERS = MapBuilder.createIS()
            .put(128, AES)
            .toMap();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _provider;
    private Cipher _ecipher;
    private Cipher _dcipher;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AESCipher(final String textkey) {
        try {
            final SecretKey key = this.createKeyFromCleartext(textkey);
            this.init(key);
        } catch (Throwable t) {
        }
    }

    public AESCipher(final SecretKey key) {
        this.init(key);
    }

    public AESCipher() {
        try {
            final KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128); // The AES key size in number of bits
            final SecretKey key = generator.generateKey();
            this.init(key);
        } catch (Throwable t) {
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public byte[] encrypt(final byte[] data) {
        try {
            return _ecipher.doFinal(data);
        } catch (Throwable ignored) {
        }
        return null;
    }

    public byte[] decrypt(final byte[] data) {
        try {
            // Decrypt
            byte[] utf8 = _dcipher.doFinal(data);

            // Decode using utf-8
            return utf8;
        } catch (Throwable t) {
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init(final SecretKey key) {
        final int key_bits = key.getEncoded().length * 8;
        _provider = PROVIDERS.get(key_bits);
        if (!StringUtils.hasText(_provider)) {
            _provider = DEFAULT_PROVIDER;
        }
        try {
            _ecipher = Cipher.getInstance(_provider);
            _ecipher.init(Cipher.ENCRYPT_MODE, key);
            _dcipher = Cipher.getInstance(_provider);
            _dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    /**
     * Creates 8 byte secret key.
     *
     * @param cleartext 8 byte string
     * @return
     */
    private SecretKey createKeyFromCleartext(final String cleartext) {
        final byte[] bytes = cleartext.getBytes();
        final SecretKeySpec sk = new SecretKeySpec(bytes, "AES");
        return sk;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static SecretKey createKey(final String cleartext) {
        final byte[] bytes = cleartext.getBytes();
        return createKey(bytes);
    }

    public static SecretKey createKey(final byte[] bytes) {
        final SecretKeySpec sk = new SecretKeySpec(bytes, "AES");
        return sk;
    }

    public static SecretKey createKey(final int key_size_bits) throws NoSuchAlgorithmException {
        final KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(key_size_bits); //
        return generator.generateKey();
    }

}
