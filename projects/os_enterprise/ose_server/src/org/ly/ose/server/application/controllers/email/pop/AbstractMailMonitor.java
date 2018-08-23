package org.ly.ose.server.application.controllers.email.pop;

import org.json.JSONObject;
import org.ly.ext.mail.reader.MailMessage;
import org.ly.ext.mail.reader.MailReader;
import org.lyj.commons.async.future.Timed;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMailMonitor
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int INTERVAL = 30; // seconds

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MailConfigPop _config;
    private final String _logger_name;
    private Timed _timed;
    private int _error_count;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractMailMonitor(final JSONObject config) {
        this(new MailConfigPop(config));
    }

    public AbstractMailMonitor(final MailConfigPop config) {
        _config = config;
        _error_count = 0;

        // logger
        _logger_name = this.getClass().getSimpleName().toLowerCase() + ".log";

        LoggingRepository.getInstance().setLogFileName(this.getClass(), _logger_name);
    }

    protected abstract boolean handle(final MailMessage message);

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean enabled() {
        return _config.enabled();
    }

    public String user() {
        return _config.user();
    }

    public String loggerName() {
        return _logger_name;
    }

    public void open() {
        if (_config.enabled()) {
            this.init();
        } else {
            super.info("open", "Email Monitor is not enabled.");
        }
    }

    public void close() {
        if (null != _timed) {
            _timed.stop(true);
            _timed = null;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        this.close();

        _timed = new Timed(INTERVAL);
        _timed.start(this::handle);

        super.info("mailclient#init",
                FormatUtils.format("Mail Listener enabled on server '%s' port '%s'. Listening interval: %s seconds.",
                        _config.server(), _config.port(), INTERVAL));
    }

    private void handle(final Timed.TaskInterruptor interruptor) {
        try {
            if (null == _timed || !_config.enabled()) {
                interruptor.stop();
            }
            // read email
            final MailReader reader = new MailReader(_config.server(), _config.port(), _config.user(), _config.psw());
            reader.removeAfterRead(true); // remove read emails  TODO: SET FALSE IF MESSAGES ARE MANUALLY REMOVED

            // check
            final Collection<MailMessage> messages = reader.check();
            if (!messages.isEmpty()) {
                super.info("check-email",
                        FormatUtils.format("Received %s emails. Now parse to check attachments and authorizations...",
                                messages.size()));

                final List<MailMessage> monitored = new ArrayList<>();
                for (final MailMessage message : messages) {
                    if (this.handleMessage(message)) {
                        monitored.add(message);
                    }
                }

                // remove emails (manually remove in a separeted thread)
                //this.removeEmails(installed);
            }
        } catch (Throwable t) {
            // error checking email
            _error_count++;
            if (_error_count == 1) {
                // log only once
                super.error("mailclient#handle", t);
            }
        }
    }

    private void removeEmails(final List<MailMessage> to_remove) {
        try {
            final MailReader reader = new MailReader(_config.server(), _config.port(), _config.user(), _config.psw());
            // remove emails
            for (final MailMessage message : to_remove) {
                reader.delete(message);
            }
        } catch (Throwable t) {
            super.error("check-email.removeEmails", t);
        }
    }

    private boolean handleMessage(final MailMessage message) {
        if (message.isReady()) {
            final String[] sender = message.info().fromAsEmailArray();
            if (sender.length > 0 && this.hasValidSubject(message) && this.authorized(sender)) {
                return this.handle(message); // remove this email
            } else {
                if (sender.length > 0) {
                    super.warning("checkMessage",
                            FormatUtils.format("Found unauthorized message from: %s",
                                    StringUtils.toString(sender)));
                }
            }
        } else {
            super.error("checkMessage",
                    FormatUtils.format("Message has errors: %s",
                            message.info().error()));
        }
        return false;
    }

    private boolean hasValidSubject(final MailMessage message) {
        try {
            final String cfg_subject = _config.subject().trim();
            if (StringUtils.hasText(cfg_subject)) {
                final String subject = message.info().subject().trim();
                return cfg_subject.equalsIgnoreCase(subject);
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    private boolean authorized(final String[] sender) {
        try {
            for (final String email : sender) {
                if (StringUtils.hasText(email)) {
                    // lookup a client with this email
                    final String[] authorized = _config.authorized();
                    if (CollectionUtils.contains(authorized, email)) {
                        return true;
                    } else {
                        // warning
                        super.warning("authorize", FormatUtils.format("Received email from '%s'. This email does not have an ACCOUNT.", email));
                        return true;
                    }
                }
            }
        } catch (Throwable t) {
            super.error("authorized", t);
        }
        return false;
    }


}

