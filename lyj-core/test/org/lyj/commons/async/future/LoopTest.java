package org.lyj.commons.async.future;

import org.junit.Test;

/**
 * Created by angelogeminiani on 13/02/16.
 */
public class LoopTest {

    @Test
    public void timeoutTest() {
        Loop l = new Loop(0, 1000);
        l.start((i) -> {
            System.out.println("count: " + i.count());
            if (i.count() > 1) {
                i.stop();
            }
        });
        l.join();
    }

}