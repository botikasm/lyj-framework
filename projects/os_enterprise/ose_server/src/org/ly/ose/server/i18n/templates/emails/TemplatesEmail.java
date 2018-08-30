package org.ly.ose.server.i18n.templates.emails;

import org.lyj.commons.i18n.impl.BaseDictionary;

/**
 * EMAIL TEMPLATES
 */
public class TemplatesEmail
        extends BaseDictionary {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String NAME = "emails_i18n";

    public static final String TPL_FAIL_BUY = "emails/fail_buy";
    public static final String TPL_LICENSE_NEW_TOADMIN = "emails/license_new_toadmin";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    @Override
    public String getName() {
        return NAME;
    }


}
