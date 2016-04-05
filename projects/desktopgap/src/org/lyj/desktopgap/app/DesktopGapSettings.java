package org.lyj.desktopgap.app;

import org.lyj.Lyj;
import org.lyj.commons.io.db.jsonproperties.JsonProperties;

/**
 *
 */
public class DesktopGapSettings
        extends JsonProperties {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "app-settings";

    private static final String SYS_URI_HOME = "uri_home";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopGapSettings() {
        super(Lyj.getAbsolutePath(ROOT));
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uriHome() {
        return super.getString(SYS_URI_HOME);
    }

    DesktopGapSettings uriHome(final String value) {
        super.set(SYS_URI_HOME, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------







}
