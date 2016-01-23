package org.lyj.commons.event.bus.utils;

import org.lyj.commons.Delegates;
import org.lyj.commons.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Event bus container
 */
public class EventBusData {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final List<EventBusDataItem> _events;
    private final int _timeout;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EventBusData(final int timeout) {
        _events = new ArrayList<>();
        _timeout = timeout;
    }

    @Override
    public void finalize() throws Throwable {
        try {
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

    public EventBusData clear() {
        synchronized (_events) {
            _events.clear();
        }
        return this;
    }

    public EventBusData add(final Event event) {
        return this.add(event, _timeout);
    }

    public EventBusData add(final Event event, final int timeout) {
        synchronized (_events) {
            _events.add(new EventBusDataItem(event, timeout));
        }
        return this;
    }

    public Event[] listen(final String listenerId, final String tag, final String name) {
        synchronized (_events) {
            final List<Event> response = new ArrayList<>();
            for (final EventBusDataItem event : _events) {
                if (this.match(event, listenerId, tag, name)) {
                    response.add(event.setListened(listenerId).event());
                }
            }
            return response.toArray(new Event[response.size()]);
        }
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    void reject(final Delegates.IterationBoolCallback<EventBusDataItem> callback) {
        synchronized (_events) {
            if (null != callback) {
                int count = _events.size();
                for (int i = count - 1; i > -1; i--) {
                    final EventBusDataItem event = _events.get(i);
                    if (callback.handle(event, i, event.event().getName())) {
                        // remove
                        _events.remove(i);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean match(final EventBusDataItem item, final String listenerId, final String tag, final String name) {
        return !item.isListened(listenerId) && item.match(tag, name);
    }

}
