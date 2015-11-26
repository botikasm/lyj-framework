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
package org.ly.commons.cryptograph;

import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Simple AES encoder/decoder
 *
 * @author angelo.geminiani
 */
public final class AESEncrypter {


    private Cipher _ecipher;
    private Cipher _dcipher;

    public AESEncrypter(final String textkey) {
        try {
            final SecretKey key = this.createKeyFromCleartext(textkey);
            this.init(key);
        } catch (Throwable t) {
        }
    }

    public AESEncrypter(final SecretKey key) {
        this.init(key);
    }

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
        try {
            _ecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            _ecipher.init(Cipher.ENCRYPT_MODE, key);
            _dcipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
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
}
