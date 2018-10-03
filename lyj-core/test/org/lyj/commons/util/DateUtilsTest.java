package org.lyj.commons.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * User: angelo.geminiani
 */
public class DateUtilsTest {

    public DateUtilsTest() {

    }

    @Test
    public void postponeEndOfMonth() throws Exception {
        Date now = DateUtils.now();
        Date date = DateUtils.postponeEndOfMonth(now);
        System.out.println(date.toString());
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

    @Test
    public void testIsTimeExpired() throws Exception {

        final DateWrapper now = new DateWrapper(DateUtils.now());
        final DateWrapper yesterday = new DateWrapper(DateUtils.yesterday());

        assertTrue(DateUtils.isExpiredDate(yesterday, now));
        assertFalse(DateUtils.isExpiredDate(now, yesterday));
        assertFalse(DateUtils.isExpiredDate(now, now));
        Thread.sleep(1000);
        assertFalse(DateUtils.isExpiredDate(now, DateUtils.now()));
    }

    @Test
    public void isWithinThreashold() throws Exception {

        // 10.30 am
        DateWrapper now = DateWrapper.parse(DateUtils.now());
        now.setHour(10);
        now.setMinute(30);
        Date d = now.getDateTime();

        // valid range is 10:30-11:30
        assertFalse( DateUtils.isWithinThreashold("7:10", d, 60) );
        assertFalse( DateUtils.isWithinThreashold("11:40", d, 60) );
        assertTrue( DateUtils.isWithinThreashold("10:30", d, 60) );
        assertTrue( DateUtils.isWithinThreashold("11:30", d, 60) );
        assertTrue( DateUtils.isWithinThreashold("11:00", d, 60) );

        now.setHour(22);
        now.setMinute(30);
        d = now.getDateTime();

        // valid range is 10:30-11:30
        assertFalse( DateUtils.isWithinThreashold("7:10", d, 60) );
        assertFalse( DateUtils.isWithinThreashold("11:40", d, 60) );
        assertFalse( DateUtils.isWithinThreashold("10:30", d, 60) );
        assertFalse( DateUtils.isWithinThreashold("11:30", d, 60) );
        assertFalse( DateUtils.isWithinThreashold("11:00", d, 60) );
        assertTrue( DateUtils.isWithinThreashold("23:00", d, 60) );
        assertFalse( DateUtils.isWithinThreashold("24:00", d, 60) );
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
