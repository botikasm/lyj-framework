package org.lyj.desktopfences.app;

import org.json.JSONObject;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.desktopgap.app.DesktopGap;
import org.lyj.desktopgap.app.DesktopGapSettings;

/**
 *
 */
public class DesktopFencesSettings {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String SYS_TELEPORT = "teleport";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final DesktopGapSettings _settings;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopFencesSettings() {
        _settings = DesktopGap.instance().settings();
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject toJson() {
        return _settings.toJson();
    }

    public String uriHome() {
        return _settings.uriHome();
    }

    public String teleport() {
        return _settings.getString(SYS_TELEPORT);
    }

    public DesktopFencesSettings teleport(final String value) {
        _settings.set(SYS_TELEPORT, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        //-- sys --//
        if (!StringUtils.hasText(this.teleport())) {
            this.teleport(PathUtils.getDesktopDirectory("teleport", false));
        }
    }


}
