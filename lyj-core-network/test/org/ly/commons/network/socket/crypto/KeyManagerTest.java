package org.ly.commons.network.socket.crypto;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.cryptograph.pem.RSAHelper;
import org.lyj.commons.util.StringUtils;

import java.io.File;

import static org.junit.Assert.*;

public class KeyManagerTest {

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void keyManagerTest() throws Exception {

        KeyManager km = new KeyManager("./keyStore/sample");
        this.test(km);

        km = new KeyManager(new File(km.privateKeyName()), new File(km.publicKeyName()));
        this.test(km);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void test(KeyManager km) throws Exception {
        assertTrue(km.enabled());
        assertNull(km.error());
        assertNotNull(km.privateKey());
        assertTrue(StringUtils.hasText(km.privateKeyString()));

        System.out.println(km.privateKeyName());
        System.out.println(km.privateKeyString());

        System.out.println("--- testing encryption ---");
        String message = "hello, this is a message";

        // encrypt with public key
        byte[] encrypted = RSAHelper.encrypt(message.getBytes(), km.publicKey());
        // decrypt with private key
        byte[] decrypted = RSAHelper.decrypt(encrypted, km.privateKey());
        assertEquals(new String(decrypted), message);
        System.out.println("--- encryption is OK ---");

        // public key length
        System.out.println("PUBLICK KEY LENGTH: " + km.publicKeyString().length());
    }

}