package org.lyj.commons.async.future;

import org.junit.Test;
import org.lyj.commons.async.Async;

import static org.junit.Assert.assertEquals;

/**
 * Test the Task
 */
public class Task2Test {

    @Test
    public void testGetTimeout() throws Exception {
        final long sleep = 2000;
        final long timeout = 1000;

        System.out.println("START ASYNC CALL");

        Async.invoke((args)->{
            try {
                final Task<String> task = new Task<String>((interruptor) -> {
                    System.out.println("RUNNING TASK...");
                    try {
                        Thread.sleep(sleep);
                        interruptor.success("Finished");
                    } catch (Throwable t) {
                        interruptor.fail(t);
                    }
                }).setTimeout(timeout);

                String response = task.get();
                assertEquals("Finished", response);
            }catch(Throwable t){
                System.out.println(t);
            }
        }).join();

        System.out.println("EXITING TEST UNIT");

    }
}