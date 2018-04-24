package org.ly.licensemanager.app;

import org.ly.licensemanager.IConstants;
import org.ly.licensemanager.app.controllers.license.LicenseController;
import org.ly.licensemanager.app.server.listeners.api.ApiServer;
import org.ly.licensemanager.deploy.config.ConfigHelper;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.PathUtils;

/**
 * Application Server
 */
public class ApplicationServer {


    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static final String VERSION = IConstants.APP_VERSION;


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _test_mode;
    private ApiServer _api_server;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ApplicationServer(final boolean test_mode) {

        LocaleUtils.setCurrent(IConstants.LOCALE);

        _test_mode = test_mode;

        this.getLogger().info("STARTING SERVER VERSION ".concat(VERSION));
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.stop();
        } finally {
            super.finalize();
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public void start() throws Exception {
        this.getLogger().info("APP SERVER: INITIALIZING...");
        this.init();
        this.logConfiguration();
    }

    public void stop() {
        this.getLogger().info("APP SERVER: FINALIZING...");
        this.finish();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() {
        try {
            //-- init all services here --//

            if (!_test_mode) {

                // start license manager
                LicenseController.instance().refresh();

                final boolean api_enabled = ConfigHelper.instance().apiEnabled();
                this.getLogger().info("APP SERVER: API SERVER IS " + (api_enabled ? "ENABLED" : "DISABLED"));
                if (api_enabled) {
                    _api_server = new ApiServer();
                    _api_server.start();
                }

                /*
                final boolean web_enabled = ConfigHelper.instance().webEnabled();
                this.getLogger().info("APP SERVER: WEB SERVER IS " + (web_enabled ? "ENABLED" : "DISABLED"));
                if (web_enabled) {
                    _web_server = new WebServer().start();
                }
                 */
            }

        } catch (Throwable t) {
            this.getLogger().error("Error initializing server", t);
        }
    }

    private void finish() {
        /*
        if (null != _web_server) {
            _web_server.stop();
        }*/
        if (null != _api_server) {
            _api_server.stop();
        }

    }


    private void logConfiguration() {
        final ConfigHelper config = ConfigHelper.instance();

        final StringBuilder sb = new StringBuilder();
        sb.append("CONFIGURATION:\n");
        sb.append("**************************************").append("\n");
        sb.append("\t").append("VERSION: ").append(IConstants.APP_VERSION).append("\n");
        // sb.append("\t").append("DB SCHEMA: ").append(IConstants.DB_VERSION).append("\n");
        sb.append("\t").append("ROOT: ").append(PathUtils.getAbsolutePath("")).append("\n");
        sb.append("\t").append("API PORT: ").append(config.apiEnabled() ? config.apiPort() : "Not Enabled").append("\n");
        sb.append("\t").append("API HOST: ").append(config.apiEnabled() ? config.apiHost() : "Not Enabled").append("\n");
        sb.append("\t").append("API TEST: ").append(config.apiEnabled()
                ? config.apiHost() + "/api/version"
                : "Not Enabled").append("\n");
        sb.append("**************************************");

        this.getLogger().info(sb.toString());
    }

}
