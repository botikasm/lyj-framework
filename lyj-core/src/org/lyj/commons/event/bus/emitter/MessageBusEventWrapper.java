package org.lyj.commons.event.bus.emitter;

import org.lyj.commons.event.Event;
import org.lyj.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap Event into container
 */
public class MessageBusEventWrapper {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Event _event;
    private int _timeout;
    private final long _timestamp;
    private final List<String> _listeners;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageBusEventWrapper(final Event event, final int timeout){
        _event = event;
        _timeout = timeout;
        _timestamp = System.currentTimeMillis();
        _listeners = new ArrayList<>();
    }

    @Override
    public void finalize() throws Throwable {
        try{

        } finally {
            super.finalize();
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Event event() {
        return _event;
    }

    public boolean isExpired(){
        return (System.currentTimeMillis() - _timestamp) > _timeout;
    }

    public boolean match(final String tag, final String name){
        final String event_tag = _event.getTag();
        final String event_name = _event.getName();
        boolean result = true;
        if(StringUtils.hasText(tag)){
            result = tag.equals(event_tag);
        }
        if(result && StringUtils.hasText(name)){
            result = name.equals(event_name);
        }
        return result;
    }

    public boolean isListened(final String id){
        synchronized (_listeners){
            return _listeners.contains(id);
        }
    }

    public MessageBusEventWrapper setListened(final String id){
        synchronized (_listeners){
            _listeners.add(id);
        }
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------



}
