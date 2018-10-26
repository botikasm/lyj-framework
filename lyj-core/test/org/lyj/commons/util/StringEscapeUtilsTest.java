package org.lyj.commons.util;

import org.junit.Test;

/**
 * Created by angelogeminiani on 13/09/16.
 */
public class StringEscapeUtilsTest {

    @Test
    public void unescapeJavaScript() throws Exception {

        String escape = StringEscapeUtils.escapeJava("this \n is a test");
        System.out.println(escape);

        String unescape = StringEscapeUtils.unescapeJava(escape);
        System.out.println(unescape);

    }

}