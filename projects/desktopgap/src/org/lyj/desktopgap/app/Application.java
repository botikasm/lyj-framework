package org.lyj.desktopgap.app;

import org.lyj.desktopgap.app.bus.GlobalMessageListener;
import org.lyj.desktopgap.app.scheduledtask.ScheduledConnectionMonitor;
import org.lyj.desktopgap.app.server.WebServer;

/**
 * Main Application Controller
 */
public class Application {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private WebServer _web_server;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private Application() {
        // internal web server
        _web_server = new WebServer();

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
    }

    public void stop() {
        _web_server.stop();

        GlobalMessageListener.instance().stop();

        ScheduledConnectionMonitor.stop();
    }

    public String httDocsPath(final String path) {
        return _web_server.path(path);
    }

    public String webUri(final String path) {
        return _web_server.uri(path);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static Application __instance;

    public static Application instance() {
        if (null == __instance) {
            __instance = new Application();
        }
        return __instance;
    }

}
