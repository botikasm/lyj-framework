package org.lyj.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelogeminiani on 17/03/17.
 */
public class ConversionUtilsTest {

    @Test
    public void inchToMm() throws Exception {

        double mm1 = ConversionUtils.inchToCm(1);
        assertEquals(2.54, mm1, 0);

        for (double i = 1.0; i < 1000000; i++) {
            double mm = ConversionUtils.inchToMm(i);
            double inc = ConversionUtils.mmToInch(mm);
            assertEquals(i, inc, 0.001);
        }

    }

    @Test
    public void toNumbers() throws Exception {

        Number[] numbers = ConversionUtils.toNumbers(1, 2, 3, "12.5", new Object[]{4, 5, 6, 7.8});
        assertTrue(numbers.length == 8);
        assertEquals(numbers[3], 12.5);
    }
}