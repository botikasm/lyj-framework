package org.lyj.commons.async.future;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 13/02/16.
 */
public class LoopTest {

    @Test
    public void timeoutTest(){
        Loop l = new Loop(0,1000,2000);
        l.start((i)->{
            System.out.println("count: " + i.count());
        });
        l.join();
    }

}