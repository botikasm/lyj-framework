package org.ly.appsupervisor.app.loop.launcher.controllers;

import org.ly.appsupervisor.app.loop.launcher.ExecMonitor;
import org.ly.appsupervisor.app.loop.launcher.exec.Executable;
import org.ly.appsupervisor.app.model.ModelAction;
import org.ly.appsupervisor.app.model.ModelLauncher;
import org.ly.appsupervisor.deploy.config.ConfigHelper;
import org.ly.ext.mail.SmtpRequest;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActionController
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final Map<String, ModelLauncher> LAUNCHERS = ConfigHelper.instance().launchers();

    private static final String COMMAND_START = ModelAction.COMMAND_START;
    private static final String COMMAND_STOP = ModelAction.COMMAND_STOP;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Executable> _execs;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ActionController() {
        _execs = new HashMap<>();
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void run(final String uid,
                    final String action_name) throws Exception {
        if (StringUtils.hasText(uid)) {
            this.runAction(LAUNCHERS.get(uid), action_name);
        } else {
            // all launchers
            final Set<String> keys = LAUNCHERS.keySet();
            for (final String key : keys) {
                if (StringUtils.hasText(key)) {
                    this.run(key, action_name);
                }
            }
        }
    }

    public boolean isExecuting(final String uid) {
        return _execs.containsKey(uid) && _execs.get(uid).isAlive();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {


    }

    private void runAction(final ModelLauncher launcher,
                           final String action_name) throws Exception {
        // get rules and check what to do next
        try {
            if (null!=launcher && launcher.enabled() && launcher.actions().containsKey(action_name)) {
                final ModelAction action = launcher.actions().get(action_name);
                // commands
                final String[] action_commands = action.commands();
                for (final String command : action_commands) {
                    if (COMMAND_START.equalsIgnoreCase(command)) {
                        this.cmdStart(launcher);
                    } else if (COMMAND_STOP.equalsIgnoreCase(command)) {
                        this.cmdStop(launcher);
                    }
                }

                // email
                this.sendEmail(action);
            }
        } catch (Exception e) {
            // error starting or stopping program
            throw e;
        }
    }

    private void cmdStart(final ModelLauncher launcher) throws Exception {
        synchronized (this) {
            final String uid = launcher.uid();
            if (!_execs.containsKey(uid)) {
                final String cmd = launcher.exec();
                // START
                final Executable exec = new Executable(cmd)
                        .output(ExecMonitor.instance().outputHandler())
                        .error(ExecMonitor.instance().errorHandler()).run();
                _execs.put(uid, exec);
            }
        }
    }

    private void cmdStop(final ModelLauncher launcher) {
        synchronized (this) {
            final String uid = launcher.uid();
            if (_execs.containsKey(uid)) {
                final Executable exec = _execs.remove(uid);
                exec.interrupt();
            }
        }
    }

    private void sendEmail(final ModelAction action) {
        final String host = action.emailConnectionHost();
        if (StringUtils.hasText(host)) {
            final String user = action.emailConnectionUsername();
            final String password = action.emailConnectionPassword();
            final boolean is_tls = action.emailConnectionIsTls();
            final boolean is_ssl = action.emailConnectionIsSsl();
            final int port = action.emailConnectionPort();
            // create the message
            final String message = action.emailMessage();
            final String[] targets = action.emailTarget();

            //-- send email --//
            // creates the request
            final SmtpRequest request = new SmtpRequest();
            request.host(host);
            request.port(port);
            request.tls(is_tls);
            request.ssl(is_ssl);
            request.user(user);
            request.password(password);
            request.from(user);
            request.replyTo(user);
            request.addresses(targets);
            request.subject("Message from App Supervisor");
            request.bodyText(message);

            request.send();
        }
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ActionController __instance;

    public static ActionController instance() {
        if (null == __instance) {
            __instance = new ActionController();
        }
        return __instance;
    }

}
