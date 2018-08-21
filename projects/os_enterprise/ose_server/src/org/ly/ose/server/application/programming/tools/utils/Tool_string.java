package org.ly.ose.server.application.programming.tools.utils;

import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.RegExpUtils;
import org.lyj.commons.util.StringUtils;

/**
 *
 */
public class Tool_string
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "string"; // used as $string.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _package_name;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_string(final OSEProgram program) {
        super(NAME, program);

        _package_name = super.info().fullName();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    public String md5(final String text) {
        return MD5.encode(text);
    }

    public boolean isEmail(final Object email) {
        return RegExpUtils.isValidEmail(StringUtils.toString(email));
    }


}
