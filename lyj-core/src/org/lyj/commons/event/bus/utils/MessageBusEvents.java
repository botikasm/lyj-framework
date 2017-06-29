package org.lyj.commons.event.bus.utils;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.future.Timed;
import org.lyj.commons.event.Event;
import org.lyj.commons.event.bus.MessageBus;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Event bus container
 */
public class MessageBusEvents {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MessageBus _bus;
    private final List<MessageBusEventWrapper> _events;
    private final int _timeout;

    private final EventsGC _gc;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageBusEvents(final MessageBus bus, final int timeout, final int gcInterval) {
        _bus = bus;
        _events = new ArrayList<>();
        _timeout = timeout;
        _gc = new EventsGC(this, gcInterval);
    }

    @Override
    public void finalize() throws Throwable {
        try {
            _gc.stop(true);
            _events.clear();
        } finally {
            super.finalize();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("timeout: ").append(_timeout).append(", ");
        sb.append("events: [");
        for (int i = 0; i < _events.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(_events.get(i).event().toString());
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int size() {
        return _events.size();
    }

    public MessageBusEvents clear() {
        synchronized (_events) {
            _events.clear();
        }
        return this;
    }

    public void stop() {
        _gc.stop(true);
    }

    public MessageBusEvents add(final Event event) {
        return this.add(event, _timeout);
    }

    public MessageBusEvents add(final Event event, final int timeout) {
        synchronized (_events) {
            _events.add(new MessageBusEventWrapper(event, timeout));
            if (!_gc.isRunning()) {
                _gc.start();
            }
        }
        return this;
    }

    public MessageBusEventWrapper[] listen(final String listenerId, final Set<String> tags, final String name) {
        return this.listen(listenerId, null != tags ? tags.toArray(new String[tags.size()]) : new String[0], name);
    }

    public MessageBusEventWrapper[] listen(final String listenerId, final String[] tags, final String name) {
        synchronized (_events) {
            final List<MessageBusEventWrapper> response = new ArrayList<>();
            for (final MessageBusEventWrapper event_wrapper : _events) {
                if (CollectionUtils.isEmpty(tags)) {
                    // no tag filter
                    if (this.match(event_wrapper, listenerId, null, name)) {
                        response.add(event_wrapper.setListened(listenerId));
                    }
                } else {
                    // tag filter
                    for (final String tag : tags) {
                        if (this.match(event_wrapper, listenerId, tag, name)) {
                            response.add(event_wrapper.setListened(listenerId));
                        }
                    }
                }
            }

            return response.toArray(new MessageBusEventWrapper[response.size()]);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean match(final MessageBusEventWrapper item, final String listenerId, final String tag, final String name) {
        return !item.isListened(listenerId) && item.match(tag, name);
    }

    private void reject(final Delegates.IterationCallbackBool<MessageBusEventWrapper> callback) {
        synchronized (_events) {
            if (null != callback) {
                int count = _events.size();
                for (int i = count - 1; i > -1; i--) {
                    final MessageBusEventWrapper event = _events.get(i);
                    if (callback.handle(event, i, event.event().getName())) {
                        // remove
                        _events.remove(i);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------


    private static class EventsGC
            extends Timed {


        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private MessageBusEvents _events;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public EventsGC(final MessageBusEvents events, final int interval) {
            super(TimeUnit.MILLISECONDS, 0, interval, 0, 0);

            _events = events;
        }

        @Override
        public void finalize() throws Throwable {
            try {

            } finally {
                super.finalize();
            }
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public void start() {
            super.start((t) -> {
                try {
                    if(null!=_events && _events.size()>0){
                        this.run();
                    } else {
                        t.stop(); // no events, no thread
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
            if (null != _events) {
                // loop on all events and remove older than 2 seconds
                _events.reject((event, index, key) -> {
                    return event.isExpired(); // remove expired
                });
            }
        }

    }
}
