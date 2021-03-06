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

package org.lyj.commons.async;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.lang.ValueObject;
import org.lyj.commons.util.MathUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class to run async methods
 */
public abstract class Async {


    /**
     * Create an executor service useful to wait for an undefined termination.
     */
    public static ExecutorService serviceAlive() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        Thread t = new Thread(() -> {
            try {
                while (!executor.isShutdown()) {
                    Thread.sleep(100);
                }
            } catch (Throwable ignored) {
                // ignored
            }
        });
        t.setName("invoke-" + t.getName());
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        executor.submit(t);
        return executor;
    }

    public static <T> Thread wrap(final Delegates.Callback<T> handler, final T args) {
        if (null != handler) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.handle(args);
                }
            });
            t.setName("invoke-" + t.getName());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
        return null;
    }

    public static Thread wrap(final Delegates.VarArgsCallback handler, final Object... args) {
        if (null != handler) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.handle(args);
                }
            });
            t.setName("invoke-" + t.getName());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
        return null;
    }

    public static Thread invoke(final Delegates.VarArgsCallback handler, final Object... args) {
        if (null != handler) {
            final Thread t = wrap(handler, args);
            t.start();
            return t;
        }
        return null;
    }

    public static Thread delay(final Delegates.VarArgsCallback handler, final int delay, final Object... args) {
        if (null != handler) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delay);
                        handler.handle(args);
                    } catch (Throwable ignored) {
                    }
                }
            });
            t.setName("delay-" + t.getName());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
            return t;
        } else {
            return null;
        }
    }

    public static Thread debounce(final String func_uid,
                                  final Delegates.VarArgsCallback handler,
                                  final int delay,
                                  final Object... args) {
        if (null != handler) {
            if (!Locker.instance().isLocked(func_uid)) {
                final ValueObject<Long> clock = new ValueObject<>(System.currentTimeMillis());
                Locker.instance().lock(func_uid, clock);
                try {
                    while (System.currentTimeMillis() - clock.content() < delay) {
                        Thread.sleep(delay);
                    }
                    final Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                handler.handle(args);
                            } catch (Throwable ignored) {
                            }
                        }
                    });
                    t.setName("debounced-" + t.getName());
                    t.setDaemon(true);
                    t.setPriority(Thread.NORM_PRIORITY);
                    t.start();
                    return t;
                } catch (Throwable ignored) {
                } finally {
                    Locker.instance().unlock(func_uid);
                }
            } else {
                ((ValueObject<Long>) Locker.instance().getLocked(func_uid)).content(System.currentTimeMillis());
            }
        }
        return null;

    }

    public static Thread loop(final Delegates.Function<Boolean> handler) {
        return loop(handler, 100);
    }

    public static Thread loop(final Delegates.Function<Boolean> handler, final int delay) {
        if (null != handler) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean interrupted = false;
                    try {
                        while (!interrupted) {
                            Thread.sleep(delay);
                            interrupted = handler.handle();
                        }
                    } catch (Throwable ignored) {
                    }
                }
            });
            t.setName("loop-" + t.getName());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
            return t;
        } else {
            return null;
        }
    }

    public static void sleep(final int delay) {
        final Task<Void> task = new Task<>((t) -> {
            try {
                Async.delay((args) -> {
                    t.success(null);
                }, delay);
            } catch (Throwable err) {
                t.fail(err);
            }
        });
        task.getSilent();
    }

    public static void maxConcurrent(final Thread[] threads,
                                     final int maxConcurrentThreads) {
        maxConcurrent(threads, maxConcurrentThreads, null);
    }

    public static void maxConcurrent(final Thread[] threads,
                                     final int maxConcurrentThreads,
                                     final Delegates.ProgressCallback onProgress) {
        final int len = threads.length;
        //-- Async execution --//
        invoke(new Delegates.VarArgsCallback() {
            @Override
            public void handle(Object... args) {
                final int max = maxConcurrentThreads <= len ? maxConcurrentThreads : len;
                final Thread[] concurrent = new Thread[max];
                int count = 0;
                for (int i = 0; i < len; i++) {
                    concurrent[count] = threads[i];
                    count++;
                    if (count == max) {
                        startAll(concurrent);
                        joinAll(concurrent);
                        count = 0;
                    }
                    if (null != onProgress) {
                        onProgress.handle(i, len, MathUtils.progress(i + 1, len, 2));
                    }
                }
            }
        });
    }

    public static Thread[] maxConcurrent(final int length,
                                         final int maxConcurrentThreads,
                                         final Delegates.CreateRunnableCallback runnableFunction) {
        if (null == runnableFunction) {
            return new Thread[0];
        }
        Thread[] result = new Thread[length];
        final int max = maxConcurrentThreads <= length ? maxConcurrentThreads : length;
        final Thread[] concurrent = new Thread[max];
        int count = 0;
        for (int i = 0; i < length; i++) {
            final Runnable runnable = runnableFunction.handle(i, length);
            final Thread t = (runnable instanceof Thread) ? (Thread) runnable : new Thread(runnable);
            result[i] = t;
            concurrent[count] = t;
            count++;
            if (count == max || (i + 1) == length) {
                startAll(concurrent);
                joinAll(concurrent);
                count = 0;
            }
        }
        return result;
    }

    public static void startAll(final Thread... threads) {
        for (final Thread thread : threads) {
            try {
                if (!thread.isAlive() && !thread.isInterrupted()) {
                    thread.start();
                }
            } catch (Throwable ignored) {
            }
        }
    }

    public static void joinAll(final Thread... threads) {
        final int length = threads.length;
        final Set<Long> terminated = new HashSet<Long>();
        while (length > terminated.size()) {
            for (final Thread thread : threads) {
                try {
                    final Thread.State state = thread.getState();
                    if (Thread.State.RUNNABLE.equals(state)) {
                        thread.join();
                    } else if (Thread.State.TERMINATED.equals(state)) {
                        terminated.add(thread.getId());
                    } else {
                        // System.out.println(state);
                    }
                } catch (Throwable ignored) {
                }
            }
        }

    }

    public static void startAllThreads(final Collection<? extends Thread> threads) {
        startAll(threads.toArray(new Thread[threads.size()]));
    }

    public static void joinAllThreads(final Collection<? extends Thread> threads) {
        if (null != threads && threads.size() > 0) {
            // final Task[] array = tasks.toArray(new Task[tasks.size()]);
            joinAll(threads.toArray(new Thread[threads.size()]));
        }
    }

    public static void joinAll(final Collection<? extends Task> tasks) {
        if (null != tasks && tasks.size() > 0) {
            // final Task[] array = tasks.toArray(new Task[tasks.size()]);
            joinAll(tasks.toArray(new Task[tasks.size()]));
        }
    }

    public static void joinAll(final Task... tasks) {
        final int length = tasks.length;
        final Set<Long> terminated = new HashSet<>();
        while (length > terminated.size()) {
            for (final Task task : tasks) {
                try {
                    final Task.TaskState state = task.getState();
                    if (Task.TaskState.NEW.equals(state)) {
                        task.run();
                    } else if (Task.TaskState.RUNNABLE.equals(state)) {
                        task.get();
                    } else if (Task.TaskState.TERMINATED.equals(state)) {
                        terminated.add(task.getId());
                    } else {
                        System.out.println(state);
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //               E M B E D D E D
    // --------------------------------------------------------------------

    private static class CallbackThread<T> {

        CallbackThread(final Delegates.Callback<T> callback) {

        }

    }

}
