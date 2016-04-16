package org.lyj.desktopfences.app;

import org.lyj.desktopfences.app.bus.SystemMessageListener;
import org.lyj.desktopfences.app.client.ApiController;
import org.lyj.desktopfences.app.controllers.DesktopController;
import org.lyj.desktopfences.app.controllers.archive.ArchiveController;
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
    private final DesktopFencesSettings _settings;

    // ------------------------------------------------------------------------
    //                      constructor
    // ------------------------------------------------------------------------

    private DesktopFences() {
        _settings = new DesktopFencesSettings();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public DesktopFencesSettings settings() {
        return _settings;
    }

    public DesktopFences start() {
        //-- add custom client API --//
        ApiController.install(_router);

        //-- init system listener --//
        SystemMessageListener.init();

        //-- init Archive controller --//
        ArchiveController.instance();

        //-- Start Desktop controller with scan for existing files --//
        DesktopController.instance().scan(true);

        return this;
    }

    public DesktopFences stop() {
        //-- Stop Desktop controller --//
        DesktopController.instance().close();

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
