package org.lyj.commons.event.bus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lyj.commons.event.Event;

/**
 * Test fo EventBus
 */
public class EventBusTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRun() throws Exception {

        System.out.println("start testing event bus");

        final Event event1 = new Event(this, "on_test").setTag("sample");
        final Event event2 = new Event(this, "on_test2").setTag("sample2");

        EventBus bus = new EventBus().emit(event1).emit(event2);

        System.out.println(bus.toString());

        EventListener listener = bus.createListener();

        listener.on((event)->{
            System.out.println("LISTENER: " + event.toString());
        });

        EventListener listener_tag = bus.createListener().setEventTag("sample2");

        listener_tag.on((event)->{
            System.out.println("LISTENER TAG: " + event.toString());
        });

        // emit another event
        bus.emit(new Event(this, "on_test3").setTag("tag 3"));

        while(bus.size()>0){
            Thread.sleep(3000);
        }

        bus = null;
        listener.clear(); // clear references to completely remove handlers references
        listener = null;
        listener_tag.clear();
        listener_tag = null;

        System.gc();

        Thread.sleep(3000);

        System.gc();

        Thread.sleep(1000);

        System.out.println("event bus is empty");
    }
}