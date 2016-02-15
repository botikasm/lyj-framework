package org.lyj.commons.event.bus;

import org.lyj.commons.event.Event;
import org.lyj.commons.event.bus.utils.MessageBusEvents;
import org.lyj.commons.event.bus.utils.MessageBusListeners;
import org.lyj.commons.util.RandomUtils;

/**
 * Event container.
 * <p>
 * To add an event to event bus call EventBus.emit().
 * <p>
 * To listen events from EventBus, create a listener calling EventBus.createListener();
 * <p>
 * The EventBus allow to implement the event pattern with no danger for crossed references and memory leak.
 * Event emitters and event listeners are decoupled and never referenced each other.
 * <p>
 * EventBus can be used as a singleton object or creating new instances.
 * Each event bus has an internal thread that works as a garbage collector.
 */
public class MessageBus {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int DEF_EVENT_TIMEOUT = 2 * 1000;
    private static final int DEF_INTERVAL = 2 * 1000;

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
        this(DEF_EVENT_TIMEOUT, DEF_INTERVAL);
    }

    public MessageBus(final int eventTimeout, final int gcInterval) {
        _id = RandomUtils.randomUUID();
        _disposed = false;
        _listeners = new MessageBusListeners(this, (int) (gcInterval * 0.5));
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

    //-- factory --//

    public MessageListener createListener() {
        synchronized (_listeners) {
            // create listener
            final MessageListener listener = new MessageListener();
            return _listeners.add(listener);
        }
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    //-- singleton --//

    private static MessageBus __instance;

    public static MessageBus getInstance() {
        if (null == __instance) {
            __instance = new MessageBus();
        }
        return __instance;
    }


}
