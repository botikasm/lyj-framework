/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * DateUtils.java
 *
 */
package org.lyj.commons.util;

import java.text.DateFormatSymbols;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author
 */
public abstract class DateUtils {

    public static final long ONE_MINUTE_MS = 60 * 1000;
    public static final long ONE_HOUR_MS = 60 * ONE_MINUTE_MS;
    public static final long ONE_DAY_MS = 24 * ONE_HOUR_MS;

    // measure unit
    /**
     * Milliseconds=14 *
     */
    public static final int MILLISECOND = Calendar.MILLISECOND;     // 14
    /**
     * Seconds=13 *
     */
    public static final int SECOND = Calendar.SECOND;               // 13
    /**
     * Minute=12 *
     */
    public static final int MINUTE = Calendar.MINUTE;               // 12
    /**
     * Hour=11 *
     */
    public static final int HOUR = Calendar.HOUR_OF_DAY;            // 11
    /**
     * Day=5 *
     */
    public static final int DAY = Calendar.DAY_OF_MONTH;            // 5
    /**
     * MONTH=2 *
     */
    public static final int MONTH = Calendar.MONTH;                 // 2
    /**
     * Year=1 *
     */
    public static final int YEAR = Calendar.YEAR;                   // 1
    /**
     * Infinite year (3000) *
     */
    public static final int INFINITE_YEAR = 3000;
    /**
     * Zero year (1900) *
     */
    public static final int ZERO_YEAR = 1900;

    public static final String sMILLISECOND = "millisecond";
    public static final String sSECOND = "second";
    public static final String sMINUTE = "minute";
    public static final String sHOUR = "hour";
    public static final String sDAY = "day";
    public static final String sMONTH = "month";
    public static final String sYEAR = "year";


    /**
     * no-working days
     */
    private static final int[] highDays = {Calendar.SUNDAY, Calendar.SATURDAY};

    public static int UnitFromString(final String unit) {
        if (sMILLISECOND.equals(unit)) {
            return MILLISECOND;
        } else if (sSECOND.equals(unit)) {
            return SECOND;
        } else if (sMINUTE.equals(unit)) {
            return MINUTE;
        } else if (sHOUR.equals(unit)) {
            return HOUR;
        } else if (sDAY.equals(unit)) {
            return DAY;
        } else {
            return MILLISECOND;
        }
    }

    public static String UnitFromInt(final int unit) {
        if (MILLISECOND == unit) {
            return sMILLISECOND;
        } else if (SECOND == unit) {
            return sSECOND;
        } else if (MINUTE == unit) {
            return sMINUTE;
        } else if (HOUR == unit) {
            return sHOUR;
        } else if (DAY == unit) {
            return sDAY;
        } else {
            return sMILLISECOND;
        }
    }

    public static TimeUnit TimeUnitFromString(final String unit) {
        if (sMILLISECOND.equals(unit)) {
            return TimeUnit.MILLISECONDS;
        } else if (sSECOND.equals(unit)) {
            return TimeUnit.SECONDS;
        } else if (sMINUTE.equals(unit)) {
            return TimeUnit.MINUTES;
        } else if (sHOUR.equals(unit)) {
            return TimeUnit.HOURS;
        } else if (sDAY.equals(unit)) {
            return TimeUnit.DAYS;
        } else {
            return TimeUnit.MILLISECONDS;
        }
    }

    public static TimeUnit TimeUnitFromInt(final int unit) {
        if (MILLISECOND == unit) {
            return TimeUnit.MILLISECONDS;
        } else if (SECOND == unit) {
            return TimeUnit.SECONDS;
        } else if (MINUTE == unit) {
            return TimeUnit.MINUTES;
        } else if (HOUR == unit) {
            return TimeUnit.HOURS;
        } else if (DAY == unit) {
            return TimeUnit.DAYS;
        } else {
            return TimeUnit.MILLISECONDS;
        }
    }

    /**
     * Return number of milliseconds in measure unit and multiplied for passed value.
     * Useful to get, for example, 35 minutes in milliseconds: milliseconds (35, DateUtils.MINUTES);
     *
     * @param value       number of measure unit
     * @param measureUnit MILLISECOND, SECOND, MINUTE, HOUR, DAY
     * @return
     */
    public static long milliseconds(final int value, int measureUnit) {
        long result = value;
        switch (measureUnit) {
            case MILLISECOND:
                result = value;
                break;
            case SECOND:
                result = value * 1000;
                break;
            case MINUTE:
                result = value * 1000 * 60;
                break;
            case HOUR:
                result = value * 1000 * 60 * 60;
                break;
            case DAY:
                result = value * 1000 * 60 * 60 * 24;
                break;
        }
        return result;
    }

