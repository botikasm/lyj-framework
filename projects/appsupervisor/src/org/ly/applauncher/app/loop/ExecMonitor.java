package org.ly.applauncher.app.loop;

import org.ly.applauncher.deploy.config.ConfigHelper;
import org.lyj.commons.logging.AbstractLogEmitter;

import java.io.IOException;

public class ExecMonitor
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String CMD = ConfigHelper.instance().launcherExec();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private Executable _exec;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    private ExecMonitor() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void monitor() {
        try {
            if (null == _exec) {
                _exec = new Executable(CMD).run();
            } else {

            }
        } catch (final IOException exec_error) {
            super.error("monitor", exec_error);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ExecMonitor __instance;

    public static ExecMonitor instance() {
        if (null == __instance) {
            __instance = new ExecMonitor();
        }
        return __instance;
    }

}
