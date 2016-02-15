package org.lyj.commons.async.future;

/**
 * Infinite Loop
 */
public class Loop {


    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private int _initial_delay;
    private int _run_interval;
    private int _timeout;

    private long _time_start;
    private long _time_end;

    private final LoopInterruptor _interruptor;
    private LoopHandler _callback;
    private Thread _thread;
    private Throwable _error;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Loop() {
        this(0, 100, 0);
    }

    public Loop(final int runInterval) {
        this(0, runInterval, 0);
    }

    public Loop(final int initialDelay, final int runInterval) {
        this(initialDelay, runInterval, 0);
    }

    private Loop(final int initialDelay, final int runInterval, final int timeout) {
        _initial_delay = initialDelay;
        _run_interval = runInterval;
        _timeout = timeout;

        _interruptor = new LoopInterruptor();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public Loop start(final LoopHandler callback) {
        if (null != callback) {
            _interruptor.reset();
            _callback = callback;
            this.run();
        }
        return this;
    }

    public void interrupt() {
        _interruptor.stop();
        this.stop();
    }

    public void join(){
        this.join(0);
    }

    public void join(final long milliseconds) {
        try {
            if (this.isRunning()) {
                if (milliseconds > 0) {
                    _thread.join(milliseconds);
                } else {
                    _thread.join();
                }
            }
        } catch (Throwable t) {
            _error = t;
        }
    }

    public boolean isRunning() {
        return null != _thread && _thread.isAlive() && !_thread.isInterrupted();
    }

    public long duration(){
        return _time_end - _time_start;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void run() {
        this.stop();
        _thread = new Thread((Runnable) () -> {
            try {
                _time_start = System.currentTimeMillis();
                // initial delay
                if (_initial_delay > 0) {
                    Thread.sleep(_initial_delay);
                }
                // loop
                while (!_interruptor.isStopped()) {
                    // counter
                    _interruptor.inc();

                    if (null != _callback) {
                        if(!_interruptor.isPaused()){
                            _callback.handle(_interruptor);
                        }
                    } else {
                        _interruptor.stop();
                    }

                    // run delay
                    if (_run_interval > 0) {
                        Thread.sleep(_run_interval);
                    }

                    // timeout
                    if (_timeout > 0 && System.currentTimeMillis() - _time_start > _timeout) {
                        _interruptor.stop();
                    }
                }
            } catch (Throwable t) {
                _error = t;
            } finally {
                _time_end = System.currentTimeMillis();
            }
        });
        _thread.start();
    }

    private void stop() {
        if (null != _thread) {
            _thread.interrupt();
            _thread = null;
        }
    }

    // --------------------------------------------------------------------
    //               E M B E D D E D
    // --------------------------------------------------------------------

    public static class LoopInterruptor {

        private boolean _stopped;
        private boolean _paused;
        private long _count;

        public void reset(){
            _count = 0;
            _stopped = false;
        }
        public void stop() {
            _stopped = true;
        }

        public void pause() {
            _paused = true;
        }

        public void resume() {
            _paused = false;
        }

        public boolean isStopped() {
            return _stopped;
        }

        public boolean isPaused() {
            return _paused;
        }

        public long count(){
            return _count;
        }

        void inc(){
            _count++;
        }
    }

    public interface LoopHandler {
        void handle(final LoopInterruptor interruptor);
    }
}
