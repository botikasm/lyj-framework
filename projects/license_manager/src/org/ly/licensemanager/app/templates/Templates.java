package org.ly.licensemanager.app.templates;

import org.ly.ext.mail.templates.TemplateManager;
import org.ly.licensemanager.app.templates.emails.TemplatesEmail;
import org.ly.licensemanager.app.templates.html.TemplatesHtml;
import org.ly.licensemanager.app.templates.system.I18nSystem;
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
    public static final String TPL_CHANGE_PASSWORD = "emails/change_password";
    public static final String TPL_CHANGED_PASSWORD = "emails/changed_password";
    public static final String TPL_CONFIRM_ACCOUNT = "emails/confirm_account";
    public static final String TPL_CONFIRMED_ACCOUNT = "emails/confirmed_account";

    public static final String TPL_CONTEST_END_WIN = "emails/contest_end_win";
    public static final String TPL_CONTEST_END_NO_WIN = "emails/contest_end_no_win";
    public static final String TPL_WELCOME = "emails/welcome";
    public static final String TPL_INVITE = "emails/invite";
    public static final String TPL_INVITE_SENT = "emails/invite_sent";
    public static final String TPL_INVITE_ACCEPTED = "emails/invite_accepted";
    public static final String TPL_NOTIFY_CONTESTS = "emails/notify_contests";

    //-- FIELDS --//
    public static final String TITLE = "TITLE";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String FRIEND_NAME = "FRIEND_NAME";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_FULL_NAME = "USER_FULL_NAME";
    public static final String USER_ADDRESS = "USER_ADDRESS";
    public static final String CONTEST_TITLE = "CONTEST_TITLE";
    public static final String CONTEST_POS = "CONTEST_POS";
    public static final String CONTEST_SCORE = "CONTEST_SCORE";
    public static final String SPONSOR_URL = "SPONSOR_URL";
    public static final String SPONSOR_NAME = "SPONSOR_NAME";
    public static final String PRIZE_NAME = "PRIZE_NAME";
    public static final String PRIZE_URL = "PRIZE_URL";
    public static final String PRIZE_DELIVERY = "PRIZE_DELIVERY";
    public static final String LINK = "LINK";
    public static final String SHORT_LINK = "SHORT_LINK";
    public static final String ERROR = "ERROR";
    public static final String CONTESTS_TXT = "CONTESTS_TXT";
    public static final String CONTESTS_HTML = "CONTESTS_HTML";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected Templates() {

        //-- contest --//
        super.register(TemplatesEmail.class);
        super.register(I18nSystem.class);
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
