package org.lyj.commons.util;

import org.junit.Test;

import java.util.Date;

/**
 * User: angelo.geminiani
 */
public class DateUtilsTest {

    public DateUtilsTest() {

    }

    @Test
    public void testPostponeWorkingDay() throws Exception {
        Date now = DateUtils.now();
        Date date = DateUtils.postponeWorkingDay(now, DateUtils.DAY, 30, new Long[0]);
        System.out.println(date.toString());
    }

    @Test
    public void testDayOfWeek() throws Exception {
        Date now = DateUtils.now();
        final int response = DateUtils.getDayOfWeek(); // sunday=1, monday=2
        System.out.println(response);
    }

    @Test
    public void testDayOfWeekInMonth() throws Exception {
        Date now = DateUtils.now();
        final int response = DateUtils.getDayOfWeekInMonth(); // sunday=1, monday=2
        System.out.println(response);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
