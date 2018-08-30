package org.ly.ose.server.application.programming.tools.license;

import org.json.JSONObject;
import org.ly.ose.server.application.controllers.email.smtp.MailSender;
import org.ly.ose.server.application.controllers.license.LicenseItem;
import org.ly.ose.server.i18n.templates.Templates;
import org.ly.ose.server.i18n.templates.emails.TemplatesEmail;
import org.lyj.commons.async.Async;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class LicenseToolMailHelper {


    private final MailSender _sender;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LicenseToolMailHelper(final JSONObject smpt_config) throws Exception {
        _sender = new MailSender(smpt_config);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void notify(final LicenseItem license) {
        if (null != license
                && _sender.config().infoAdminAddresses().length > 0) {
            // this.notifyRegister(license, _sender.config().infoAdminAddresses());
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void notifyRegister(final LicenseItem license,
                                final String[] recipients) {
        if (null != license) {
            Async.invoke((args) -> {
                try {
                    if (null != recipients && recipients.length > 0) {
                        final String license_uid = license.uid();
                        final String license_name = license.name();
                        final String license_email = license.email();

                        final Map<String, String> params = new HashMap<>();
                        params.put("LANG", license.lang());
                        params.put("UID", license_uid);
                        params.put("NAME", license_name);
                        params.put("EMAIL", license_email);
                        params.put("LICENSE", license.toString());

                        final String subject = Templates.instance().getSubject(license.lang(),
                                TemplatesEmail.TPL_LICENSE_NEW_TOADMIN);
                        final String body_txt = Templates.instance().getTemplateTXT(license.lang(),
                                TemplatesEmail.TPL_LICENSE_NEW_TOADMIN,
                                params);
                        final String body_html = Templates.instance().getTemplateHTML(license.lang(),
                                TemplatesEmail.TPL_LICENSE_NEW_TOADMIN,
                                params);

                        sendEmail(subject, body_html, body_txt, recipients);
                    }
                } catch (Throwable ignored) {
                    // error sending email
                }
            });
        }
    }

    private void sendEmail(final String subject,
                           final String body_html,
                           final String body_text,
                           final String[] emails) throws Exception {
        _sender.send(emails, "", subject, body_text, body_html);
    }

}
