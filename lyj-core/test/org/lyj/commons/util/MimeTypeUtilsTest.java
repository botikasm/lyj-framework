package org.lyj.commons.util;

import org.junit.Test;

/**
 * Created by angelogeminiani on 02/02/17.
 */
public class MimeTypeUtilsTest {

    @Test
    public void getMimeType() throws Exception {

        final String type = MimeTypeUtils.getMimeType("/path/image.JPG");
        System.out.println(type);

    }

}