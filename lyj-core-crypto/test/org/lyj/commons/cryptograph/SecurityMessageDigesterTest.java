package org.lyj.commons.cryptograph;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;

import java.io.File;
import java.io.IOException;

public class SecurityMessageDigesterTest {

    @BeforeClass
    public static void setUp() throws Exception {

        TestInitializer.init();

    }

    @Test
    public void encodeSHA_256() {

        String text = RandomUtils.randomAlphanumeric((int) RandomUtils.rnd(100, 2000));
        String secret = RandomUtils.randomAlphanumeric((int) RandomUtils.rnd(10, 90));

        String msg_256 = SecurityMessageDigester.encodeSHA_256(text, secret);
        String msg_512 = SecurityMessageDigester.encodeSHA_512(text, secret);

        System.out.println("len hash 256: " + msg_256.length() + " secret: '" + secret + "' text: '" + text + "'");
        System.out.println("len hash 512: " + msg_512.length() + " secret: '" + secret + "' text: '" + text+  "'");

    }

    @Test
    public void encodeSHA_256_large() throws Exception {

        String text = FileUtils.readFileToString(new File(PathUtils.getAbsolutePath("./sample_file.txt")));

        String sha = SecurityMessageDigester.encodeSHA_256(text.getBytes(), new byte[1]);

        System.out.println("len hash 256: " + sha.length() + " " + sha);

    }

}