package org.ly.licensemanager.app.server.listeners.api.routing.impl;

import org.ly.ext.mail.SmtpRequest;
import org.ly.licensemanager.IConstants;
import org.ly.licensemanager.app.controllers.license.LicenseController;
import org.ly.licensemanager.app.controllers.license.registry.LicenseItem;
import org.ly.licensemanager.app.controllers.license.registry.LicenseRegistry;
import org.ly.licensemanager.app.server.listeners.api.routing.AbstractApi;
import org.ly.licensemanager.app.templates.Templates;
import org.ly.licensemanager.app.templates.emails.TemplatesEmail;
import org.ly.licensemanager.app.templates.html.TemplatesHtml;
import org.ly.licensemanager.deploy.config.ConfigHelper;
import org.lyj.commons.async.Async;
import org.lyj.commons.util.*;
import org.lyj.ext.netty.server.web.HttpServerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * LICENSE MANAGER API
 */
public class ApiLicense
        extends AbstractApi {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String FORM_ACTION_REGISTER = PathUtils.concat(ConfigHelper.instance().apiHost(), "api/license/register");

    private static final String PARAM_NAME = "name";
    private static final String PARAM_UID = "uid";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_POSTPONE_DAYS = "postpone_days";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ApiLicense() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void redirect_form_register(final HttpServerContext context) {
        final String root = context.rootUrl();
        final String url = PathUtils.concat(root, "/api/license/form_register/" + IConstants.APP_TOKEN_COINMULE_API);
        context.writeRedirect(url);
    }

    public void form_register_iframe(final HttpServerContext context) {

        final String root = context.rootUrl();

        try {
            final String response = Templates.instance().getTemplateHTML(context.getLang(),
                    TemplatesHtml.TPL_SUBMIT_REGISTRATION_IFRAME, MapBuilder.createSS()
                            .put("ROOT", root)
                            .toMap());

            // return html response
            super.writeHTML(context, response);
        } catch (Throwable t) {
            super.writeError(context, t, "form_register_iframe");
        }

    }

    /**
     * https://localhost:4001/api/license/form_register/coinmule_lm18
     * https://api.conversacon.com:4001/api/license/form_register/coinmule_lm18
     */
    public void form_register(final HttpServerContext context) {
        final String token = super.getParamToken(context);

        super.auth(token, (err, valid) -> {
            if (null == err) {
                // params
                final String uid = super.getParam(context, PARAM_UID);

                //if (StringUtils.hasText(uid)) {

                try {
                    final String response = Templates.instance().getTemplateHTML(context.getLang(),
                            TemplatesHtml.TPL_SUBMIT_REGISTRATION, MapBuilder.createSS()
                                    .put("FORM_ACTION", FORM_ACTION_REGISTER)
                                    .put("APP_TOKEN", token)
                                    .put("UID", uid)
                                    .toMap());

                    // return html response
                    super.writeHTML(context, response);
                } catch (Throwable t) {
                    super.writeError(context, t, "form_register");
                }
                //} else {
                //    super.writeErroMissingParams(context, PARAM_UID);
                //}
            } else {
                super.writeError(context, err);
            }
        });
    }

    /**
     * https://localhost:4001/api/license/register/coinmule_lm18/uid_hgajdgadjsg/angelo.geminiani@gmail.com/angelo
     * https://api.conversacon.com:4001/api/license/register/coinmule_lm18/uid_hgajdgadjsg/angelo.geminiani@gmail.com/angelo
     * https://api.conversacon.com:4001/api/license/register/coinmule_lm18/:uid/:email/:name
     */
    public void register(final HttpServerContext context) {
        final String token = super.getParamToken(context);

        super.auth(token, (err, valid) -> {
            if (null == err) {
                // params
                final String email = super.getParam(context, PARAM_EMAIL);
                final String uid = super.getParam(context, PARAM_UID);
                final String name = super.getParam(context, PARAM_NAME);

                if (StringUtils.hasText(uid) && StringUtils.hasText(email)) {

                    try {

                        if (StringUtils.hasText(email) && !RegExpUtils.isValidEmail(email)) {
                            throw new Exception("Invalid Email: " + email);
                        }

                        // get or create a registry (Registry should be a valid auth_token)
                        final LicenseRegistry registry = LicenseController.instance().registry(token);
                        final LicenseItem license = registry.register(this.clean(uid), email, name, context.getLang());

                        this.notifyRegister(license);

                        final String response = Templates.instance().getTemplateHTML(context.getLang(),
                                TemplatesHtml.TPL_CONFIRM_REGISTRATION, new HashMap());

                        // return html response
                        super.writeHTML(context, response);
                    } catch (Throwable t) {
                        super.writeError(context, t, "register");
                    }
                } else {
                    super.writeErroMissingParams(context, PARAM_UID, PARAM_EMAIL);
                }
            } else {
                super.writeError(context, err);
            }
        });
    }

    /**
     * https://localhost:4001/api/license/validate/coinmule_lm18/uid_hgajdgadjsg/json
     * https://api.conversacon.com:4001/api/license/validate/coinmule_lm18/uid_hgajdgadjsg/json
     * https://api.conversacon.com:4001/api/license/validate/coinmule_lm18/:uid/:format
     */
    public void validate(final HttpServerContext context) {
        final String token = super.getParamToken(context);

        super.auth(token, (err, valid) -> {
            if (null == err) {
                // params
                final String uid = super.getParam(context, PARAM_UID);
                final String format = super.getParam(context, PARAM_FORMAT, "json");

                if (StringUtils.hasText(uid)) {

                    try {

                        // get or create a registry (Registry should be a valid auth_token)
                        final LicenseRegistry registry = LicenseController.instance().registry(token);
                        final boolean response = registry.isValidLicense(this.clean(uid));

                        if ("json".equalsIgnoreCase(format)) {
                            super.writeJSON(context, response);
                        } else if ("raw".equalsIgnoreCase(format)) {
                            super.writeRawValue(context, response);
                        } else {
                            super.writeJSON(context, response);
                        }
                    } catch (Throwable t) {
                        super.writeError(context, t, "register");
                    }
                } else {
                    super.writeErroMissingParams(context, PARAM_UID);
                }
            } else {
                super.writeError(context, err);
            }
        });
    }

    public void get(final HttpServerContext context) {
        final String token = super.getParamToken(context);

        super.auth(token, (err, valid) -> {
            if (null == err) {
                // params
                final String uid = super.getParam(context, PARAM_UID);

                if (StringUtils.hasText(uid)) {

                    try {

                        // get or create a registry (Registry should be a valid auth_token)
                        final LicenseRegistry registry = LicenseController.instance().registry(token);
                        final LicenseItem response = registry.getLicense(uid);

                        super.writeJSON(context, response);

                    } catch (Throwable t) {
                        super.writeError(context, t, "get");
                    }
                } else {
                    super.writeErroMissingParams(context, PARAM_UID);
                }
            } else {
                super.writeError(context, err);
            }
        });
    }

    /**
     * https://localhost:4001/api/license/enable/coinmule_lm18/UID
     */
    public void enable(final HttpServerContext context) {
        final String token = super.getParamToken(context);

        super.auth(token, (err, valid) -> {
            if (null == err) {
                // params
                final String uid = super.getParam(context, PARAM_UID);

                if (StringUtils.hasText(uid)) {

                    try {

                        // get or create a registry (Registry should be a valid auth_token)
                        final LicenseRegistry registry = LicenseController.instance().registry(token);
                        final LicenseItem response = registry.getLicense(uid);
                        response.enabled(true);
                        registry.update(response);

                        super.writeJSON(context, response);

                    } catch (Throwable t) {
                        super.writeError(context, t, "enable");
                    }
                } else {
                    super.writeErroMissingParams(context, PARAM_UID);
                }
            } else {
                super.writeError(context, err);
            }
        });
    }

    public void disable(final HttpServerContext context) {
        final String token = super.getParamToken(context);

        super.auth(token, (err, valid) -> {
            if (null == err) {
                // params
                final String uid = super.getParam(context, PARAM_UID);

                if (StringUtils.hasText(uid)) {

                    try {

                        // get or create a registry (Registry should be a valid auth_token)
                        final LicenseRegistry registry = LicenseController.instance().registry(token);
                        final LicenseItem response = registry.getLicense(uid);
                        response.enabled(false);
                        registry.update(response);

                        super.writeJSON(context, response);

                    } catch (Throwable t) {
                        super.writeError(context, t, "disable");
                    }
                } else {
                    super.writeErroMissingParams(context, PARAM_UID);
                }
            } else {
                super.writeError(context, err);
            }
        });
    }

    public void postpone(final HttpServerContext context) {
        final String token = super.getParamToken(context);

        super.auth(token, (err, valid) -> {
            if (null == err) {
                // params
                final String uid = super.getParam(context, PARAM_UID);
                final int postpone_days = ConversionUtils.toInteger(super.getParam(context, PARAM_POSTPONE_DAYS));

                if (StringUtils.hasText(uid)) {

                    try {

                        // get or create a registry (Registry should be a valid auth_token)
                        final LicenseRegistry registry = LicenseController.instance().registry(token);
                        final LicenseItem response = registry.getLicense(uid);

                        if (postpone_days > 0) {
                            response.postpone(postpone_days);
                            registry.update(response);
                        }

                        super.writeJSON(context, response);

                    } catch (Throwable t) {
                        super.writeError(context, t, "postpone");
                    }
                } else {
                    super.writeErroMissingParams(context, PARAM_UID);
                }
            } else {
                super.writeError(context, err);
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String clean(final String value) {
        return StringUtils.hasText(value)
                ? StringUtils.replace(value, " ", "").trim()
                : "";
    }

    private void notifyRegister(final LicenseItem license) {
        if (null != license) {
            Async.invoke((args) -> {
                final String[] recipients = ConfigHelper.instance().apiNotifyTo();
                if (null != recipients && recipients.length > 0) {
                    final String license_uid = license.uid();
                    final String license_name = license.name();
                    final String license_email = license.email();
                    final String url_show = PathUtils.concat(ConfigHelper.instance().apiHost(),
                            "api/license/get/" + IConstants.APP_TOKEN_COINMULE_API + "/" + license_uid);
                    final String url_enable = PathUtils.concat(ConfigHelper.instance().apiHost(),
                            "api/license/enable/" + IConstants.APP_TOKEN_COINMULE_API + "/" + license_uid);
                    final String url_postpone_30 = PathUtils.concat(ConfigHelper.instance().apiHost(),
                            "api/license/postpone/" + IConstants.APP_TOKEN_COINMULE_API + "/" + license_uid + "/30");
                    final String url_postpone_365 = PathUtils.concat(ConfigHelper.instance().apiHost(),
                            "api/license/postpone/" + IConstants.APP_TOKEN_COINMULE_API + "/" + license_uid + "/365");

                    final Map<String, String> params = new HashMap<>();
                    params.put("LANG", license.lang());
                    params.put("UID", license_uid);
                    params.put("NAME", license_name);
                    params.put("EMAIL", license_email);
                    params.put("URL_SHOW", url_show);
                    params.put("URL_ENABLE", url_enable);
                    params.put("URL_POSTPONE_30", url_postpone_30);
                    params.put("URL_POSTPONE_365", url_postpone_365);
                    final String subject = Templates.instance().getSubject(license.lang(),
                            TemplatesEmail.TPL_LICENSE_REQUEST);
                    final String body_txt = Templates.instance().getTemplateTXT(license.lang(),
                            TemplatesEmail.TPL_LICENSE_REQUEST,
                            params);
                    final String body_html = Templates.instance().getTemplateHTML(license.lang(),
                            TemplatesEmail.TPL_LICENSE_REQUEST,
                            params);

                    sendEmail(subject, body_html, body_txt, recipients);
                }
            });
        }
    }

    private void sendEmail(final String subject,
                           final String body_html,
                           final String body_text,
                           final String[] emails) {
        final SmtpRequest message = message("");
        message.subject(subject);
        message.bodyHtml(body_html);
        if (StringUtils.hasText(body_text)) {
            message.bodyText(body_text);
        }
        message.addresses(emails);

        message.send();
    }

    private static SmtpRequest message(final String reply_to) {
        final SmtpRequest message = new SmtpRequest();
        message.host(ConfigHelper.instance().mailStmp().connectionHost());
        message.port(ConfigHelper.instance().mailStmp().connectionPort());
        message.tls(ConfigHelper.instance().mailStmp().connectionIsTLS());
        message.ssl(ConfigHelper.instance().mailStmp().connectionIsSSLS());
        message.user(ConfigHelper.instance().mailStmp().connectionUsername());
        message.password(ConfigHelper.instance().mailStmp().connectionPassword());
        message.from(ConfigHelper.instance().mailStmp().infoFrom());
        message.replyTo(StringUtils.hasText(reply_to) ? reply_to : ConfigHelper.instance().mailStmp().infoReplyTo());

        return message;
    }
}
