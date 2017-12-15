package org.lyj.commons.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class SortUtilsTest {

    @Test
    public void sortChars() {

        final String sorted = SortUtils.sortChars("ABCD", "efg", "xyz", "KYW");

        System.out.println(sorted);

    }
}