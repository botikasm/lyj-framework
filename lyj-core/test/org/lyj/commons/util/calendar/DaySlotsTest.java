package org.lyj.commons.util.calendar;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.junit.Test;

import java.util.Map;

public class DaySlotsTest {

    @Test
    public void parse() throws Exception {

        DaySlots day = new DaySlots("en");

        day.parse("puppa");
        assertFalse(day.isValid());

        day.parse("sun 8.30 12.30 15.00 16");
        System.out.println(day);

        day.parse("sun 8.30 12.30 15.00");
        System.out.println(day);

        System.out.println(day.json().toString());

        final Map<String, Object> map = day.map();
        System.out.println(map);

        // COMPARE

        final DaySlots day1 = DaySlots.create("it");
        day1.parse("lun 8 13");

        final DaySlots day2 = DaySlots.create("it");
        day2.parse("LUNEDI 8,00 13,00");

        assertTrue(day1.equals(day2));

        day1.parse("lun 8 13");
        day2.parse("LUN 8 12");
        assertFalse(day1.equals(day2));
        assertTrue(day2.insideOf(day1));
        assertFalse(day1.insideOf(day2));
        assertEquals(day1.match(day2), -1); // day1 contains day2
        assertTrue(day1.outsideOf(day2));
        assertFalse(day2.outsideOf(day1));

        day1.parse("lun 8 13 14 17");     // 8-13 14-17
        day2.parse("LUNEDI 8,00 12,00");  // 8-12
        assertFalse(day1.equals(day2));
        assertFalse(day2.insideOf(day1));
        assertFalse(day1.insideOf(day2));
        assertEquals(day1.match(day2), -2); // does not match
        assertFalse(day1.outsideOf(day2));
        assertFalse(day2.outsideOf(day1));
        JSONArray inside_array = day2.selectInside(day1);
        assertTrue(inside_array.length()==day2.slots().length());
        JSONArray outside_array = day2.selectOuside(day1);
        assertTrue(outside_array.length()==0);

        day1.parse("lun 8 13 14 17");
        day2.parse("LUNEDI 8,30 12,00 14.30 17");
        assertFalse(day1.equals(day2));
        assertTrue(day2.insideOf(day1));
        assertFalse(day1.insideOf(day2));
        assertEquals(day1.match(day2), -1); // day1 contains day2
        assertTrue(day1.outsideOf(day2));
        assertFalse(day2.outsideOf(day1));
        inside_array = day2.selectInside(day1);
        assertTrue(inside_array.length()==day1.slots().length());
    }


}