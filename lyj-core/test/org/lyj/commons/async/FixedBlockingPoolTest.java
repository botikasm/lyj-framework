package org.lyj.commons.async;

import org.junit.Test;
import org.lyj.commons.lang.Counter;

/**
 * Created by angelogeminiani on 03/03/17.
 */
public class FixedBlockingPoolTest {

    @Test
    public void test() throws Exception {

        System.out.println(AsyncUtils.reportActiveThreads());

        FixedBlockingPool pool = new FixedBlockingPool();
        pool.capacity(5); // 20 threads
        pool.corePoolSize(3);
        pool.maximumPoolSize(10);

        final Counter counter = new Counter();
        for (int i = 0; i < 1000; i++) {
            counter.inc();
            Async.loop(() -> {
                try {
                    Thread.sleep(10);
                } catch (Throwable t) {

                }
                return counter.value() > 900;
            });
            pool.start(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + ": " + AsyncUtils.countThreads());
                    Thread.sleep(100);
                } catch (Throwable t) {

                }
            });
            Thread.sleep(10);
        }

        Thread.sleep(3000);

        System.out.println(AsyncUtils.reportActiveThreads());
    }

}