package org.lyj.commons.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegExpUtilsTest {

    @Test
    public void testReplaceNoAlphanumericChar() throws Exception {
        String result = RegExpUtils.replaceNoAlphanumericChar("test file");
        System.out.println(result);
        assertEquals(result, "testfile");
    }

    @Test
    public void testReplaceNotChars() throws Exception {
        String result = RegExpUtils.replaceNotChars("123test file, çòè èè");
        System.out.println(result);
        assertEquals(result, "123testfileçòèèè");
    }

    @Test
    public void testPhone() throws Exception {

        assertTrue(RegExpUtils.isValidPhoneNumber("+39 0541 642255"));
        assertTrue(RegExpUtils.isValidPhoneNumber("541 642255"));
        assertFalse(RegExpUtils.isValidPhoneNumber("541-642255"));
        assertFalse(RegExpUtils.isValidPhoneNumber("asdfghgjhjjk"));

    }

    @Test
    public void tokenize() throws Exception {

        String[] tokens = RegExpUtils.tokenize("this is {token1 double} text with {token2} tokens {token3}!}", "\\{(?:[^\\}]+)?\\}");
        assertEquals(tokens.length, 7);

        tokens = RegExpUtils.tokenize("this is 'token1 double' text with 'token2' tokens 'token3'!'", "\\'(?:[^\\']+)?\\'");
        assertEquals(tokens.length, 7);
    }

    @Test
    public void testUppercase() throws Exception {

        assertTrue(RegExpUtils.isFirstUppercase("De"));
        assertTrue(RegExpUtils.isFirstUppercase("Della"));
        assertFalse(RegExpUtils.isFirstUppercase("DELLA"));
        assertFalse(RegExpUtils.isFirstUppercase("541-642255"));
        assertFalse(RegExpUtils.isFirstUppercase("asdfghgjhjjk"));

    }

}
