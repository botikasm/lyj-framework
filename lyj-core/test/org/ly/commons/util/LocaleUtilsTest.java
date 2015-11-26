package org.ly.commons.util;

import org.junit.Test;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * User: angelo.geminiani
 */
public class LocaleUtilsTest {

    public LocaleUtilsTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testGetCurrent() throws Exception {
        DecimalFormatSymbols dfs = LocaleUtils.getDecimalFormatSymbols(new Locale("it", "US"));
        assertEquals(dfs.getDecimalSeparator(), ',');

        dfs = LocaleUtils.getDecimalFormatSymbols(new Locale("en", "US"));
        assertEquals(dfs.getDecimalSeparator(), '.');
    }
}
