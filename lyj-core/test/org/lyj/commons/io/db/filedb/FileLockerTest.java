package org.lyj.commons.io.db.filedb;

import org.junit.Test;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.Locker;

public class FileLockerTest {

    @Test
    public void acquire() {

        System.out.println("lock A");
        Locker.instance().lock("A");

        Async.delay((args)->{
            System.out.println("release A");
            Locker.instance().unlock("A");
        }, 1000);

        Locker.instance().lock("A");
        System.out.println("lock A");


        System.out.println("Exiting...");
    }
}