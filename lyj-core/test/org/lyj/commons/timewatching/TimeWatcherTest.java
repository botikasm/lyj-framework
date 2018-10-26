package org.lyj.commons.timewatching;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by angelogeminiani on 07/04/17.
 */
public class TimeWatcherTest {

    @Test
    public void start() throws Exception {

        TimeWatcher tw = new TimeWatcher();

        tw.start();
        Thread.sleep(1000);
        tw.pause();
        Thread.sleep(1000);
        tw.resume();
        tw.stop();
        assertTrue(tw.elapsed() < 1010);

        tw.start();
        Thread.sleep(1000);
        tw.pause();
        Thread.sleep(1000);
        tw.stop();
        assertTrue(tw.elapsed() < 1010);
    }

}