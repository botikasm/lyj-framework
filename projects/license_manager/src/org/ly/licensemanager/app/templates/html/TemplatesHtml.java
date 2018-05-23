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

    public static final String TPL_SUBMIT_REGISTRATION_IFRAME = "html/registration_submit_iframe";
    public static final String TPL_SUBMIT_REGISTRATION = "html/registration_submit";
    public static final String TPL_CONFIRM_REGISTRATION = "html/registration_confirm";

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
