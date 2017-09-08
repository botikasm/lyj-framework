package org.lyj.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PhoneUtilsTest {

    @Test
    public void localize() throws Exception {

        assertEquals(PhoneUtils.localize("00395416412345", "IT"), "+395416412345");
        assertEquals(PhoneUtils.localize("+395416412345", "IT"), "+395416412345");
        assertEquals(PhoneUtils.localize("0541 6412 345", "IT"), "+395416412345");
        assertEquals(PhoneUtils.localize("0541-6412-345", "IT"), "+395416412345");
        assertEquals(PhoneUtils.localize("0541-6412-345", "IT_IT"), "+395416412345");

    }

    @Test
    public void localize_SM() throws Exception {
        
        assertEquals(PhoneUtils.localize("33312121212", "SM"), "+37833312121212");

    }

}