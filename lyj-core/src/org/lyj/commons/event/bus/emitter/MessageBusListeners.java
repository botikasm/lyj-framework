package org.lyj.commons.event.bus.emitter;

import org.lyj.commons.event.Event;
import org.lyj.commons.event.IEventListener;
import org.lyj.commons.event.bus.MessageListener;

import java.util.ArrayList;
import java.util.List;

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

    private final List<MessageListener> _listeners;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageBusListeners() {
        _listeners = new ArrayList<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void clear(){
        synchronized (_listeners){
            _listeners.clear();
        }
    }

    public MessageListener add(final MessageListener listener){
        synchronized (_listeners){
            if(!_listeners.contains(listener)) {
                _listeners.add(listener);
            }
            return listener;
        }
    }


    public void process(final MessageBusEvents eventBus){
        synchronized (_listeners){
            final List<MessageListener> remove_list = new ArrayList<>();

            for(final MessageListener listener:_listeners){
                final String id = listener.getId();
                final String name = listener.getEventName();
                final String[] tags = listener.getEventTags();
                final Event[] events = eventBus.listen(id, tags, name);
                if(events.length>0) {
                    final IEventListener[] executors = listener.listeners();
                    if(executors.length>0) {
                        for(final Event event:events){
                            for(final IEventListener executor:executors){
                                try {
                                    executor.on(event);
                                }catch(Throwable t){
                                    remove_list.add(listener); // mark as remove
                                }
                            }
                        }
                    }
                }
            }

            // remove dead listeners
            if(remove_list.size()>0){
                for(final MessageListener listener:remove_list){
                    _listeners.remove(listener);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------



}
