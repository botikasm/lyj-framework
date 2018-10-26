package org.lyj.commons.async.future;

import org.lyj.commons.async.Async;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.RandomUtils;

import java.util.concurrent.TimeoutException;

/**
 * functional async action
 */
public class Task<T>
        extends AbstractLogEmitter {

    public enum TaskState {
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
    private final String _name;
    private final Thread _thread;
    private final TaskInterruptor<T> _interruptor;


    // callbacks
    private ActionCallback<T> _callback_action;
    private ErrorCallback _callback_error;
    private ResultCallback<T> _callback_result;
    private ExitCallback<T> _callback_exit;

    private boolean _debug = false;
    private boolean _running;
    private Exception _error;
    private T _data;
    private TaskState _state;
    private long _execution_timeout;
    private int _initial_delay;
    private int _sleep;

    private long _time_start;
    private long _time_end;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Task() {
        this(null);
    }

    public Task(final ActionCallback<T> callback) {
        this(null, callback);
    }

    public Task(final ActionCallback<T> callback, final int timeout) {
        this(null, callback, timeout);
    }

    public Task(final String name, final ActionCallback<T> callback) {
        this(name, callback, 0);
    }

    public Task(final String name, final ActionCallback<T> callback, final int timeout) {
        this(name, callback, 0, 100, timeout);
    }

    public Task(final String name, final ActionCallback<T> callback,
                final int initialDelay, final int sleep, final int timeout) {
        _name = null != name ? name : RandomUtils.randomUUID();
        _callback_action = callback;
        _error = null;
        _state = TaskState.NEW;
        _id = RandomUtils.getTimeBasedRandomLong(true, true);

        _initial_delay = initialDelay;
        _sleep = sleep;
        _execution_timeout = timeout;

        _interruptor = new TaskInterruptor<T>();
        _thread = new InternalJob<T>(this);

        _running = false;

        this.log("constructor", this.toString());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("name=").append(_name);
        sb.append(", ");
        sb.append("id=").append(_id);
        sb.append("}");
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Long getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public TaskState getState() {
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
        return this.run(null);
    }

    protected Task<T> run(final ActionCallback<T> callback_action) {
        if (null != callback_action) {
            _callback_action = callback_action;
        }
        if (!this.threadRunning()) {
            this.threadRun();
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

    private boolean threadRunning() {
        return _running || (null != _thread && _thread.isAlive() && !_thread.isInterrupted());
    }

    private void threadRun() {
        _running = true;
        if (null == _callback_action) {
            _interruptor.fail(new Exception("Missing action."));
        } else {
            _thread.start();
        }
    }

    private void threadStop() {
        _state = TaskState.TERMINATED;
        if (null != _thread) {
            if (_thread.isAlive() && !_thread.isInterrupted()) {
                _thread.interrupt();
            }
        }
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
    }

    private void success(final T data) {
        _data = data;
        try {
            if (null != _callback_result) {
                _callback_result.handle(data);
            }
        } catch (Throwable ignored) {
        }
    }

    private void log(final String methodName, final String message) {
        if (_debug) {
            super.info(methodName, message);
        }
    }

    // ------------------------------------------------------------------------
    //                      I N N E R
    // ------------------------------------------------------------------------

    @FunctionalInterface
    public interface ActionCallback<T> {
        void handle(final TaskInterruptor<T> interruptor) throws Exception;
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

    public static class TaskInterruptor<T> {

        private Exception _error;
        private T _data;
        private boolean _stopped = false;


        private TaskInterruptor() {

        }

        //-- PROPERTIES --//

        private Exception error() {
            return _error;
        }

        private T data() {
            return _data;
        }

        public synchronized boolean finished() {
            return _stopped;
        }

        //-- CALLBACK --//

        public synchronized void fail(final String cause) {
            this.fail(new Exception(cause));
        }

        public synchronized void fail(final String cause, final T data) {
            this.fail(new Exception(cause), data);
        }

        public synchronized void fail(final Throwable cause) {
            this.fail(new Exception(cause));
        }

        public synchronized void fail(final Throwable cause, final T data) {
            this.fail(new Exception(cause), data);
        }

        public synchronized void fail(final Exception cause) {
            this.fail(cause, null);
        }

        public synchronized void fail(final Exception cause, final T data) {
            _error = cause;
            _data = data;
            _stopped = true;
        }

        public synchronized void success(final T data) {
            _data = data;
            _stopped = true;
        }

    }

    // --------------------------------------------------------------------
    //               E M B E D D E D
    // --------------------------------------------------------------------

    private static class InternalJob<T>
            extends Thread {

        private Task<T> _owner;
        private TaskInterruptor<T> _interruptor;

        public InternalJob(final Task<T> task) {
            super();
            _owner = task;
            _interruptor = _owner._interruptor;
        }

        @Override
        public String toString() {
            return "id:" + _owner.getId();
        }

        @Override
        public void run() {
            try {
                _owner._state = TaskState.RUNNABLE;
                _owner._time_start = System.currentTimeMillis();

                // initial delay
                if (_owner._initial_delay > 0) {
                    Thread.sleep(_owner._initial_delay);
                }

                Async.invoke((args) -> {
                    try {
                        _owner._callback_action.handle(_interruptor);
                    } catch (Exception ex_error) {
                        _interruptor.fail(ex_error);
                    }
                });

                // wait finish
                while (!_interruptor.finished() && !_owner._state.equals(TaskState.TERMINATED) && _owner.threadRunning()) {

                    _owner.log("LOOP", _owner.toString());

                    // run delay
                    if (_owner._sleep > 0) {
                        Thread.sleep(_owner._sleep);
                    }

                    // timeout
                    if (_owner._execution_timeout > 0 && System.currentTimeMillis() - _owner._time_start > _owner._execution_timeout) {
                        _interruptor.fail(new TimeoutException("Task Action timeout ms: " + _owner._execution_timeout));
                    }
                }

            } catch (Exception t) {
                _owner._error = t;
            } finally {

                _owner.log("EXIT LOOP", _owner.toString());

                _owner._time_end = System.currentTimeMillis();
                _owner._error = _interruptor.error();
                _owner._data = _interruptor.data();

                if (null != _owner._error) {
                    _owner.fail(_owner._error, _owner._data);
                } else {
                    _owner.success(_owner._data);
                }

                // invoke exit callback if any
                if (null != _owner._callback_exit) {
                    _owner._callback_exit.handle(_owner._error, _owner._data);
                }

                _owner.threadStop();
            }
        }
    }

}
