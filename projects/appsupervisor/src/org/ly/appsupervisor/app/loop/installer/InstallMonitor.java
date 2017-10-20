package org.ly.appsupervisor.app.loop.installer;

import org.ly.appsupervisor.app.loop.installer.controllers.FileController;
import org.ly.appsupervisor.app.loop.installer.controllers.PackageController;
import org.lyj.commons.Delegates;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;

import java.io.File;
import java.util.Collection;

public class InstallMonitor
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private Delegates.Callback<String> _callback_out;
    private Delegates.Callback<String> _callback_error;

    private final FileController _watchdog;
    private final PackageController _package;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    private InstallMonitor() {
        _watchdog = new FileController();
        _package = new PackageController();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void outputHandler(final Delegates.Callback<String> callback) {
        _callback_out = callback;
    }

    public Delegates.Callback<String> outputHandler() {
        return _callback_out;
    }

    public void errorHandler(final Delegates.Callback<String> callback) {
        _callback_error = callback;
    }

    public Delegates.Callback<String> errorHandler() {
        return _callback_error;
    }

    public void monitor() {
        this.monitor(null, null);
    }

    public int monitor(final Delegates.Callback<String> output,
                       final Delegates.Callback<String> error) {
        try {
            this.outputHandler(output);
            this.errorHandler(error);

            // check files
            return this.checkFiles();
        } catch (final Exception exec_error) {
            super.error("monitor", exec_error);
        }
        return 0;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private int checkFiles() throws Exception {
        final Collection<File> files = _watchdog.check();
        final Counter counter = new Counter();
        if (!files.isEmpty()) {
            // ready to install something
            for (final File file : files) {
                if (_package.install(file)) {
                    counter.inc();
                    // remove installed
                    FileUtils.delete(file.getAbsolutePath());
                }
            }
        }
        return counter.valueAsInt();
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static InstallMonitor __instance;

    public static InstallMonitor instance() {
        if (null == __instance) {
            __instance = new InstallMonitor();
        }
        return __instance;
    }

}
