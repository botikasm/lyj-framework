/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lyj.commons.async.future;

import org.lyj.commons.logging.AbstractLogEmitter;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Act as a timer,
 * Extend this class to create a scheduled task with following features:
 * <ol>
 * <li>Start after a delay</li>
 * <li>Loop has a delay (cicle after some delay)</li>
 * <li>Stop after a specific time</li>
 * <li>Stop after a specific number of cicles</li>
 * </ol>
 * <p>
 * <br>
 * Sample usage:
 * <code>
 * Timed alarmClock2 = new Timed(TimeUnit.SECONDS,<br>
 *      0, // start after 3 seconds<br>
 *      1, // run each 1 second<br>
 *      0, 3);<br>
 * alarmClock2.start((t) -> {<br>
 *      final String msg = FormatUtils.format("[%s] Stop after 3 loop. #%s",t.id(), t.count());<br>
 *      System.out.println(msg);<br>
 * });<br>
 * </code>
 */
public class Timed
        extends AbstractLogEmitter {

    // --------------------------------------------------------------------
    //               c o n s t a n t s
    // --------------------------------------------------------------------

    /**
     * If invocations might overlap, you can specify more than a single thread.
     */
    private static final int MAX_THREADS = 1;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final List<ScheduledFuture<?>> _tasks;
    private ScheduledFuture<?> _stop_task;
    private ScheduledExecutorService __scheduler;
    private int _count;

    //-- properties --//
    private int _max_threads;
    private TimeUnit _time_unit;
    private long _initial_delay;
    private long _interval;
    private long _stop_after_time;
    private int _stop_after_count;
    private boolean _daemon;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Timed() {
        this(TimeUnit.MILLISECONDS, 0, 0, 0, 0); // one shot task
    }

    public Timed(final long delayBetweenRuns) {
        this(TimeUnit.SECONDS, 0, delayBetweenRuns, 0, 0);
    }

    public Timed(final TimeUnit timeUnit,
                 final long initialDelay,
                 final long delayBetweenRuns,
                 final long stopAfterTime,
                 final int stopAfterCount) {
        _time_unit = timeUnit;
        _initial_delay = initialDelay;
        _interval = delayBetweenRuns;
        _stop_after_time = stopAfterTime;
        _stop_after_count = stopAfterCount;

        _tasks = new LinkedList<ScheduledFuture<?>>();
    }

    // --------------------------------------------------------------------
    //               p r o p e r t i e s
    // --------------------------------------------------------------------

    public int getMaxThreads() {
        return _max_threads > 0 ? _max_threads : MAX_THREADS;
    }

    /**
     * Set max number of concurrent threads in pool
     *
     * @param value int
     */
    public Timed setMaxThreads(final int value) {
        _max_threads = value > 0 ? value : MAX_THREADS;
        return this;
    }

    public boolean isDaemon() {
        return _daemon;
    }

    /**
     * Set to true if you want that tasks works as Daemon
     *
     * @param value boolean
     */
    public Timed setDaemon(final boolean value) {
        _daemon = value;
        return this;
    }

    public TimeUnit getTimeUnit() {
        return _time_unit;
    }

    public Timed setTimeUnit(final TimeUnit value) {
        _time_unit = value;
        return this;
    }

    public long getInitialDelay() {
        return _initial_delay;
    }

    public Timed setInitialDelay(final long value) {
        _initial_delay = value;
        return this;
    }

    public long getInterval() {
        return _interval;
    }

    public Timed setInterval(final long value) {
        _interval = value;
        return this;
    }

    public long getShutdownAfterTime() {
        return _stop_after_time;
    }

    public Timed setShutdownAfterTime(final long value) {
        _stop_after_time = value;
        return this;
    }

    public int getShutdownAfterCount() {
        return _stop_after_count;
    }

    public Timed setShutdownAfterCount(final int value) {
        _stop_after_count = value;
        return this;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    /**
     * Sound the alarm for a few seconds, then stop.
     */
    public ScheduledFuture<?> start(final Handler callback) {
        return startTask(callback);
    }

    public void stop(final boolean interrupt_if_running) {
        getScheduler().schedule(new StopTask(this, interrupt_if_running),
                0, TimeUnit.MILLISECONDS);
    }

    public void join() {
        synchronized (this) {
            for (final ScheduledFuture<?> task : _tasks) {
                try {
                    if (!task.isDone() && !task.isCancelled()) {
                        task.get();
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private ScheduledExecutorService getScheduler() {
        this.init();
        return __scheduler;
    }

    private void init() {
        if (null == __scheduler) {
            __scheduler = Executors.newScheduledThreadPool(this.getMaxThreads(), new Factory(this));
        }
    }

    private void finish(final boolean interrupt_if_running) {
        try {
            for (final ScheduledFuture<?> task : _tasks) {
                if (!task.isCancelled() || !task.isDone()) {
                    task.cancel(interrupt_if_running);
                }
            }
        } catch (Throwable ignored) {
        } finally {
            _tasks.clear();
        }
        //-- stop all tasks--//
        try {
            if (null != __scheduler) {
                if (interrupt_if_running) {
                    __scheduler.shutdownNow();
                } else {
                    __scheduler.shutdown();
                }
            }
        } catch (Throwable ignored) {
        } finally {
            __scheduler = null;
            _stop_task = null;
        }
    }

    private ScheduledFuture<?> startTask(final Handler callback) {
        if (_initial_delay == 0 && _interval == 0) {
            _stop_after_count = 1; // avoid infinite loop at no interval
        }
        final ScheduledFuture<?> future = getScheduler().scheduleWithFixedDelay(
                new RunTask(this, callback), _initial_delay, _interval, _time_unit
        );
        // add to internal list
        _tasks.add(future);

        //-- eval if run count-down--//
        if (_stop_after_time > 0 && null == _stop_task) {
            _stop_task = getScheduler().schedule(new StopTask(this, false), _stop_after_time, _time_unit);
        }

        return future;
    }

    private int incCount() {
        if (_count == Integer.MAX_VALUE) {
            _count = 0; // reset counter to avoid errors
        }
        _count++;
        return _count;
    }

    private void onTaskFinish() {

    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    /**
     * To start at a specific date in the future, the initial delay
     * needs to be calculated relative to the current time, as in :
     * Date futureDate = ...
     * long startTime = Timed.getRelativeToCurrentTime(futureDate);
     * Timed alarm = new Timed(startTime, ...);
     * This works only if the system clock isn't reset.
     *
     * @param date Specific Date in the future
     * @return Millisecond from now to specific Date.
     */
    public static long getRelativeToCurrentTime(final Date date) {
        return date.getTime() - System.currentTimeMillis();
    }

    // --------------------------------------------------------------------
    //               E M B E D D E D
    // --------------------------------------------------------------------

    /**
     * Thread Factory
     */
    private static final class Factory
            implements ThreadFactory {

        private final Timed _sender;

        public Factory(final Timed sender) {
            _sender = sender;
        }

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(r);
            t.setPriority(Thread.NORM_PRIORITY);
            t.setDaemon(_sender.isDaemon());
            return t;
        }
    }

    public static final class TaskInterruptor {

        private boolean _interrupted = false;
        private final int _count;
        private final int _hash;

        public TaskInterruptor(final int hash, final int counter) {
            _hash = hash;
            _count = counter;
        }

        public void stop() {
            _interrupted = true;
        }

        public boolean isStopped() {
            return _interrupted;
        }

        public int count() {
            return _count;
        }

        public int id() {
            return _hash;
        }
    }

    @FunctionalInterface
    public static interface Handler {
        void handle(final TaskInterruptor interruptor);
    }

    /**
     * Task Runner
     */
    private static final class RunTask
            implements Runnable {

        private final Handler _callback;
        private final Timed _sender;

        public RunTask(final Timed sender,
                       final Handler callback) {
            _sender = sender;
            _callback = callback;
        }

        @Override
        public void run() {
            this.invoke();
        }

        private void invoke() {
            try {
                if (null != _sender && null != _callback) {
                    final int count = _sender.incCount();
                    final TaskInterruptor interruptor = new TaskInterruptor(_sender.hashCode(), count);
                    _callback.handle(interruptor);

                    boolean stop = interruptor.isStopped();

                    // if reached max number of iterations, stop
                    final int max_iterations = _sender.getShutdownAfterCount();
                    if (max_iterations > 0 && count >= max_iterations) {
                        stop = true;
                    }

                    if (stop) {
                        _sender.stop(true);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    } //-- start task

    /**
     * Tasks Stopper
     */
    private static final class StopTask
            implements Runnable {

        private final Timed _sender;
        private final boolean _interrupt_if_running;

        public StopTask(final Timed sender, final boolean interrupt_if_running) {
            _sender = sender;
            _interrupt_if_running = interrupt_if_running;
        }

        @Override
        public void run() {
            if (null != _sender) {
                _sender.finish(_interrupt_if_running);
            }
        }
    } // stop-task


}
