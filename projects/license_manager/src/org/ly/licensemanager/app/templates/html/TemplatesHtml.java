package org.ly.licensemanager.app.templates.html;

import org.lyj.commons.i18n.impl.BaseDictionary;

/**
 * Templates for Contest.
 */
public class TemplatesHtml
        extends BaseDictionary {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String NAME = "html_i18n";

    public static final String TPL_EMAIL_CONFIRMED = "html/email_confirmed";
    public static final String TPL_INVOICE = "html/invoice";
    public static final String TPL_SOCIAL_SHARE = "html/social_share";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    @Override
    public String getName() {
        return NAME;
    }


}
