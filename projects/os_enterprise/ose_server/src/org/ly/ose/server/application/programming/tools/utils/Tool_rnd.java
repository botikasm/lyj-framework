package org.ly.ose.server.application.programming.tools.utils;

import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.RandomUtils;

/**
 * Database utility class
 * *
 * Usage:
 * $db.collection('test').find().....
 * $db.name('system_utils').collection('test').find()....
 * *
 * *
 */
public class Tool_rnd
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "rnd"; // used as $rnd.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _package_name;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_rnd(final OSEProgram program) {
        super(NAME, program);

        _package_name = super.info().fullName();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    public String uuid() {
        return RandomUtils.randomUUID(true);
    }

    public String digits(final int count_digits) {
        return RandomUtils.randomNumeric(count_digits);
    }

    public String ascii(final int count_chars) {
        return RandomUtils.randomAscii(count_chars);
    }

    public String text(final int count_chars) {
        return RandomUtils.randomAlphabetic(count_chars);
    }

    public int timeBased() {
        return RandomUtils.getTimeBasedRandomInteger();
    }

    public int timeBased(final int count_chars) {
        return RandomUtils.getTimeBasedRandomInteger(count_chars);
    }

    public Number number(final Object num1, final Object num2) {
        return RandomUtils.rnd(num1, num2);
    }



}
