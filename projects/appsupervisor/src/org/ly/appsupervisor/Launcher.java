package org.ly.appsupervisor;

import org.ly.appsupervisor.app.Application;
import org.ly.appsupervisor.deploy.config.Deployer;
import org.lyj.Lyj;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.launcher.LyjLauncher;

/**
 * Main server class.
 */
public class Launcher
        extends LyjLauncher {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _test_mode;
    private boolean _debug_mode;
    private Application _application;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Launcher(final String[] args) {
        // run Lyj app framework
        super(args);
        _test_mode = super.getArgBoolean("t");
        _debug_mode = super.getArgBoolean("d");

        LoggingRepository.getInstance().setArchiveExisting(!_test_mode && !_debug_mode);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void ready() {

        if (_debug_mode) {
            // wait 5 seconds to allow debugger to connect
            LoggingUtils.getLogger(this).info("DEBUG MODE ON: waiting 5 seconds to allow debugger to connect....");
            try {
                Thread.sleep(5000);
            } catch (Throwable ignored) {
            }
        }

        if (!_test_mode) {
            try {
                // run
                _application = new Application(_test_mode);
                _application.start();
            } catch (Exception e) {
                e.printStackTrace();
                LoggingUtils.getLogger(this).error("Error Initializing Application", e);
            }
        } else {
            // TEST MODE: ONLY FOR TEST UNIT
            try {
                //_application = new Application(_test_mode);
                //_application.start();
            } catch (Exception e) {
                e.printStackTrace();
                LoggingUtils.getLogger(this).error("Error Initializing Application", e);
            }
        }
    }

    @Override
    public void shutdown() {
        if (null != _application) {
            _application.stop();
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static Launcher _instance;

    public static void main(final String[] args) {
        _instance = new Launcher(args);
        final String config_path = Lyj.getConfigurationPath();

        Lyj.registerDeployer(new Deployer(config_path, Lyj.isSilent()));

        _instance.run();
    }

    public static boolean isDebug() {
        return _instance._debug_mode;
    }
}
