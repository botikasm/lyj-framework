package org.lyj.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegExpUtilsTest {

    @Test
    public void testReplaceNoAlphanumericChar() throws Exception {
        String result = RegExpUtils.replaceNoAlphanumericChar("test file");
        System.out.println(result);
        assertEquals(result, "testfile");
    }

}
