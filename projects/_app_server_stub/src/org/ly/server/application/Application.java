package org.ly.server.application;

import org.ly.server.IConstants;
import org.ly.server.application.endpoints.api.ApiServer;
import org.ly.server.application.endpoints.socket.SocketServer;
import org.ly.server.application.endpoints.web.WebServer;
import org.ly.server.application.importer.PackageImporter;
import org.ly.server.application.persistence.DBController;
import org.ly.server.application.programming.ProgramsManager;
import org.ly.server.deploy.config.ConfigHelper;
import org.ly.server.IConstants;
import org.ly.server.application.importer.PackageImporter;
import org.ly.server.deploy.config.ConfigHelper;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.PathUtils;

/**
 * Application Server
 */
public class Application {


    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static final String VERSION = IConstants.APP_VERSION;


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _test_mode;

    private ApiServer _api_server;
    private WebServer _web_server;
    private SocketServer _socket_server;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Application(final boolean test_mode) {

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

            // DATABASE
            this.getLogger().info("APP SERVER: INITIALIZING DATABASE CONTROLLER");
            DBController.instance().open();
            this.getLogger().info("APP SERVER: DATABASE CONTROLLER INITIALIZED");

            // PROGRAMMING (javascript engine)
            this.getLogger().info("APP SERVER: INITIALIZING PROGRAMS CONTROLLER");
            ProgramsManager.instance().open();
            this.getLogger().info("APP SERVER: DATABASE PROGRAMS INITIALIZED");

            // open importer
            this.getLogger().info("APP SERVER: INITIALIZING IMPORTER CONTROLLER");
            PackageImporter.instance().open();
            PackageImporter.instance().force(); // import immediately existing
            this.getLogger().info("APP SERVER: IMPORTER CONTROLLER INITIALIZED");

            if (!_test_mode) {

                // start license manager
                //LicenseController.instance().refresh();

                // start endpoint listeners
                this.startEndpointServices();
            }

        } catch (Throwable t) {
            this.getLogger().error("Error initializing server", t);
        }
    }

    private void finish() {
        // stop endpoint listeners
        this.stopEnpointServices();

        PackageImporter.instance().close();
        ProgramsManager.instance().close();
        DBController.instance().close();
    }


    private void startEndpointServices() {
        // tiny web server
        final boolean api_enabled = ConfigHelper.instance().apiEnabled();
        this.getLogger().info("APP SERVER: API SERVER IS " + (api_enabled ? "ENABLED" : "DISABLED"));
        if (api_enabled) {
            _api_server = new ApiServer().start();
        }

        final boolean socket_enabled = ConfigHelper.instance().socketEnabled();
        this.getLogger().info("APP SERVER: SOCKET SERVER IS " + (socket_enabled ? "ENABLED" : "DISABLED"));
        if (socket_enabled) {
            _socket_server = new SocketServer().start();
        }

        final boolean web_enabled = ConfigHelper.instance().webEnabled();
        this.getLogger().info("APP SERVER: WEB SERVER IS " + (web_enabled ? "ENABLED" : "DISABLED"));
        if (web_enabled) {
            _web_server = new WebServer().start();
        }
    }

    private void stopEnpointServices() {
        if (null != _web_server) {
            _web_server.stop();
        }
        if (null != _api_server) {
            _api_server.stop();
        }
        if (null != _socket_server) {
            _socket_server.stop();
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
        sb.append("\t").append("API UPLOAD: ").append(config.apiHost() + "/upload").append("\n");

        // logs
        sb.append("\t").append("LOGS: ").append("\n");
        sb.append("\t\t").append(config.apiHost()).append("/api/util/log/" + IConstants.APP_TOKEN_CLIENT_API + "/lyj.log").append("\n");
        sb.append("\t\t").append(config.apiHost()).append("/api/util/log/" + IConstants.APP_TOKEN_CLIENT_API + "/").append(PackageImporter.LOGGER_NAME).append("\n");

        sb.append("**************************************");

        this.getLogger().info(sb.toString());
    }

}
