package org.lyj.commons.io.filecache;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.io.cache.filecache.CacheWatchDog;

/**
 *
 */
public class TempRepositoryTest {


    @BeforeClass
    public static void setUp() {
        TestInitializer.init();
    }

    @Test
    public void testTemp() throws Exception {

        CacheWatchDog temp = new CacheWatchDog("./repository");

        temp.join();

        System.out.println("exit");
    }
}
