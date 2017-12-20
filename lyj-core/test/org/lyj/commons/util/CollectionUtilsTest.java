package org.lyj.commons.util;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelogeminiani on 11/03/16.
 */
public class CollectionUtilsTest {

    @Test
    public void testMerge() throws Exception {
        byte[] arr1 = new byte[]{1, 2, 3};
        byte[] arr2 = new byte[]{4, 5, 6, 7};
        byte[] arr3 = CollectionUtils.merge(arr1, arr2);
        assertTrue(arr3.length == arr1.length + arr2.length);
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
        assertTrue(tokens.size() == 4);

        final String[] sarray = new String[]{"a", "b", "c", "d"};
        final Collection<String> sub_sarray = CollectionUtils.subList(sarray, 2, 3);
        System.out.println(StringUtils.toString(sub_sarray.toArray()));
        System.out.println(StringUtils.toString(sub_sarray));

        final String[] sub_sarray2 = CollectionUtils.subArray(sarray, 2, 3);
        System.out.println(StringUtils.toString(sub_sarray2));
    }

    @Test
    public void matchAll() throws Exception {
        final Collection<Integer> list1 = new ArrayList<>();
        list1.add(0);
        list1.add(1);
        list1.add(2);
        list1.add(3);
        list1.add(4);
        list1.add(5);
        list1.add(6);
        final Collection<Integer> list2 = new ArrayList<>();
        list2.add(0);
        list2.add(1);
        list2.add(2);
        final Collection<Integer> list3 = new ArrayList<>();
        list3.add(0);
        list3.add(1);
        list3.add(2);
        list3.add(3);
        list3.add(4);
        list3.add(5);
        list3.add(6);
        final Collection<Integer> list4 = new ArrayList<>();
        list4.add(0);
        list4.add(1);


        final Collection<Integer> result = CollectionUtils.matchAllUnique(list1, list2, list3, list4);
        assertTrue(result.size() == 2);
        System.out.println(result);

        final Collection<Map<String, Integer>> c_map_1 = new ArrayList<>();
        c_map_1.add(new MapBuilder<String, Integer>().put("a", 1).toMap());
        c_map_1.add(new MapBuilder<String, Integer>().put("b", 11).toMap());
        final Collection<Map<String, Integer>> c_map_2 = new ArrayList<>();
        c_map_2.add(new MapBuilder<String, Integer>().put("a", 2).toMap());

        final Collection<Map<String, Integer>> c_map_common_unique = CollectionUtils.matchAllUnique((item1, item2)->{
           final Set<String> keys = item1.keySet();
           for(final String key:keys){
               if(item2.containsKey(key)){
                    return true;
               }
           }
           return false;
        }, c_map_1, c_map_2);
        assertTrue(c_map_common_unique.size() == 1);
        System.out.println(c_map_common_unique);

        final Collection<Map<String, Integer>> c_map_common_all = CollectionUtils.matchAll((item1, item2)->{
            final Set<String> keys = item1.keySet();
            for(final String key:keys){
                if(item2.containsKey(key)){
                    return true;
                }
            }
            return false;
        }, c_map_1, c_map_2);
        assertTrue(c_map_common_all.size() == 2);
        System.out.println(c_map_common_all);

    }
}