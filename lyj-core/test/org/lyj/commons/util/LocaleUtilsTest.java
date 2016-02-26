package org.lyj.commons.util;

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

    @Test
    public void testGetLanguage() throws Exception {
        String lang = LocaleUtils.getLocale("it").getLanguage();
        assertEquals("it", lang);

        lang = LocaleUtils.getLocale("Italian").getLanguage();
        assertEquals("it", lang);

        lang = LocaleUtils.getLocale("en").getLanguage();
        assertEquals("en", lang);

        lang = LocaleUtils.getLocale("English").getLanguage();
        assertEquals("en", lang);

    }

}
