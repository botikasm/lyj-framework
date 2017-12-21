package org.lyj.commons.event.bus;

import org.lyj.commons.event.Event;
import org.lyj.commons.event.bus.utils.MessageBusEvents;
import org.lyj.commons.event.bus.utils.MessageBusListeners;
import org.lyj.commons.util.RandomUtils;

/**
 * Event container.
 * <p>
 * To add an event to event bus call MessageBus.emit().
 * <p>
 * To listen events from MessageBus, create a listener calling MessageBus.createListener();
 * <p>
 * The MessageBus allow to implement the event pattern with no danger for crossed references and memory leak.
 * Event emitters and event listeners are decoupled and never referenced each other.
 * <p>
 * MessageBus can be used as a singleton object or creating new instances.
 * Each event bus has an internal thread that works as a garbage collector.
 */
public class MessageBus {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int DEF_EVENT_TIMEOUT = 5 * 1000;  // default timeout
    private static final int DEF_GC_INTERVAL = 2 * 1000;
    private static final int DEF_LISTEN_INTERVAL = 500;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MessageBusEvents _events;
    private final String _id;
    private final MessageBusListeners _listeners;

    private boolean _disposed;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageBus() {
        this(DEF_EVENT_TIMEOUT, DEF_GC_INTERVAL, DEF_LISTEN_INTERVAL);
    }

    public MessageBus(final int eventTimeout, final int gcInterval, final int listenInterval) {
        _id = RandomUtils.randomUUID();
        _disposed = false;
        _listeners = new MessageBusListeners(this, listenInterval);
        _events = new MessageBusEvents(this, eventTimeout, gcInterval);
    }

    @Override
    public void finalize() throws Throwable {
        try {
            _disposed = true;
            _events.clear();
            _listeners.clear();
        } catch (Throwable ignored) {
            // nothing useful to do here
        } finally {
            super.finalize();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" + _id + "] " +
                _events.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isDisposed() {
        return _disposed;
    }

    public MessageBusListeners listeners() {
        return _listeners;
    }

    public MessageBusEvents events() {
        return _events;
    }

    public MessageBus emit(final Event event) {
        _events.add(event);
        return this;
    }

    public void clearAll() {
        _listeners.clear();
        _events.clear();
    }

    //-- factory --//

    public MessageListener createListener() {
        // create listener
        final MessageListener listener = new MessageListener();
        return _listeners.add(listener);
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(){

    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    //-- singleton --//

    private static MessageBus __instance;

    public static synchronized MessageBus getInstance() {
        if (null == __instance) {
            __instance = new MessageBus();
        }
        return __instance;
    }


}
