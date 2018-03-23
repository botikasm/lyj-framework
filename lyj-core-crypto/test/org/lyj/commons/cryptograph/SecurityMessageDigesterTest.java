package org.lyj.commons.cryptograph;

import org.junit.Test;
import org.lyj.commons.util.RandomUtils;

public class SecurityMessageDigesterTest {

    @Test
    public void encodeSHA_256() {

        String text = RandomUtils.randomAlphanumeric((int) RandomUtils.rnd(100, 2000));
        String secret = RandomUtils.randomAlphanumeric((int) RandomUtils.rnd(10, 20));

        String msg_256 = SecurityMessageDigester.encodeSHA_256(text, secret);
        String msg_512 = SecurityMessageDigester.encodeSHA_512(text, secret);

        System.out.println("len hash 256" + msg_256.length() + " secret: '" + secret + "' text: '" + text + "'");
        System.out.println("len hash 512" + msg_512.length() + " secret: '" + secret + "' text: '" + text+  "'");

    }
}