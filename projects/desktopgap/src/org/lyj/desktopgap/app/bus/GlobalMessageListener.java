package org.lyj.desktopgap.app.bus;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.event.Event;
import org.lyj.commons.event.bus.MessageBus;
import org.lyj.commons.event.bus.MessageListener;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.desktopgap.app.IConstants;
import org.lyj.desktopgap.app.controllers.DataController;

import java.util.ArrayList;
import java.util.List;

/**
 * Main message listener
 */
public class GlobalMessageListener
        extends AbstractLogEmitter
        implements IEvents, IConstants {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MessageListener _global_listener;
    private final List<Delegates.Callback<Event>> _external_listeners;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private GlobalMessageListener() {
        _global_listener = MessageBus.getInstance()
                .createListener();
        _global_listener.on(this::handle);
        _external_listeners = new ArrayList<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void stop() {
        _global_listener.clear();

        // handle close for external
        this.handleExternal(Event.create(this, ENAME_QUIT).setTag(ETAG_SYSTEM));
        _external_listeners.clear();
    }

    public GlobalMessageListener handler(final Delegates.Callback<Event> handler) {
        _external_listeners.add(handler);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handle(final Event event) {
        this.handleExternalAsync(event);
        final String tag = event.getTag();
        if (tag.equals(ETAG_CONNECTION)) {
            this.onConnection(event);
        }
    }

    private void handleExternalAsync(final Event event) {
        Async.invoke((args) -> {
            this.handleExternal(event);
        });
    }

    private void handleExternal(final Event event) {
        for (final Delegates.Callback<Event> callback : _external_listeners) {
            try {
                Delegates.invoke(callback, event);
            } catch (Throwable ignored) {
            }
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
            if (connected != old_connected) {
                // changed
                super.debug("onConnection", connected + "");


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
