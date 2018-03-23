package org.lyj.commons.cryptograph;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 04/08/17.
 */
public class MD5Test {

    @Test
    public void encode() throws Exception {

        final String[] sample_1 = new String[]{"A", "B", "C", "D"};
        final String[] sample_2 = new String[]{"D", "C", "B", "A"};

        final String md_1 = MD5.encode(sample_1, true);
        final String md_2 = MD5.encode(sample_2, true);

        assertEquals(md_1, md_2);

        final String md_3 = MD5.encode(sample_1, false);
        final String md_4 = MD5.encode(sample_2, false);

        assertNotEquals(md_3, md_4);

        assertTrue(md_1.length()==md_2.length());

        System.out.println(md_1.length());
    }

}