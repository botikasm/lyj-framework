package org.lyj.commons.event.bus;

import org.lyj.commons.event.Event;
import org.lyj.commons.event.bus.utils.MessageBusData;
import org.lyj.commons.event.bus.utils.MessageBusGC;
import org.lyj.commons.util.RandomUtils;

/**
 *  Event container.
 *
 *  To add an event to event bus call EventBus.emit().
 *
 *  To listen events from EventBus, create a listener calling EventBus.createListener();
 *
 *  The EventBus allow to implement the event pattern with no danger for crossed references and memory leak.
 *  Event emitters and event listeners are decoupled and never referenced each other.
 *
 *  EventBus can be used as a singleton object or creating new instances.
 *  Each event bus has an internal thread that works as a garbage collector.
 *
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
    private final MessageBusData _data;
    private final String _id;

    private boolean _disposed;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageBus(){
        this(DEF_EVENT_TIMEOUT, DEF_INTERVAL);
    }

    public MessageBus(final int eventTimeout, final int gcInterval){
        _data = new MessageBusData(eventTimeout);
        _gc = new MessageBusGC(this, gcInterval);
        _id = RandomUtils.randomUUID();
        _disposed = false;
    }

    @Override
    public void finalize() throws Throwable {
        try{
            _disposed = true;
            _gc.stop(true);
            _data.clear();
        } catch(Throwable ignored){
            // nothing useful to do here
        } finally {
            super.finalize();
        }
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + " [" + _id + "] " +
                _data.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isDisposed(){
        return _disposed;
    }

    public int size(){
        return _data.size();
    }

    public MessageBusData events(){
        return _data;
    }

    public MessageBus emit(final Event event){
        if(!_gc.isRunning()){
            _gc.start();
        }
        _data.add(event);
        return this;
    }



    //-- factory --//

    public MessageListener createListener(){
        return new MessageListener(this, (int)(DEF_INTERVAL*0.5));
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    Event[] listen(final String listenerId, final String tag, final String name){
        return _data.listen(listenerId, tag, name);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    //-- singleton --//

    private static MessageBus __instance;

    public static MessageBus getInstance(){
        if(null==__instance){
            __instance = new MessageBus();
        }
        return __instance;
    }



}
