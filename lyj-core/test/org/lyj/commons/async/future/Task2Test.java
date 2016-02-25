package org.lyj.commons.async.future;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the Task
 */
public class Task2Test {

    @Test
    public void testGetTimeout() throws Exception {
        final long sleep = 2000;
        final long timeout = 0;
        final Task<String> task = new Task<String>((interruptor)->{
            try{
                Thread.sleep(sleep);
                interruptor.success("Finished");
            }catch(Throwable t){
                interruptor.fail(t);
            }
        }).setTimeout(timeout);

        String response = task.get();
        assertEquals("Finished", response);
    }
}