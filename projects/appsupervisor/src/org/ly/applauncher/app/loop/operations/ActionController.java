package org.ly.applauncher.app.loop.operations;

import org.ly.applauncher.app.loop.ExecMonitor;
import org.ly.applauncher.app.loop.Executable;
import org.ly.applauncher.app.model.Action;
import org.ly.applauncher.deploy.config.ConfigHelper;

import java.io.IOException;

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

    private Executable _exec;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ActionController() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void run(final Action action) {
        // get rules and check what to do next
        try {
            final String[] commands = action.commands();
            for (final String command : commands) {
                if (COMMAND_START.equalsIgnoreCase(command)) {
                    this.start(action);
                } else if (COMMAND_STOP.equalsIgnoreCase(command)) {
                    this.stop(action);
                }
            }
        } catch (Throwable t) {
            // error starting or stopping program

        }
    }

    private void start(final Action action) throws IOException {
        if (null == _exec) {

            // START
            _exec = new Executable(CMD)
                    .output(ExecMonitor.instance().outputHandler())
                    .error(ExecMonitor.instance().errorHandler()).run();
        }
    }

    private void stop(final Action action) {
        if (null != _exec) {

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
