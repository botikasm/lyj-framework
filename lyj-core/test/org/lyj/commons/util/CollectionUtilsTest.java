package org.lyj.commons.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @Test
    public void testSublist() throws Exception {
        final Collection<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        Collection<Integer> sub1 = CollectionUtils.subList(list, 0, 2);
        Collection<Integer> sub2 = CollectionUtils.subList(list, 3, 6);

        System.out.println(sub1);
        System.out.println(sub2);

        Collection<Collection> tokens = CollectionUtils.subList(list, 2);
        System.out.println(tokens);
        assertTrue(tokens.size()==4);
    }
}