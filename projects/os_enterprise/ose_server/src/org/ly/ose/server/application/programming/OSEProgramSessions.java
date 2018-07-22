package org.ly.ose.server.application.programming;

/**
 * SessionManager for OSE Programs
 */
public class OSEProgramSessions {

    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private OSEProgramSessions(){

    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    

    // ------------------------------------------------------------------------
    //                     S I N G L E T O N
    // ------------------------------------------------------------------------

    private static OSEProgramSessions __instance;

    public static synchronized OSEProgramSessions instance() {
        if (null == __instance) {
            __instance = new OSEProgramSessions();
        }
        return __instance;
    }

}
