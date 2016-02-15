package org.lyj.commons.event.bus.utils;

import org.lyj.commons.async.future.Timed;
import org.lyj.commons.event.Event;
import org.lyj.commons.event.IEventListener;
import org.lyj.commons.event.bus.MessageBus;
import org.lyj.commons.event.bus.MessageListener;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Container for all listeners
 */
public class MessageBusListeners {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MessageBus _bus;
    private final int _interval;
    private final List<MessageListener> _listeners;
    private final ListenersTask _task;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageBusListeners(final MessageBus bus, final int interval) {
        _bus = bus;
        _interval = interval;
        _listeners = new ArrayList<>();
        _task = new ListenersTask(this, interval);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int size() {
        return _listeners.size();
    }

    public void clear() {
        synchronized (_listeners) {
            _listeners.clear();
        }
    }

    public void stop(){
        _task.stop(true);
    }

    public MessageListener add(final MessageListener listener) {
        synchronized (_listeners) {
            if (!_task.isRunning()) {
                _task.start();
            }
            if (!_listeners.contains(listener)) {
                _listeners.add(listener);
            }
            return listener;
        }
    }

    public MessageListener remove(final MessageListener listener) {
        synchronized (_listeners) {
            if (!_listeners.contains(listener)) {
                _listeners.remove(listener);
            }
            return listener;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void process(final MessageBusEvents eventBus) {
        synchronized (_listeners) {
            final List<MessageListener> remove_list = new ArrayList<>();

            for (final MessageListener listener : _listeners) {
                final String id = listener.getId();
                final String name = listener.getEventName();
                final String[] tags = listener.getEventTags();
                final Event[] events = eventBus.listen(id, tags, name);
                if (events.length > 0) {
                    if (listener.isEmpty()) {
                        // remove
                        remove_list.add(listener);
                    } else {
                        final IEventListener[] executors = listener.listeners();
                        if (executors.length > 0) {
                            for (final Event event : events) {
                                for (final IEventListener executor : executors) {
                                    try {
                                        executor.on(event);
                                    } catch (Throwable t) {
                                        remove_list.add(listener); // mark as remove
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // remove dead listeners
            if (remove_list.size() > 0) {
                for (final MessageListener listener : remove_list) {
                    _listeners.remove(listener);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    private static class ListenersTask
            extends Timed {


        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private final MessageBusListeners _listeners;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public ListenersTask(final MessageBusListeners listeners, final int interval) {
            super(TimeUnit.MILLISECONDS, 0, interval, 0, 0);
            _listeners = listeners;
        }

        @Override
        public void finalize() throws Throwable {
            try {

            } finally {
                super.finalize();
            }
        }

        public void start() {
            super.start((t) -> {
                try {
                    if(null!=_listeners && _listeners.size()>0){
                        this.run();
                    } else {
                        t.stop();
                    }
                } catch (Throwable err) {
                    super.error("run", FormatUtils.format("Error running garbage collector for EventBus: %s",
                            ExceptionUtils.getMessage(err)));
                }
            });
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private void run() {
            if (null != _listeners) {
                try {
                    _listeners.process(_listeners._bus.events());
                }catch(Throwable ignored){
                }
            }
        }


    }

}
