package org.ly.ose.server.application.programming.tools.remote;

import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONObject;
import org.ly.ose.server.application.controllers.email.smtp.MailSender;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramToolRequest;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.utils.Converter;

/**
 * SMTP helper
 */
public class Tool_smtp
        extends OSEProgramToolRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "smtp"; // used as $smtp.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_smtp(final OSEProgram program) {
        super(NAME, program);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    /**
     * Send email and return error if any
     */
    public Smtp open(final Object raw_settings) {
        return new Smtp(Converter.toJsonObject(raw_settings));
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class Smtp {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        final JSONObject _config;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        private Smtp(final JSONObject config) {
            _config = config;
        }

        // ------------------------------------------------------------------------
        //                      p u b l ic
        // ------------------------------------------------------------------------

        /**
         * Send email and return error if any
         */
        public String send(final String subject,
                           final String message,
                           final Object raw_target) {
            try {
                if (StringUtils.hasText(subject) && StringUtils.hasText(message)) {
                    final String[] target = Converter.toStringArray(raw_target);
                    if (target.length > 0) {
                        send(_config, subject, message, message, target, "");
                    } else {
                        return "Missing target. Cannot send email to nobody.";
                    }
                } else {
                    return "Missing subject or message body.";
                }
            } catch (Throwable t) {
                return t.getMessage();
            }
            return null;
        }

        /**
         * Send message async and return error if any into callback
         */
        public void sendAsync(final String subject,
                              final String message,
                              final Object raw_target,
                              final JSObject callback) {
            try {
                if (StringUtils.hasText(subject) && StringUtils.hasText(message)) {
                    final String[] target = Converter.toStringArray(raw_target);
                    if (target.length > 0) {
                        send(_config, subject, message, message, target, "");

                        invoke(null, callback);
                    } else {
                        invoke(new Exception("Missing target. Cannot send email to nobody."), callback);
                    }
                } else {
                    invoke(new Exception("Missing subject or message body."), callback);
                }
            } catch (Throwable t) {
                invoke(t, callback);
            }
        }

        // ------------------------------------------------------------------------
        //                      S T A T I C
        // ------------------------------------------------------------------------

        private static void send(final JSONObject settings,
                                 final String subject,
                                 final String message_text,
                                 final String message_html,
                                 final String[] target,
                                 final String reply_to) throws Exception {
            final MailSender sender = new MailSender(settings);

            sender.send(target, reply_to, subject, message_text, message_html);
        }

        private static void invoke(final Throwable err, final JSObject callback) {
            if (null != callback && callback.isFunction()) {
                if (null == err) {
                    callback.call(null, false);
                } else {
                    callback.call(null, err.getMessage());
                }
            }
        }


    }

}
