package org.ly.ose.client.gui;

import org.ly.ose.client.gui.application.Application;
import org.ly.ose.client.gui.deploy.DeployController;
import org.ly.ose.client.gui.deploy.config.ConfigDeployer;
import org.lyj.Lyj;
import org.lyj.commons.logging.Logger;
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

    private final Logger _logger;

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

        _logger = LoggingUtils.getLogger(this);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void ready() {

        // complete deploy
        DeployController.instance().deploy();

        if (_debug_mode) {
            // wait 5 seconds to allow debugger to connect
            _logger.info("DEBUG MODE ON: waiting 5 seconds to allow debugger to connect....");
            try {
                Thread.sleep(5000);
            } catch (Throwable ignored) {
            }
        }

        if (!_test_mode) {
            try {
                _logger.info("LAUNCHER: CREATING APP SERVER INSTANCE");
                _application = new Application(_test_mode);
                _logger.info("LAUNCHER: STARTING APP SERVER INSTANCE");
                _application.start();
            } catch (Exception e) {
                e.printStackTrace();
                LoggingUtils.getLogger(this).error("Error Initializing App Server", e);
            }
        } else {
            // TEST MODE: ONLY FOR TEST UNIT
            try {
                _application = new Application(_test_mode);
                _application.start();
            } catch (Exception e) {
                e.printStackTrace();
                LoggingUtils.getLogger(this).error("Error Initializing App Server", e);
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
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static Launcher _instance;

    public static void main(final String[] args) {
        _instance = new Launcher(args);
        final String config_path = Lyj.getConfigurationPath();

        Lyj.registerDeployer(new ConfigDeployer(config_path, Lyj.isSilent()));

        _instance.run();
    }

    public static boolean isDebug() {
        return _instance._debug_mode;
    }
}
