package org.ly.server.i18n.templates;

import org.ly.ext.mail.templates.TemplateManager;
import org.ly.server.i18n.templates.emails.TemplatesEmail;
import org.ly.server.i18n.templates.html.TemplatesHtml;
import org.ly.server.i18n.templates.html.TemplatesHtml;
import org.lyj.commons.util.FormatUtils;
import org.lyj.ext.html.CSSInlineStyler;

import java.util.Map;

/**
 * Manage templates and templates localizations.
 * How to use:
 * - Create a class template dictionary using camel case for class name. ex: Sample.java
 * - Create a resource bundle with same name as template dictionary class. ex: Sample.properties
 * - In resource bundle declare some fields: subject, html, txt using template name (lowercase of class name, 'sample')
 * as prefix. ex: "sample.subject", "sample.html", "sample.txt".
 * "sample.subject": the email subject
 * "sample.html": HTML version of your email template (file name)
 * "sample.txt": TEXT version of your email (file name)
 * - Create template files in same package.
 */
public class Templates
        extends TemplateManager {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------
    private static final String PREFIX = "*|";
    private static final String SUFFIX = "|*";
    public static final String TYPE_HTML = "html";
    public static final String TYPE_TEXT = "txt";

    //-- TEMPLATES --//
    public static final String TPL_ERROR = "system/error";

    public static final String TPL_FAIL_BUY = "emails/fail_buy";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected Templates() {

        //-- contest --//
        super.register(TemplatesEmail.class);
        super.register(TemplatesHtml.class);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String getSubject(final String lang, final String templateName,
                             final Map<String, ?> data) {
        final String template = super.getSubject(lang, templateName);
        return FormatUtils.formatTemplate(template, PREFIX, SUFFIX, data);
    }

    public String getTemplateHTML(final String lang, final String templateName, final Map<String, ?> data) {
        return getTemplate(lang, templateName, TYPE_HTML, data);
    }

    public String getTemplateTXT(final String lang, final String templateName, final Map<String, ?> data) {
        return getTemplate(lang, templateName, TYPE_TEXT, data);
    }

    public String getTemplate(final String lang, final String templateName,
                              final String type, final Map<String, ?> data) {
        final String template = super.getTemplate(lang, templateName, type);
        if (TYPE_HTML.equals(type)) {
            final String inlinehtml = CSSInlineStyler.instance().convert(template);
            return FormatUtils.formatTemplate(inlinehtml, PREFIX, SUFFIX, data, true);
        } else {
            return FormatUtils.formatTemplate(template, PREFIX, SUFFIX, data, true);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static Templates __instance;

    public static synchronized Templates instance() {
        if (null == __instance) {
            __instance = new Templates();
        }
        return __instance;
    }


}
