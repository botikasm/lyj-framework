package org.lyj.desktopgap.app.bus;

import org.lyj.commons.event.Event;
import org.lyj.commons.event.bus.MessageBus;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.desktopgap.app.IConstants;
import org.lyj.desktopgap.app.controllers.DataController;

/**
 * Main message listener
 */
public class GlobalMessageListener
        extends AbstractLogEmitter
        implements IEvents, IConstants {

    private org.lyj.commons.event.bus.MessageListener _global_listener;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private GlobalMessageListener() {
        _global_listener = MessageBus.getInstance()
                .createListener();
        _global_listener.on(this::handle);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void stop() {
        _global_listener.clear();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handle(final Event event) {
        final String tag = event.getTag();
        if (tag.equals(ETAG_CONNECTION)) {
            this.onConnection(event);
        }
    }

    // ------------------------------------------------------------------------
    //                      connection
    // ------------------------------------------------------------------------

    private void onConnection(final Event event) {
        final String name = event.getName();
        if (name.equals(ENAME_CONNECTION)) {
            final boolean connected = ConversionUtils.toBoolean(event.getData());
            final boolean old_connected = DataController.instance().getConnected();
            DataController.instance().setConnected(connected);

            // changed connection state?
            if(connected != old_connected) {
                // changed
                super.debug("onConnection", connected+"");


            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static GlobalMessageListener __instance;

    public static GlobalMessageListener instance() {
        if (null == __instance) {
            __instance = new GlobalMessageListener();
        }
        return __instance;
    }

    public static void init() {
        instance();
    }


}
