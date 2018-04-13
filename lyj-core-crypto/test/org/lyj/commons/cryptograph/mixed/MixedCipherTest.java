package org.lyj.commons.cryptograph.mixed;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.cryptograph.pem.RSAHelper;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;

public class MixedCipherTest {

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void mixedCipherTest() throws Exception {

        final KeyPair key = RSAHelper.generateRSAKeyPair();
        File file = new File(PathUtils.getAbsolutePath("./sample_file.txt"));

        // test key conversion
        final String string_public = RSAHelper.toString(key.getPublic());
        final PublicKey key_public = RSAHelper.readRSAPublicKeyFromText(string_public);
        final String string_private = RSAHelper.toString(key.getPrivate());
        final PrivateKey key_private = RSAHelper.readRSAPrivateKeyFromText(string_private);

        //-- encrypt --//
        MixedCipher.Pack pack = MixedCipher.encrypt(file, key_public);

        //-- decrypt --//
        byte[] data = MixedCipher.decrypt(pack, key_private);
        byte[] data2 = MixedCipher.decrypt(pack, string_private);

        assertEquals(data.length, data2.length);

        System.out.println("ENCODED SECRET LENGTH: " + pack.encodedSecret().length + " " + MD5.encode(pack.encodedSecret()));
        System.out.println("ENCODED DATA LENGTH: " + pack.encodedData().length);
        System.out.println("DATA LENGTH: " + data.length);

        System.out.println(StringUtils.leftStr(new String(data), 50, true));
    }

}