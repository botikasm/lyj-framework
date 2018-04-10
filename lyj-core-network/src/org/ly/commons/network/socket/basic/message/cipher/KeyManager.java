package org.ly.commons.network.socket.basic.message.cipher;

import org.lyj.commons.cryptograph.pem.RSAHelper;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.security.*;

/**
 * Manage keys (private and public)
 */
public class KeyManager {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "./keyStore/keys";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _private_key_name;
    private final String _public_key_name;

    private boolean _enabled;
    private Exception _error;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public KeyManager() {
        this(ROOT);
    }

    public KeyManager(final String root,
                      final String name) {
        this(PathUtils.concat(root, name));
    }

    public KeyManager(final String root) {
        _enabled = false;
        _error = null;

        final String absolute_root = PathUtils.getAbsolutePath(root);

        _private_key_name = PathUtils.concat(absolute_root, "private.pem");
        _public_key_name = PathUtils.concat(absolute_root, "public.pem");

        FileUtils.tryMkdirs(absolute_root); // ensure root exists

        this.init();
    }

    public KeyManager(final File privateKeyFile,
                      final File publicKeyFile) {
        _enabled = false;
        _error = null;

        _private_key_name = privateKeyFile.getAbsolutePath();
        _public_key_name = publicKeyFile.getAbsolutePath();

        FileUtils.tryMkdirs(_private_key_name); // ensure root exists
        FileUtils.tryMkdirs(_public_key_name);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String privateKeyName() {
        return _private_key_name;
    }

    public String publicKeyName() {
        return _public_key_name;
    }

    public boolean enabled() {
        return _enabled;
    }

    public Exception error() {
        return _error;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public PrivateKey privateKey() {
        try {
            if (_enabled) {
                return RSAHelper.readRSAPrivateKeyFromFile(_private_key_name);
            }
        } catch (Exception e) {
            _error = e;
            _enabled = false;
        }
        return null;
    }

    public PublicKey publicKey() {
        try {
            if (_enabled) {
                return RSAHelper.readRSAPublicKeyFromFile(_public_key_name);
            }
        } catch (Exception e) {
            _error = e;
            _enabled = false;
        }
        return null;
    }


    public String privateKeyString() {
        try {
            return RSAHelper.readKeyFile(_private_key_name, true);
        } catch (Exception e) {
            _error = e;
            _enabled = false;
        }
        return "";
    }

    public String publicKeyString() {
        try {
            return RSAHelper.readKeyFile(_public_key_name, true);
        } catch (Exception e) {
            _error = e;
            _enabled = false;
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        try {
            if (!FileUtils.exists(_private_key_name) || !FileUtils.exists(_public_key_name)) {
                this.generate();
            }

            _enabled = true;
        } catch (Exception e) {
            _enabled = false;
            _error = e;
        }
    }


    private void generate() throws NoSuchProviderException, NoSuchAlgorithmException, IOException {
        final KeyPair key_pair = RSAHelper.generateRSAKeyPair();
        RSAHelper.writePemFiles(key_pair, _private_key_name, _public_key_name);
    }


}
