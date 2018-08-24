package org.ly.ose.server.application.programming.tools.ose;

import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.DateUtils;

/**
 * OS-Enterprise cross-invocation utility
 */
public class Tool_ose
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "ose"; // used as $ose.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _package_name;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_ose(final OSEProgram program) {
        super(NAME, program);

        _package_name = super.info().fullName();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    public String timestamp() {
        return DateUtils.timestamp() + "";
    }


}
