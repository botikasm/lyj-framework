package org.lyj.commons.util;

import org.junit.Test;

public class SortUtilsTest {

    @Test
    public void sortChars() {

        final String sorted = SortUtils.sortChars("ABCD", "efg", "xyz", "KYW");

        System.out.println(sorted);

    }
}