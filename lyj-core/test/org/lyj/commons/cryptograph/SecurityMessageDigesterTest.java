package org.lyj.commons.cryptograph;

import org.junit.Test;

import static org.junit.Assert.*;

public class SecurityMessageDigesterTest {

    @Test
    public void encodeSHA_256() {

        String msg_256 = SecurityMessageDigester.encodeSHA_256("HELLO", "123");
        String msg_512 = SecurityMessageDigester.encodeSHA_512("HELLO", "123");

        System.out.println(msg_256.length());
        System.out.println(msg_512.length());

    }
}