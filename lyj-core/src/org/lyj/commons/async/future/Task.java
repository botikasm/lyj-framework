package org.lyj.commons.async.future;

import org.lyj.commons.async.Async;
import org.lyj.commons.util.RandomUtils;

import java.util.concurrent.TimeoutException;

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
    private Thread _thread;
    private final TaskResponse<T> _interruptor;


    // callbacks
    private ActionCallback<T> _callback_action;
    private ErrorCallback _callback_error;
    private ResultCallback<T> _callback_result;
    private ExitCallback<T> _callback_exit;

    private Exception _error;
    private T _data;
    private State _state;
    private long _execution_timeout;
    private int _initial_delay;
    private int _sleep;

    private long _time_start;
    private long _time_end;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Task(final ActionCallback<T> callback) {
        this(callback, 0, 100, 0);
    }

    public Task(final ActionCallback<T> callback,
                 final int initialDelay, final int sleep, final int timeout) {
        _callback_action = callback;
        _error = null;
        _state = State.NEW;
        _id = RandomUtils.getTimeBasedRandomLong(true, true);

        _initial_delay = initialDelay;
        _sleep = sleep;
        _execution_timeout = timeout;

        _interruptor = new TaskResponse<T>();
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

    public Task<T> setTimeout(final long milliseconds) {
        _execution_timeout = milliseconds;
        return this;
    }

    public long getTimeout() {
        return _execution_timeout;
    }

    //-- THREAD --//

    public Task<T> run() {
        if (!this.isRunning()) {
            this.runThread();
        }
        return this;
    }

    public T get() throws Exception {
        this.run();
        // join execution
        try {
            _thread.join();
        } catch (InterruptedException e) {
            // interrupted
            //-- TIMEOUT --//
            this.fail(new Exception("Join Task timeout."), null);
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
            result = this.get();
        } catch (Exception err) {
            _error = err;
        }
        return result;
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

    private void runThread() {
        this.stop();
        _thread = new Thread((Runnable) () -> {
            try {
                _state = State.RUNNABLE;
                _time_start = System.currentTimeMillis();

                // initial delay
                if (_initial_delay > 0) {
                    Thread.sleep(_initial_delay);
                }

                if (null != _callback_action) {
                    Async.invoke((args)->{
                        try {
                            _callback_action.handle(_interruptor);
                        } catch (Exception ex_error) {
                            _interruptor.fail(ex_error);
                        }
                    });
                } else {
                    _interruptor.fail(new Exception("Wrong state: missing action to execute in Task."));
                }

                // wait finish
                while (!_interruptor.finished()) {

                    // run delay
                    if (_sleep > 0) {
                        Thread.sleep(_sleep);
                    }

                    // timeout
                    if (_execution_timeout > 0 && System.currentTimeMillis() - _time_start > _execution_timeout) {
                        _interruptor.fail(new TimeoutException("Task Action timeout ms: " + _execution_timeout));
                    }
                }

            } catch (Exception t) {
                _error = t;
            } finally {
                _time_end = System.currentTimeMillis();
                _error = _interruptor.error();
                _data = _interruptor.data();

                // stop and reset interruptor
                this.stop();

                if(null!=_error){
                    this.fail(_error, _data);
                } else {
                    this.success(_data);
                }

                // invoke exit callback if any
                if (null != _callback_exit) {
                    _callback_exit.handle(_error, _data);
                }
            }
        });
        _thread.start();
    }

    private void stop() {
        if (null != _thread) {
            if (this.isRunning()) {
                _thread.interrupt();
            }
            _thread = null;
        }
        _interruptor.reset();
        _state = State.TERMINATED;
    }

    private boolean isRunning() {
        return null != _thread && _thread.isAlive() && !_thread.isInterrupted();
    }

    private void fail(final Exception cause, final T data) {
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

    private void success(final T data) {
        _data = data;
        try {
            if (null != _callback_result) {
                _callback_result.handle(data);
            }
        } catch (Throwable ignored) {
        }
        this.stop();
    }

    // ------------------------------------------------------------------------
    //                      I N N E R
    // ------------------------------------------------------------------------

    @FunctionalInterface
    public interface ActionCallback<T> {
        void handle(final TaskResponse<T> interruptor) throws Exception;
    }

    @FunctionalInterface
    public interface ResultCallback<T> {
        void handle(final T data);
    }

    @FunctionalInterface
    public interface ErrorCallback {
        void handle(final Exception err);
    }

    @FunctionalInterface
    public interface ExitCallback<T> {
        void handle(final Throwable t, final T data);
    }

    // --------------------------------------------------------------------
    //               E M B E D D E D
    // --------------------------------------------------------------------

    public static class TaskResponse<T> {

        private Exception _error;
        private T _data;
        private boolean _stopped = false;


        //-- PROPERTIES --//

        public Exception error() {
            return _error;
        }

        public T data() {
            return _data;
        }

        public boolean finished() {
            return _stopped;
        }

        public void reset() {
            _stopped = false;
            _error = null;
            _data = null;
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
            _stopped = true;
        }

        public void success(final T data) {
            _data = data;
            _stopped = true;
        }

    }

}
