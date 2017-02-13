package org.lyj.commons.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SortUtils {


    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByKeyAsc(final Map<K, V> map) {
        return sortByKey(map, true);
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByKeyDes(final Map<K, V> map) {
        return sortByKey(map, false);
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByKey(final Map<K, V> map,
                                                                                       final boolean asc) {

        final List<Map.Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());

        sortedEntries.sort(new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                return asc
                        ? CompareUtils.compare(e2.getKey(), e1.getKey())
                        : CompareUtils.compare(e1.getKey(), e2.getKey());
            }
        });

        return sortedEntries;
    }

    public static <V extends Comparable<? super V>> List<Map.Entry<String, V>> sortByKeyLenght(final Map<String, V> map,
                                                                                                final boolean asc) {

        final List<Map.Entry<String, V>> sortedEntries = new ArrayList<>(map.entrySet());

        sortedEntries.sort(new Comparator<Map.Entry<String, V>>() {
            @Override
            public int compare(Map.Entry<String, V> e1, Map.Entry<String, V> e2) {
                final String key1 = e1.getKey();
                final String key2 = e2.getKey();
                return asc
                        ? CompareUtils.compare(key2.length(), key1.length())
                        : CompareUtils.compare(key1.length(), key2.length());
            }
        });

        return sortedEntries;
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValueAsc(final Map<K, V> map) {
        return sortByValue(map, true);
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValueDes(final Map<K, V> map) {
        return sortByValue(map, false);
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValue(final Map<K, V> map,
                                                                                         final boolean asc) {

        final List<Map.Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());

        sortedEntries.sort(new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                return asc
                        ? CompareUtils.compare(e2.getValue(), e1.getValue())    //  e2.getValue().compareTo(e1.getValue())
                        : CompareUtils.compare(e1.getValue(), e2.getValue());   //  e1.getValue().compareTo(e2.getValue())
            }
        });

        return sortedEntries;
    }

}
