package org.lyj.desktopfences.app;

import org.lyj.desktopfences.app.bus.SystemMessageListener;
import org.lyj.desktopfences.app.client.ApiController;
import org.lyj.desktopfences.app.scheduledtasks.DesktopMonitor;
import org.lyj.ext.netty.server.web.controllers.routing.IRouter;

/**
 * Application Controller
 */
public class DesktopFences {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _test_mode;
    private IRouter _router;

    // ------------------------------------------------------------------------
    //                      constructor
    // ------------------------------------------------------------------------

    private DesktopFences() {
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public DesktopFences start() {
        //-- add custom client API --//
        ApiController.install(_router);

        //-- init system listener --//
        SystemMessageListener.init();

        //-- Start Task --//
        DesktopMonitor.start();

        return this;
    }

    public DesktopFences stop(){
        //-- Stop Tasks --//
        DesktopMonitor.stop();

        return this;
    }

    public boolean isTestMode() {
        return _test_mode;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private DesktopFences router(final IRouter value) {
        _router = value;
        return this;
    }

    private DesktopFences testMode(final boolean value) {
        _test_mode = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      STATIC
    // ------------------------------------------------------------------------

    private static DesktopFences __instance;

    public static DesktopFences instance() {
        if (null == __instance) {
            __instance = new DesktopFences();
        }
        return __instance;
    }

    public static DesktopFences init(final IRouter router, final boolean testMode) {
        return instance().router(router).testMode(testMode);
    }

}
