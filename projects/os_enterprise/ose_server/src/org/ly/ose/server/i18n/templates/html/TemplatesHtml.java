package org.ly.ose.server.i18n.templates.html;

import org.lyj.commons.i18n.impl.BaseDictionary;

/**
 * EMAIL TEMPLATES
 */
public class TemplatesHtml
        extends BaseDictionary {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String NAME = "errors_i18n";

    public static final String TPL_ERROR = "html/error";

    public static final String FLD_ERROR = "error";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    @Override
    public String getName() {
        return NAME;
    }


}
