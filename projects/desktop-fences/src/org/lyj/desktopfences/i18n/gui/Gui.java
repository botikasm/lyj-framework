package org.lyj.desktopfences.i18n.gui;

import org.lyj.commons.i18n.impl.BaseDictionary;

/**
 * Templates for notification messages.
 */
public class Gui
        extends BaseDictionary{

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String NAME = "gui";

    public static final String FLD_SEARCH_TOURNAMENTS_PROMPT = "_fld_search_tournaments.prompt";
    public static final String FLD_SEARCH_USER_PROMPT = "_fld_search_users.prompt";
    public static final String FLD_SEARCH_APPS_PROMPT = "_fld_search_apps.prompt";

    public static final String MSG_CONFIRM_REMOVE = "_msg_confirm_remove";
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    @Override
    public String getName() {
        return NAME;
    }


}
