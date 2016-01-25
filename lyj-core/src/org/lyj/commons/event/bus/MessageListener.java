package org.lyj.commons.event.bus;

import org.lyj.commons.event.Event;
import org.lyj.commons.event.EventListeners;
import org.lyj.commons.event.IEventListener;
import org.lyj.commons.event.bus.utils.MessageListenerTask;
import org.lyj.commons.util.RandomUtils;

/**
 * Listen events from EventBus.
 * <p>
 * To create an EventListener use EventBus.createListener() method.
 */
public class MessageListener {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MessageBus _bus;

    private final String _id;
    private final EventListeners _listeners;
    private final MessageListenerTask _task;

    private String _event_tag;
    private String _event_name;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    MessageListener(final MessageBus bus, final int interval) {
        _bus = bus;
        _id = RandomUtils.randomUUID();
        _listeners = new EventListeners();
        _task = new MessageListenerTask(interval);
    }

    @Override
    public void finalize() throws Throwable {
        try {
            this.clear();
        } finally {
            super.finalize();
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Listener unique identifier
     * @return Unique Identifier
     */
    public String getId() {
        return _id;
    }

    private String getEventTag() {
        return _event_tag;
    }

    /**
     * Set filter for events. Tag Filter
     * @param value The Event Tag to filter
     * @return this
     */
    public MessageListener setEventTag(final String value) {
        _event_tag = value;
        return this;
    }

    public String getEventName() {
        return _event_name;
    }

    /**
     * Set filter for events. Name Filter
     * @param value The Event Name to filter
     * @return this
     */
    public MessageListener setEventName(final String value) {
        _event_name = value;
        return this;
    }

    /**
     * Clear all listeners and stop internat thread.
     * Internal thread run again when new listeners are added to queue.
     */
    public void clear() {
        synchronized (_listeners) {
            _listeners.clear();
            _task.stop(true);
        }
    }

    /**
     * Add a callback listener to internal queue.
     * @param callback The listener implementing Handle interface.
     */
    public void on(final IEventListener callback) {
        synchronized (_listeners) {
            this.waikeupTask();
            _listeners.add(callback);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void waikeupTask() {
        if (!_bus.isDisposed() && !_task.isRunning()) {
            _task.start((interruptor) -> {
                if (!_bus.isDisposed()) {
                    final Event[] events = _bus.listen(_id, _event_tag, _event_name);
                    final IEventListener[] listeners = _listeners.toArray();
                    for (final Event event : events) {
                        for (final IEventListener listener : listeners) {
                            try {
                                listener.on(event);
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                } else {
                    interruptor.stop();
                }
            });
        }
    }

}
