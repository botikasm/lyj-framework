package org.lyj.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelogeminiani on 11/03/16.
 */
public class CollectionUtilsTest {

    @Test
    public void testMerge() throws Exception {
        byte[] arr1 = new byte[]{1,2,3};
        byte[] arr2 = new byte[]{4,5,6,7};
        byte[] arr3 = CollectionUtils.merge(arr1, arr2);
        assertTrue(arr3.length==arr1.length+arr2.length);
        assertEquals(arr1[0], arr3[0]);
        assertEquals(arr2[3], arr3[6]);
    }
}