package org.lyj.commons.cryptograph.mixed;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.cryptograph.pem.RSAHelper;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.security.KeyPair;

public class MixedCipherTest {

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void mixedCipherTest() throws Exception {

        final KeyPair key = RSAHelper.generateRSAKeyPair();
        File file = new File(PathUtils.getAbsolutePath("./sample_file.txt"));
        
        MixedCipher.Pack pack = MixedCipher.encrypt(file, key.getPublic());
        byte[] data = MixedCipher.decrypt(pack, key.getPrivate());

        System.out.println(new String(data));
    }

}