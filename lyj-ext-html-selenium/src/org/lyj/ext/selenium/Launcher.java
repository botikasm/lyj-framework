package org.lyj.ext.selenium;

import org.lyj.Lyj;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.ext.selenium.controllers.SeleniumManager;
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
    private SeleniumManager _app_server;

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
                _app_server = SeleniumManager.instance();
                _logger.info("LAUNCHER: STARTING APP SERVER INSTANCE");
                _app_server.open();
            } catch (Exception e) {
                e.printStackTrace();
                LoggingUtils.getLogger(this).error("Error Initializing App Server", e);
            }
        } else {
            // TEST MODE: ONLY FOR TEST UNIT
            try {
                _app_server = SeleniumManager.instance();
                _app_server.open();
            } catch (Exception e) {
                e.printStackTrace();
                LoggingUtils.getLogger(this).error("Error Initializing App Server", e);
            }
        }
    }

    @Override
    public void shutdown() {
        if (null != _app_server) {
            _app_server.close();
        }
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static Launcher _instance;

    public static void main(final String[] args) {
        _instance = new Launcher(args);
        final String config_path = Lyj.getConfigurationPath();


        _instance.run();
    }

    public static boolean isDebug() {
        return _instance._debug_mode;
    }


}
