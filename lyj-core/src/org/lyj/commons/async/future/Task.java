package org.lyj.commons.async.future;

import org.lyj.commons.Delegates;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.MathUtils;
import org.lyj.commons.util.RandomUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * functional async action
 */
public class Task<T> {

    public enum State {
        /**
         * Thread state for a thread which has not yet started.
         */
        NEW,

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         */
        RUNNABLE,


        /**
         * Thread state for a terminated thread.
         * The thread has completed execution.
         */
        TERMINATED;
    }

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Long _id;
    private final ExecutorService _executor;

    // callbacks
    private ActionCallback<T> _callback_action;
    private ErrorCallback _callback_error;
    private ResultCallback<T> _callback_result;
    private ExitCallback<T> _callback_exit;

    private Exception _error;
    private T _data;
    private State _state;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Task(final ActionCallback<T> callback) {
        _executor = Executors.newSingleThreadExecutor();
        _callback_action = callback;
        _error = null;
        _state = State.NEW;
        _id = RandomUtils.getTimeBasedRandomLong(true, true);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Long getId() {
        return _id;
    }

    public State getState() {
        return _state;
    }

    public boolean hasError() {
        return null != _error;
    }

    public Throwable getError() {
        return _error;
    }

    //-- THREAD --//

    public Task<T> run() {

        // run thread
        if (_state != State.RUNNABLE) {
            final Task self = this;
            _state = State.RUNNABLE;

            // creates internal infinite loop
            final Future future = _executor.submit((Runnable) () -> {
                try {
                    if (null != _callback_action) {
                        _callback_action.handle(self);
                    }
                } catch (Exception t) {
                    self.fail(t);
                }

                // loop
                while (!_executor.isShutdown()) {
                    if (!_executor.isTerminated()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // interrupted
                        }
                    }
                }

                // change state before exit
                _state = State.TERMINATED;
                // invoke exit callback
                if (null != _callback_exit) {
                    _callback_exit.handle(_error, _data);
                }
            });
        }

        return this;
    }

    public T get() throws Exception {
        return this.get(1, TimeUnit.MINUTES);
    }

    public T get(final long timeout, final TimeUnit unit) throws Exception {
        // join execution
        try {
            _executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            // interrupted
        }

        if (null != _error) {
            throw _error;
        }

        // return data passed at "success" method
        return _data;
    }

    public T getSilent() {
        T result = null;
        try {
            result = this.get(1, TimeUnit.MINUTES);
        } catch (Throwable ignored) {
        }
        return result;
    }

    public T getSilent(final TimeUnit unit) {
        T result = null;
        try {
            result = this.get(1, unit);
        } catch (Throwable ignored) {
        }
        return result;
    }

    //-- CALLBACK --//

    public void fail(final String cause) {
        this.fail(new Exception(cause));
    }

    public void fail(final String cause, final T data) {
        this.fail(new Exception(cause), data);
    }

    public void fail(final Throwable cause) {
        this.fail(new Exception(cause));
    }

    public void fail(final Throwable cause, final T data) {
        this.fail(new Exception(cause), data);
    }

    public void fail(final Exception cause) {
        this.fail(cause, null);
    }

    public void fail(final Exception cause, final T data) {
        _error = cause;
        _data = data;
        try {
            if (null != _callback_error) {
                _callback_error.handle(cause);
            }
        } catch (Throwable ignored) {
        }
        this.stop();
    }

    public void success(final T data) {
        _data = data;
        try {
            if (null != _callback_result) {
                _callback_result.handle(data);
            }
        } catch (Throwable ignored) {
        }
        this.stop();
    }

    //-- CALLBACK HANDLERS --//

    public Task<T> result(final ResultCallback<T> callback) {
        _callback_result = callback;
        return this;
    }

    public Task<T> error(final ErrorCallback callback) {
        _callback_error = callback;
        return this;
    }

    public Task<T> exit(final ExitCallback<T> callback) {
        _callback_exit = callback;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void stop() {
        try {
            //System.out.println("attempt to shutdown executor");
            _executor.shutdown();
            _executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //System.err.println("tasks interrupted");
        } finally {
            //if (!_executor.isTerminated()) {
            //System.err.println("cancel non-finished tasks");
            //}
            _executor.shutdownNow();
            //System.out.println("shutdown finished");
        }
    }


    // ------------------------------------------------------------------------
    //                      I N N E R
    // ------------------------------------------------------------------------

    @FunctionalInterface
    public interface ActionCallback<T>  {
        void handle(final Task<T> self) throws Exception;
    }

    @FunctionalInterface
    public interface ResultCallback<T>  {
        void handle(final T data);
    }

    @FunctionalInterface
    public interface ErrorCallback  {
        void handle(final Exception err);
    }

    @FunctionalInterface
    public interface ExitCallback<T>  {
        void handle(final Throwable t, final T data);
    }

}
