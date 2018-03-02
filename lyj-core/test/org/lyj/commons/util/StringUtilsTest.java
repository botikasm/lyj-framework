package org.lyj.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 *
 */
public class StringUtilsTest {

    @Test
    public void chunk() throws Exception {
        String[] array = StringUtils.chunk("questa aaaaa aaaa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga ", 128);
        System.out.println(array.length);
        for(final String line:array){
            System.out.println(line);
        }
    }

    @Test
    public void splitLines() throws Exception {
        String[] array = StringUtils.splitLines("questa aaaaa aaaa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga questa è una frase lunga ", 128);
        System.out.println(array.length);
        for(final String line:array){
            System.out.println(line);
        }
    }

    @Test
    public void concatArgsExt() throws Exception {
        String[] array = new String[]{"val1", "val2"};
        final String out = StringUtils.concatArgsEx(",", array);
        assertTrue(out.equalsIgnoreCase("val1,val2"));
        System.out.println(out);
    }

    @Test
    public void testToString() throws Exception {
        String str = StringUtils.toString(new String[]{"val1", "val2"});
        assertTrue(str.equalsIgnoreCase("val1,val2"));

        System.out.println(StringUtils.toString(123.0000000003300000));
        System.out.println(StringUtils.toString(0.000000000000000000000000000000001));
    }

    @Test
    public void testFill() throws Exception {
        String str = StringUtils.fillString("123456", "0", 8);
        assertTrue(str.equalsIgnoreCase("00123456"));
    }

    @Test
    public void testSplit() throws Exception {

        System.out.println("Test Split");

        String[] tokens = StringUtils.split("prefix_suffix", "DELIM", true);
        assertTrue(tokens.length == 1);

        tokens = StringUtils.split("prefix_suffix", "_DELIM_", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix_suffix", "_", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix!suffix", "!DELIM!", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix!suffix", "!", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix.suffix", ".DELIM.", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix.suffix", ".", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix.DELIM.suffix", ".DELIM.", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split(" prefix suffix", " ", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("1:2;3;", ":;", true);
        assertTrue(tokens.length == 3);
        assertEquals(tokens[0], "1");
        assertEquals(tokens[1], "2");
        assertEquals(tokens[2], "3");
    }
}
