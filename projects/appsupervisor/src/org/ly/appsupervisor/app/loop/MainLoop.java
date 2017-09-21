package org.ly.appsupervisor.app.loop;

import org.lyj.commons.async.future.Timed;

import java.util.concurrent.TimeUnit;

/**
 * Base parametric scheduled task.
 */
public class MainLoop
        extends Timed {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final TimeUnit UNIT = TimeUnit.SECONDS;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _working;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MainLoop(final int interval) {
        super(UNIT, 0, interval, 0, 0);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        super.start(this::handle);
    }

    public void close() {
        super.stop(true);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handle(final TaskInterruptor interruptor) {
        if (!_working) {
            _working = true;
            try {
                ExecMonitor.instance().monitor(this::handleOutput, null);
            } catch (Throwable t) {
                super.error("handle", t);
            } finally {
                _working = false;
            }
        }
    }

    /**
     * Handle program console output
     */
    private void handleOutput(final String line){
        System.out.println(line);
    }

}
