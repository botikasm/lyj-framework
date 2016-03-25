package org.lyj.desktopfences.app.bus;

import org.lyj.commons.event.Event;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.desktopfences.app.DesktopFences;
import org.lyj.desktopgap.app.bus.GlobalMessageListener;

/**
 * Main message listener.
 *
 */
public class SystemMessageListener
        extends AbstractLogEmitter {

    private static final String TAG_SYSTEM = org.lyj.desktopgap.app.bus.IEvents.ETAG_SYSTEM;
    private static final String EVENT_QUIT = org.lyj.desktopgap.app.bus.IEvents.ENAME_QUIT;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private SystemMessageListener() {
        GlobalMessageListener.instance().handler(this::handle);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handle(final Event event) {
        final String tag = event.getTag();
        if(TAG_SYSTEM.equals(tag)){
            this.onSystem(event);
        }

    }

    // ------------------------------------------------------------------------
    //                      system
    // ------------------------------------------------------------------------

    private void onSystem(final Event event){
        if(EVENT_QUIT.equals(event.getName())){
            DesktopFences.instance().stop();

        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static SystemMessageListener __instance;

    public static SystemMessageListener init() {
        if (null == __instance) {
            __instance = new SystemMessageListener();
        }
        return __instance;
    }



}
