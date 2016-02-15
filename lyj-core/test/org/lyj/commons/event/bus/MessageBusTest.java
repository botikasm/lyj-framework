package org.lyj.commons.event.bus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lyj.commons.event.Event;

/**
 * Test fo EventBus
 */
public class MessageBusTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRun() throws Exception {

        System.out.println("start testing event bus");

        SampleListenerClass class_listener = new SampleListenerClass();

        final Event event1 = new Event(this, "on_test").setTag("sample");
        final Event event2 = new Event(this, "on_test2").setTag("sample2");

        MessageBus bus = MessageBus.getInstance();
       bus.emit(event1).emit(event2);

        System.gc(); // force GC

        System.out.println(bus.toString());

        MessageListener listener = bus.createListener();
        listener.on((event)->{
            System.out.println("LISTENER ALL: " + event.getTag());
        });

        //listener.clear(); // clear references to completely remove handlers references
        listener = null;

        MessageListener listener_tag = bus.createListener().setEventTag("sample2");
        listener_tag.on((event)->{
            System.out.println("LISTENER TAG 2: " + event.getTag());
        });

        MessageListener listener_multi_tag = bus.createListener().setEventTag("sample2").addEventTag("tag 3");
        listener_multi_tag.on((event)->{
            System.out.println("LISTENER MULTI TAG: " + event.getTag());
        });

        // emit another event
        bus.emit(new Event(this, "on_test3").setTag("tag 3"));

        while(bus.events().size()>0){
            Thread.sleep(3000);
        }

        bus = null;

        listener_tag.clear();
        listener_tag = null;
        listener_multi_tag.clear();
        listener_multi_tag = null;

        System.gc();

        Thread.sleep(3000);

        class_listener.clear();
        class_listener = null;

        System.gc();

        Thread.sleep(1000);

        System.out.println("event bus is empty");
    }
}