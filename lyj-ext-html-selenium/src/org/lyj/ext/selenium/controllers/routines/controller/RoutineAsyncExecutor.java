package org.lyj.ext.selenium.controllers.routines.controller;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.selenium.IConstants;
import org.lyj.ext.selenium.controllers.routines.model.ModelPackage;

public class RoutineAsyncExecutor
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _selenium_root;
    private final String _root;
    private final ModelPackage _info;
    private final Thread _thread;

    private Delegates.Callback<RoutineAsyncExecutor> _callback_task_terminated;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RoutineAsyncExecutor(final String selenium_root,
                                final ModelPackage info) {
        _selenium_root = selenium_root;
        _root = IConstants.PATH_ROUTINES;
        _info = info;
        _thread = Async.wrap(this::threadRun, this); // async task
        _thread.setName("task_script_" + info.name());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public void run(final Delegates.Callback<RoutineAsyncExecutor> callback_task_terminated) {
        _callback_task_terminated = callback_task_terminated;
        if (null != _thread) {
            _thread.start();
        }
    }

    /**
     * Force close of current task
     */
    public void stop() {
        if (null != _thread) {
            _thread.interrupt();
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    /**
     * Thread execution context
     *
     * @param args (optional)
     */
    private void threadRun(final Object[] args) {
        if (StringUtils.hasText(_selenium_root) && null != _info && _info.enabled()) {
            // synchronous execution
            RoutineSyncTask.run(_root, _info);
        }

        // exit thread
        if (null != _callback_task_terminated) {
            Delegates.invoke(_callback_task_terminated, this);
        }
    }


}
