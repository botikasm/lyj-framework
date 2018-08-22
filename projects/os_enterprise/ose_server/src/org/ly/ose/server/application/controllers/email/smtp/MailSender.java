package org.ly.ose.server.application.controllers.email.smtp;

import org.json.JSONObject;
import org.ly.ext.mail.SmtpRequest;
import org.lyj.commons.util.StringUtils;

import java.util.Map;

public class MailSender {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MailConfigSmtp _config;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MailSender(final JSONObject config) throws Exception {
        _config = new MailConfigSmtp(config);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isReady() {
        return _config.enabled();
    }

    public boolean send(final String[] target,
                        final String reply_to,
                        final String subject,
                        final String body_text,
                        final String body_html) {
        return this.send(null, target, reply_to, subject, body_text, body_html);
    }

    public boolean send(final Map<String, String> headers,
                        final String[] target,
                        final String reply_to,
                        final String subject,
                        final String body_text,
                        final String body_html) {
        if (target.length > 0) {
            final SmtpRequest message = this.message(reply_to);
            message.subject(subject);
            message.bodyHtml(body_html);
            if (StringUtils.hasText(body_text)) {
                message.bodyText(body_text);
            }
            message.addresses(target);

            if (null != headers && !headers.isEmpty()) {
                message.headers().putAll(headers);
            }

            return message.send();
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private SmtpRequest message(final String reply_to) {
        final SmtpRequest message = new SmtpRequest();

        message.host(_config.connectionHost());
        message.port(_config.connectionPort());
        message.tls(_config.connectionIsTLS());
        message.ssl(_config.connectionIsSSLS());
        message.user(_config.connectionUsername());
        message.password(_config.connectionPassword());
        message.from(_config.infoFrom());
        message.replyTo(StringUtils.hasText(reply_to) ? reply_to : _config.infoReplyTo());

        return message;
    }

}
