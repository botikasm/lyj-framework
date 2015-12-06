package org.lyj.commons.async.future;

import org.lyj.commons.Delegates;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * functional async action
 */
public class Task<T> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final ExecutorService _executor;

    // callbacks
    private ActionCallback _callback_action;
    private ErrorCallback _callback_error;
    private ResultCallback<T> _callback_result;

    private Exception _error;
    private T _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Task(final ActionCallback callback) {
        _executor = Executors.newSingleThreadExecutor();
        _callback_action = callback;
        _error = null;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    //-- THREAD --//

    public Task<T> run() {
        final Task self = this;

        // creates internal infinite loop
        _executor.submit((Runnable) () -> {
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
        });

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

    //-- CALLBACK --//

    public void fail(final String cause) {
        this.fail(new Exception(cause));
    }

    public void fail(final Throwable cause) {
        this.fail(new Exception(cause));
    }

    public void fail(final Exception cause) {
        _error = cause;
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

    public interface ActionCallback extends Delegates.Handler {
        void handle(final Task self) throws Exception;
    }

    public interface ResultCallback<T> extends Delegates.Handler {
        void handle(final T data);
    }

    public interface ErrorCallback extends Delegates.Handler {
        void handle(final Exception err);
    }

}