    public static long to(final long value, int measureUnit) {
        long result = value;
        switch (measureUnit) {
            case MILLISECOND:
                result = value;
                break;
            case SECOND:
                result = value / 1000;
                break;
            case MINUTE:
                result = value / (1000 * 60);
                break;
            case HOUR:
                result = value / (1000 * 60 * 60);
                break;
            case DAY:
                result = value / (1000 * 60 * 60 * 24);
                break;
        }
        return result;
    }


    /**
     * Return a date.
     *
     * @param year    Year
     * @param month   Month. 1 is January
     * @param day     Day
     * @param hour    Hour
     * @param minutes Minutes
     * @param seconds Seconds
     * @return A valid date
     */
    public static Date encodeDateTime(int year, int month, int day, int hour, int minutes, int seconds) {
        Date result;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minutes, seconds);
        result = calendar.getTime();
        return result;
    }

    /**
     * Calculate difference in milliseconds from 2 dates.
     *
     * @param date1 First date
     * @param date2 Second date
     * @return Difference between first date and second date in milliseconds
     */
    public static long dateDiff(final Date date1, final Date date2) {
        long result = date1.getTime() - date2.getTime();
        return result;
    }

    /**
     * Calculate difference from 2 dates.<br>
     * Result is in "measureUnit" measure unit. (Ex: DAY)
     *
     * @param date1       First date
     * @param date2       Second date
     * @param measureUnit Measure unit. Ex: DateUtility.DAY
     * @return Difference between first date and second date.
     */
    public static double dateDiff(final Date date1, final Date date2, int measureUnit) {
        long diff = dateDiff(date1, date2);
        double result = 0d;
        switch (measureUnit) {
            case MILLISECOND:
                result = (double) diff;
                break;
            case SECOND:
                result = (double) (diff / 1000d);
                break;
            case MINUTE:
                result = (double) (diff / (1000d * 60d));
                break;
            case HOUR:
                result = (double) (diff / (1000d * 60d * 60d));
                break;
            case DAY:
                result = (double) (diff / (1000d * 60d * 60d * 24d));
                break;
            case YEAR:
                result = (double) (diff / (1000d * 60d * 60d * 24d * 365d));
                break;
            case MONTH:
                result = (double) (diff / (1000d * 60d * 60d * 24d * 365d) / 12d);
                break;
        }
        return result;
    }

    public static Date postpone(final Date date, final int measureUnit, final int amount) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return postpone(calendar, measureUnit, amount);
    }

    public static Date postpone(final Calendar calendar, final int measureUnit, final int amount) {

        switch (measureUnit) {
            case MILLISECOND:
                calendar.add(MILLISECOND, amount);
                break;
            case SECOND:
                calendar.add(SECOND, amount);
                break;
            case MINUTE:
                calendar.add(MINUTE, amount);
                break;
            case HOUR:
                calendar.add(HOUR, amount);
                break;
            case DAY:
                calendar.add(DAY, amount);
                break;
            case MONTH:
                calendar.add(MONTH, amount);
                break;
            case YEAR:
                calendar.add(YEAR, amount);
                break;
        }

        return calendar.getTime();
    }

    public static Date postponeWorkingDay(final Date date,
                                          final int measureUnit,
                                          final int amount,
                                          final Long[] holidays) {
        Date result = date;
        for (int i = 0; i < amount; i++) {
            result = postpone(result, measureUnit, 1);
            if (!isWorkingDay(result, holidays)) {
                result = nextWorkingDay(result, holidays).getTime();
            }
        }
        return result;
    }

    public static Date postponeWorkingDay(final Calendar calendar,
                                          final int measureUnit,
                                          final int amount,
                                          final Long[] holidays) {
        Date result = calendar.getTime();
        for (int i = 0; i < amount; i++) {
            postpone(calendar, measureUnit, 1);
            if (!isWorkingDay(calendar, holidays)) {
                nextWorkingDay(calendar, holidays);
            }
        }
        return calendar.getTime();
    }

    public static Date anticipateWorkingDay(final Date date,
                                            final int measureUnit,
                                            final int amount,
                                            final Long[] holidays) {
        Date result = date;
        for (int i = 0; i < amount; i++) {
            result = postpone(result, measureUnit, -1);
            if (!isWorkingDay(result, holidays)) {
                result = previousWorkingDay(result, holidays).getTime();
            }
        }
        return result;
    }

    /**
     * Return true if the day is a working day
     *
     * @param date     Date
     * @param holidays Array of holidays
     * @return Return true if the day is a working day
     */
    public static boolean isWorkingDay(final Date date, final Long[] holidays) {
        return isWorkingDay(date.getTime(), holidays);
    }

    /**
     * Return true if the day is a working day
     *
     * @param time     Long - time in milliseconds
     * @param holidays Array of holidays
     * @return Return true if the day is a working day
     */
    public static boolean isWorkingDay(final long time, final Long[] holidays) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return isWorkingDay(calendar, holidays);
    }

    public static boolean isWorkingDay(final Calendar calendar, final Long[] holidays) {

        // check if in holiday
        if (null != holidays) {
            final Calendar holiday = Calendar.getInstance();
            for (final long d : holidays) {
                holiday.setTimeInMillis(d);
                if (holiday.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                        && holiday.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                        && holiday.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    return false;
                }
            }
        }

        // check day of week
        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        for (final int i : highDays) {
            if (i == dayOfWeek) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param date
     * @return
     */
    public static Calendar nextWorkingDay(final Date date, final Long[] holidays) {
        return nextWorkingDay(date.getTime(), holidays);
    }

    /**
     * @param time
     * @return
     */
    public static Calendar nextWorkingDay(final Long time,
                                          final Long[] holidays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        // move forward calendar of one day
        calendar.add(DAY, 1);
        while (!isWorkingDay(calendar.getTimeInMillis(), holidays)) {
            calendar.add(DAY, 1);
        }
        return calendar;
    }


    /**
     * @param calendar
     * @param holidays (Optional) Array of holidays
     * @return
     */
    public static Calendar nextWorkingDay(final Calendar calendar,
                                          final Long[] holidays) {
        // move forward calendar of one day
        calendar.add(DAY, 1);
        while (!isWorkingDay(calendar.getTimeInMillis(), holidays)) {
            calendar.add(DAY, 1);
        }
        return calendar;
    }

    public static Calendar previousWorkingDay(final Date date, final Long[] holidays) {
        return previousWorkingDay(date.getTime(), holidays);
    }

    public static Calendar previousWorkingDay(final Long time,
                                              final Long[] holidays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        // move forward calendar of one day
        calendar.add(DAY, -1);
        while (!isWorkingDay(calendar.getTimeInMillis(), holidays)) {
            calendar.add(DAY, -1);
        }
        return calendar;
    }

    /**
     * @param calendar
     * @param holidays (Optional) Array of holidays
     * @return
     */
    public static Calendar previousWorkingDay(final Calendar calendar,
                                              final Long[] holidays) {
        // move backward calendar of one day
        calendar.add(DAY, -1);
        while (!isWorkingDay(calendar.getTimeInMillis(), holidays)) {
            calendar.add(DAY, -1);
        }
        return calendar;
    }

    /**
     * @param startTime
     * @param endTime
     * @param holidays  (Optional) Array of holidays
     * @return
     */
    public static Long getWorkingDays(final long startTime, final long endTime,
                                      final Long[] holidays) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(startTime);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(endTime);
        return getWorkingDays(startCalendar, endCalendar, holidays);
    }

    /**
     * @param startDate
     * @param finalDate
     * @param holidays  (Optional) Array of holidays
     * @return
     */
    public static Long getWorkingDays(final Date startDate, final Date finalDate,
                                      final Long[] holidays) {
        return getWorkingDays(startDate.getTime(), finalDate.getTime(), holidays);
    }

    /**
     * @param start
     * @param end
     * @param holidays (Optional) Array of holidays
     * @return
     */
    public static Long getWorkingDays(Calendar start, Calendar end,
                                      final Long[] holidays) {
        Long result = 0L;

        // day of weeks
        int startDayOfWeek = start.get(Calendar.DAY_OF_WEEK);
        int endDayOfWeek = end.get(Calendar.DAY_OF_WEEK);

        // adjust calendars to fist working day
        if (!isWorkingDay(start.getTime(), holidays)) {
            start = nextWorkingDay(start, holidays);
        }
        if (!isWorkingDay(end.getTime(), holidays)) {
            end = previousWorkingDay(end, holidays);
        }

        // difference in day beetween 2 date
        Long dayDiff = Math.abs(dateDiff(start.getTime(), end.getTime()) / (1000L * 60L * 60L * 24L));

        // number of week-ends in period
        Long weekEnds = dayDiff / 7;
        if (startDayOfWeek < endDayOfWeek) {
            weekEnds++;
        }

        result = dayDiff - (weekEnds * highDays.length);

        return result;
    }

    public static Long[] getPeriod(final int startY, final int startM, final int startD,
                                   final int endY, final int endM, final int endD) {
        final Calendar start = Calendar.getInstance();
        start.set(startY, startM - 1, startD);
        final Calendar end = Calendar.getInstance();
        end.set(endY, endM - 1, endD);

        return getPeriod(start, end);
    }

    public static Long[] getPeriod(final Date start, final Date end) {
        final Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        final Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);

        return getPeriod(startCal, endCal);
    }

    public static Long[] getPeriod(final Calendar startCal, final Calendar endCal) {
        final List<Long> result = new LinkedList<Long>();
        // loop
        int count = 0;
        while (true) {
            if (startCal.getTime().after(endCal.getTime())) {
                break;
            } else {
                result.add(startCal.getTimeInMillis());
            }
            startCal.add(DAY, 1);
            count++;
            if (count > 3650) { // 10 years limit
                break; // avoid infinite loop 
            }
        }

        return result.toArray(new Long[result.size()]);
    }

    public static boolean equals(final Date date1, final Date date2) {
        return dateDiff(date1, date2) == 0L;
    }

    public static boolean equals(final Date date1, final Date date2,
                                 final long tolleranceMs) {
        return dateDiff(date1, date2) <= tolleranceMs;
    }

    public static boolean equalsDay(final Date date1, final Date date2) {
        final Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        final Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    public static Date infinite() {
        Date date = DateUtils.encodeDateTime(INFINITE_YEAR, 1, 1, 0, 0, 0);
        return date;
    }

    public static boolean isInfinite(final Date date) {
        final int year = DateUtils.getYear(date);
        final boolean result = year >= INFINITE_YEAR;
        return result;
    }

    public static Date zero() {
        Date date = DateUtils.encodeDateTime(ZERO_YEAR, 1, 1, 0, 0, 0);
        return date;
    }

    public static boolean isZero(final Date date) {
        final int year = DateUtils.getYear(date);
        final boolean result = year <= ZERO_YEAR;
        return result;
    }

    public static Date now() {
        return new Date();
    }

    public static Date yesterday() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(DAY, -1);
        // zero time (hour, minutes, seconds, milliseconds)
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date today() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // zero time (hour, minutes, seconds, milliseconds)
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static boolean isToday(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int year = calendar.get(YEAR);
        final int month = calendar.get(MONTH);
        final int day = calendar.get(DAY);
        final Calendar today = Calendar.getInstance();
        today.setTime(now());
        final int tyear = today.get(YEAR);
        final int tmonth = today.get(MONTH);
        final int tday = today.get(DAY);
        final boolean result = tyear == year
                && tmonth == month
                && tday == day;
        return result;
    }

    public static int getMinutes() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    public static int getMinutes(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSeconds() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.SECOND);
    }

    public static int getSeconds(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    public static int getHourOfDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getHourOfDay(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static int getDayOfYear(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 1=sunday, 2=monday, etc...
     *
     * @return The day of week
     */
    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayOfWeek(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayOfWeekInMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    public static int getDayOfWeekInMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static String getMonthAsString(final Date date) {
        return DateUtils.getMonthAsString(DateUtils.getMonth(date));
    }

    public static String getMonthAsString(final int month) {
        final DateFormatSymbols dfs = new DateFormatSymbols();
        final String[] months = dfs.getMonths();
        return months[month - 1];
    }

    public static String getMonthAsString(final Date date,
                                          final Locale locale) {
        return DateUtils.getMonthAsString(DateUtils.getMonth(date), locale);
    }

    public static String getMonthAsString(final int month,
                                          final Locale locale) {
        final DateFormatSymbols dfs = new DateFormatSymbols(locale);
        final String[] months = dfs.getMonths();
        return months[month - 1];
    }

    public static String getShortMonthAsString(final Date date) {
        return DateUtils.getShortMonthAsString(DateUtils.getMonth(date));
    }

    public static String getShortMonthAsString(final int month) {
        final DateFormatSymbols dfs = new DateFormatSymbols();
        final String[] months = dfs.getShortMonths();
        return months[month - 1];
    }

    public static String getShortMonthAsString(final Date date,
                                               final Locale locale) {
        return DateUtils.getShortMonthAsString(DateUtils.getMonth(date), locale);
    }

    public static String getShortMonthAsString(final int month,
                                               final Locale locale) {
        final DateFormatSymbols dfs = new DateFormatSymbols(locale);
        final String[] months = dfs.getShortMonths();
        return months[month - 1];
    }

    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static int getYear(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Return maximun number of days in month.
     *
     * @param date
     * @return
     */
    public static int getActualMaximumDayOfMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static long getDateLong() {
        return getDateLong(DateUtils.now());
    }

    public static List<Integer> getDateList() {
        return getDateList(DateUtils.now());
    }

    public static Integer[] getDateArray() {
        return getDateArray(DateUtils.now());
    }

    public static int getDateWeek() {
        return getDateWeek(DateUtils.now());
    }

    public static String getDateString() {
        return getDateString(DateUtils.now(), "yyyy-MM-dd");
    }

    public static long getDateLong(final Date date) {
        return date.getTime();
    }

    public static List<Integer> getDateList(final Date date) {
        final DateWrapper datew = new DateWrapper(date);
        final List<Integer> list = new LinkedList<>();
        list.add(datew.getYear());
        list.add(datew.getMonth());
        list.add(datew.getDay());
        return list;
    }

    public static Integer[] getDateArray(final Date date) {
        final List<Integer> list = DateUtils.getDateList(date);
        return list.toArray(new Integer[list.size()]);
    }

    public static int getDateWeek(final Date date) {
        final DateWrapper datew = new DateWrapper(date);
        return datew.getWeek();
    }

    public static String getDateString(final Date date, final String pattern) {
        final DateWrapper datew = new DateWrapper(date);
        return datew.toString(pattern);
    }

    /**
     * @param time Time string with pattern: 10:25, 10.25, 10,25
     * @param now  Time to check
     * @return true if passed time string is expired
     */
    public static boolean isExpiredTime(final String time, final Date now) {
        try {
            if (StringUtils.hasText(time)) {
                final String[] tokens = StringUtils.split(time, new String[]{":", ",", "."}); // 10:25
                final int hour = ConversionUtils.toInteger(tokens[0], -1);
                final int minutes = ConversionUtils.toInteger(tokens[1], -1);
                if (hour > -1 && hour <= DateUtils.getHourOfDay(now)) {
                    if (minutes > -1) {
                        return minutes <= DateUtils.getMinutes(now);
                    }
                    return true;
                }
                return false;
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    /**
     * Calculate if passed time is inside a range of threshold_minutes from now.
     *
     * @param time              Time to check
     * @param now               Current time or time to consider as start time
     * @param threshold_minutes Minutes of validity from now.
     */
    public static boolean isWithinThreashold(final String time,
                                             final Date now,
                                             final int threshold_minutes) {
        try {
            if (StringUtils.hasText(time)) {
                final String[] tokens = StringUtils.split(time, new String[]{":", ",", "."}); // 10:25
                final int hour = ConversionUtils.toInteger(tokens[0], -1);
                final int minutes = ConversionUtils.toInteger(tokens[1], -1);
                final int now_hour = DateUtils.getHourOfDay(now);
                final int now_min = DateUtils.getMinutes(now);
                final int f = threshold_minutes / 60;
                final int th_hour;
                final int th_min;
                if (threshold_minutes > 0) {
                    if (f > 0) {
                        th_hour = f;
                        th_min = threshold_minutes - (f * 60);
                    } else {
                        th_hour = f;
                        th_min = threshold_minutes;
                    }
                } else {
                    th_hour = 0;
                    th_min = 0;
                }
                final int t_now = now_hour * 60 + now_min;
                final int t_now_max = (now_hour + th_hour) * 60 + (now_min + th_min);
                final int t_check = hour * 60 + minutes;

                return t_check>=t_now && t_check<=t_now_max;
            }
        } catch (Throwable ignored) {
        }
        return true;
    }


    /**
     * @param date String date in format yyyyMMdd
     * @param now  Date to check
     * @return true if passed date string is expired
     */
    public static boolean isExpiredDate(final String date, final Date now) {
        try {
            if (StringUtils.hasText(date)) {
                final DateWrapper dw = new DateWrapper(date, DateWrapper.DATEFORMAT_DEFAULT); // yyyyMMdd
                final int year = dw.getYear();
                final int month = dw.getMonth();
                final int day = dw.getDay();
                if (year <= DateUtils.getYear(now)) {
                    if (month <= DateUtils.getMonth(now)) {
                        if (day <= DateUtils.getDayOfMonth(now)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

}
