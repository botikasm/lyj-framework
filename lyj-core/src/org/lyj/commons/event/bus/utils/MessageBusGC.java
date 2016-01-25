package org.lyj.commons.event.bus.utils;

import org.lyj.commons.async.future.Timed;
import org.lyj.commons.event.bus.MessageBus;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;

import java.util.concurrent.TimeUnit;

/**
 * garbage collector for events
 */
public class MessageBusGC
        extends Timed {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private MessageBus _bus;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageBusGC(final MessageBus bus, final int interval) {
        super(TimeUnit.MILLISECONDS, 0, interval, 0, 0);

        _bus = bus;
    }

    @Override
    public void finalize() throws Throwable {
        try {

        }finally{
            super.finalize();
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void start(){
        super.start((t) -> {
            try {
                this.run();
            } catch (Throwable err) {
                super.error("run", FormatUtils.format("Error running garbage collector for EventBus: %s",
                        ExceptionUtils.getMessage(err)));
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void run(){
        if(null!=_bus){
            // loop on all events and remove older than 2 seconds
            _bus.events().reject((event, index, key)->{
                return event.isExpired(); // remove expired
            });
        }
    }

}
