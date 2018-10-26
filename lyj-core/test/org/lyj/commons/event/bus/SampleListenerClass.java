package org.lyj.commons.event.bus;

import org.lyj.commons.event.Event;

/**
 * Created by angelogeminiani on 15/02/16.
 */
public class SampleListenerClass {

    private MessageListener _listener;

    public SampleListenerClass() {
        _listener = MessageBus.getInstance().createListener();
        _listener.on(this::handle);
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("SampleListenerClass: FINALIZE");
        super.finalize();
    }

    public void clear() {
        _listener.clear();
    }

    private void handle(final Event event) {
        System.out.println("SampleListenerClass: " + event.getTag());
    }

}
