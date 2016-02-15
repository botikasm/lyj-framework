package org.lyj.commons.event.bus;

import org.lyj.commons.async.future.Loop;
import org.lyj.commons.event.Event;
import org.lyj.commons.event.bus.emitter.MessageBusEvents;
import org.lyj.commons.event.bus.emitter.MessageBusGC;
import org.lyj.commons.event.bus.emitter.MessageBusListeners;
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

    private final MessageBusGC _gc;
    private final MessageBusEvents _events;
    private final String _id;
    private final MessageBusListeners _listeners;
    private final Loop _loop;

    private boolean _disposed;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageBus() {
        this(DEF_EVENT_TIMEOUT, DEF_INTERVAL);
    }

    public MessageBus(final int eventTimeout, final int gcInterval) {
        _events = new MessageBusEvents(eventTimeout);
        _gc = new MessageBusGC(this, gcInterval);
        _id = RandomUtils.randomUUID();
        _disposed = false;
        _listeners = new MessageBusListeners();
        _loop = new Loop(100, (int) (gcInterval * 0.5));
    }

    @Override
    public void finalize() throws Throwable {
        try {
            _disposed = true;
            _gc.stop(true);
            _events.clear();
            _listeners.clear();
            _loop.interrupt();
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

    public int size() {
        return _events.size();
    }

    public MessageBusEvents events() {
        return _events;
    }

    public MessageBus emit(final Event event) {
        if (!_gc.isRunning()) {
            _gc.start();
        }
        _events.add(event);
        return this;
    }

    //-- factory --//

    public MessageListener createListener() {
        synchronized (_listeners){
            // start if not running
            this.startLoopEvents();
            // create listener
            final MessageListener listener = new MessageListener();
            return _listeners.add(listener);
        }
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    /*
    Event[] listen(final String listenerId, final Set<String> tags, final String name) {
        return _events.listen(listenerId, tags, name);
    }
    */

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void startLoopEvents() {
        if (!_loop.isRunning()) {
            _loop.start((interruptor) -> {
                interruptor.pause();
                _listeners.process(_events);
                interruptor.resume();
            });
        }
    }

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
