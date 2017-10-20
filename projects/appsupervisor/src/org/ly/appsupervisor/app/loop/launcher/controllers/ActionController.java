package org.ly.appsupervisor.app.loop.launcher.controllers;

import org.json.JSONObject;
import org.ly.appsupervisor.app.loop.launcher.ExecMonitor;
import org.ly.appsupervisor.app.loop.launcher.exec.Executable;
import org.ly.appsupervisor.app.model.Action;
import org.ly.appsupervisor.deploy.config.ConfigHelper;
import org.ly.ext.mail.SmtpRequest;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActionController {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String CMD = ConfigHelper.instance().launcherExec();

    private static final String COMMAND_START = Action.COMMAND_START;
    private static final String COMMAND_STOP = Action.COMMAND_STOP;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    private final Map<String, Action> _actions;

    private Executable _exec;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ActionController() {
        _actions = new HashMap<>();
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void run(final String action) throws Exception {
        if (_actions.containsKey(action)) {
            this.run(_actions.get(action));
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // init actions
        final JSONObject actions = ConfigHelper.instance().actions();
        final Set<String> keys = actions.keySet();
        for (final String key : keys) {
            final Action action = new Action(actions.optJSONObject(key));
            if (action.commands().length > 0) {
                _actions.put(key, action);
            }
        }
    }

    private void run(final Action action) throws Exception {
        // get rules and check what to do next
        try {
            // commands
            final String[] commands = action.commands();
            for (final String command : commands) {
                if (COMMAND_START.equalsIgnoreCase(command)) {
                    this.start(action);
                } else if (COMMAND_STOP.equalsIgnoreCase(command)) {
                    this.stop(action);
                }
            }

            // email
            this.sendEmail(action);
        } catch (Exception e) {
            // error starting or stopping program
            throw e;
        }
    }

    private void start(final Action action) throws Exception {
        if (null == _exec) {
            // START
            _exec = new Executable(CMD)
                    .output(ExecMonitor.instance().outputHandler())
                    .error(ExecMonitor.instance().errorHandler()).run();
        }
    }

    private void stop(final Action action) {
        if (null != _exec) {
            _exec.interrupt();
            if (!_exec.isAlive()) {
                _exec = null;
            }
        }
    }

    private void sendEmail(final Action action) {
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
