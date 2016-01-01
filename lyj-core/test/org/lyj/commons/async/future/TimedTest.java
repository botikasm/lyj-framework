package org.lyj.commons.async.future;

import org.junit.Test;
import org.lyj.commons.util.FormatUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by angelogeminiani on 01/01/16.
 */
public class TimedTest {

    @Test
    public void testJoin() throws Exception {

        Timed alarmClock1 = new Timed(TimeUnit.SECONDS,
                3, // start after 3 seconds
                1, // run each 1 second
                0, 0);
        alarmClock1.setMaxThreads(2);
        // start first thread
        alarmClock1.start((t) -> {
            try {
                final String msg = FormatUtils.format("[%s] Stop after 10 loop. #%s",t.id(), t.count());
                System.out.println(msg);
                Thread.sleep((long) (Math.random() * 1000));
            } catch (Throwable ignored) {

            }
            if (t.count() > 9) { // stop after 10 loop
                t.stop();
            }
        });


        Timed alarmClock2 = new Timed(TimeUnit.SECONDS,
                0, // start after 3 seconds
                1, // run each 1 second
                0, 3);
        alarmClock2.start((t) -> {
            final String msg = FormatUtils.format("[%s] Stop after 3 loop. #%s",t.id(), t.count());
            System.out.println(msg);
        });


        // start second thread
        Timed alarmClock3 = new Timed(TimeUnit.SECONDS,
                0, // start immediately
                1, // run each 1 second
                0, 0);
        alarmClock3.start((t) -> {
            final String msg = FormatUtils.format("[%s] Stop after 5 loop. #%s",t.id(), t.count());
            System.out.println(msg);
            if (t.count() > 4) { // stop after 5 loop
                t.stop();
            }
        });


        alarmClock1.join();
        alarmClock2.join();
        alarmClock3.join();
    }
}