package org.lyj.commons.async.future;

import java.util.HashSet;
import java.util.Set;

/**
 * Cicles is a Loop controller that handle a single loop invoking many sync tasks.
 * At each loop tick this controller check for all added tasks and evaluate
 * if run method must be called.
 */
public class Cicles {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int TICK_MS = 10; // loop tick

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Set<Runner> _delegates;

    private int _loop_tick;
    private Loop _loop;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Cicles() {
        this(TICK_MS);
    }

    public Cicles(int tick_ms) {
        _loop_tick = tick_ms;
        _delegates = new HashSet<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isRunning() {
        return null != _loop && _loop.isRunning();
    }

    public int loopTick() {
        return _loop_tick;
    }

    public Cicles loopTick(final int value) {
        _loop_tick = value;
        if (this.isRunning()) {
            this.start(); // restart
        }
        return this;
    }

    public Cicles start() {
        this.stop();
        if (null == _loop) {
            _loop = new Loop(_loop_tick).start(this::tick);
        }
        return this;
    }

    public Cicles stop() {
        if (null != _loop) {
            try {
                _loop.interrupt();
            } finally {
                _loop = null;
            }
        }
        return this;
    }

    public Cicles add(final Runner runner) {
        this.addRunner(runner);
        return this;
    }

    public Cicles add(final int interval, final Runnable runner) {
        this.addRunner(interval, runner);
        return this;
    }

    public Cicles clear() {
        synchronized (_delegates) {
            _delegates.clear();
            return this;
        }
    }

    public void join(final long timeout) {
        if (null != _loop) {
            _loop.join(timeout);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void addRunner(final int interval, final Runnable runner) {
        addRunner(new Runner(interval, runner));
    }

    private void addRunner(final Runner runner) {
        synchronized (_delegates) {
            if (null != runner && runner._interval > 0 && null != runner._task) {
                _delegates.add(runner);
            }
        }
    }

    private void tick(final Loop.LoopInterruptor interruptor) {
        synchronized (_delegates) {
            _delegates.forEach((runner) -> {
                try {
                    if (null != runner && runner.expired()) {
                        runner.run();
                    }
                } catch (final Throwable t) {
                    // error running the task
                }
            });

        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static Cicles __instance;

    public static synchronized Cicles instance() {
        if (null == __instance) {
            __instance = new Cicles();
        }
        return __instance;
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class Runner
            implements Runnable {

        private Runnable _task;
        private final int _interval;
        private long _last_tick;

        public Runner(final int interval) {
            this(interval, null);
        }

        public Runner(final int interval,
                      final Runnable task) {
            _interval = interval;
            _task = task;
            _last_tick = 0;
        }

        public Runner task(final Runnable value) {
            _task = value;
            return this;
        }

        public boolean expired() {
            if (_interval > 0) {
                final long curr_tick = System.currentTimeMillis();
                final long diff = curr_tick - _last_tick;
                return diff >= _interval;
            }
            return false;
        }

        @Override
        public void run() {
            if (null != _task) {
                try {
                    _task.run();
                } finally {
                    _last_tick = System.currentTimeMillis();
                }
            }
        }
    }

}

