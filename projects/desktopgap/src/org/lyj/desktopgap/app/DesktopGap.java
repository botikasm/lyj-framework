package org.lyj.desktopgap.app;

import org.lyj.desktopgap.app.bus.GlobalMessageListener;
import org.lyj.desktopgap.app.scheduledtask.ScheduledConnectionMonitor;
import org.lyj.desktopgap.app.server.WebServer;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.controllers.routing.IRouter;

/**
 * Main Application Controller
 */
public class DesktopGap {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private WebServer _web_server;
    private DesktopGapSettings _settings; // contains read/write settings

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DesktopGap() {
        // internal web server
        _web_server = new WebServer();
        _settings = new DesktopGapSettings();

        // global listener
        GlobalMessageListener.init();

        // start tasks
        ScheduledConnectionMonitor.start();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void start() {
        _web_server.start();
        _settings.uriHome(_web_server.uri(""));
    }

    public void stop() {
        _web_server.stop();

        //-- close internal global listener --//
        GlobalMessageListener.instance().stop();

        //-- close scheduled tasks--//
        ScheduledConnectionMonitor.stop();
    }

    public DesktopGapSettings settings() {
        return _settings;
    }

    public String httDocsPath(final String path) {
        return _web_server.path(path);
    }

    public String webUri(final String path) {
        return _web_server.uri(path);
    }

    public IRouter router() {
        if (null != _web_server) {
            return _web_server.router();
        }
        return null;
    }

    public HttpServerConfig webConfig() {
        if (null != _web_server) {
            return _web_server.config();
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static DesktopGap __instance;

    public static DesktopGap instance() {
        if (null == __instance) {
            __instance = new DesktopGap();
        }
        return __instance;
    }

}
