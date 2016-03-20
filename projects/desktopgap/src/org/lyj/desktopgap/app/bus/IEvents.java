package org.lyj.desktopgap.app.bus;

/**
 * Events used in message bus
 */
public interface IEvents {

    // ------------------------------------------------------------------------
    //                      s y s t e m   e v e n t s
    // ------------------------------------------------------------------------
    public static final String ETAG_SYSTEM= "tag_system";
    public static final String ENAME_QUIT= "on_quit"; // closed application

    // ------------------------------------------------------------------------
    //                      c o n n e c t i o n
    // ------------------------------------------------------------------------

    public static final String ETAG_CONNECTION = "tag_connection";
    public static final String ENAME_CONNECTION = "on_connection";


}
