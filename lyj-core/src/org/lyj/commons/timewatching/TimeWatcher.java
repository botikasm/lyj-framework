package org.lyj.commons.timewatching;

/**
 * Simple time watcher.
 */
public class TimeWatcher {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _started;
    private boolean _paused;
    private long _start;
    private long _end;
    private long _start_pause;
    private long _end_pause;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public TimeWatcher() {
        _started = false;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public synchronized long start() {
        if (!_started) {
            _started = true;
            _start = System.currentTimeMillis();
        }
        return _start;
    }

    public synchronized long stop() {
        if (_started) {
            _started = false;
            this.resume();
            _end = System.currentTimeMillis();
        }
        return _end;
    }

    public synchronized long pause() {
        if (!_paused) {
            _paused = true;
            _start_pause = System.currentTimeMillis();
        }
        return _start_pause;
    }

    public synchronized long resume() {
        if (_paused) {
            _paused = false;
            _end_pause = System.currentTimeMillis();
        }
        return _end_pause;
    }

    public synchronized long elapsed() {
        final long pause = _end_pause - _start_pause;
        return _end - _start - pause;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static TimeWatcher create() {
        return new TimeWatcher();
    }


}
