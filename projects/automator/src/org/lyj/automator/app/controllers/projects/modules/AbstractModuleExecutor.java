package org.lyj.automator.app.controllers.projects.modules;

import org.lyj.commons.async.future.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap module execution
 */
public abstract class AbstractModuleExecutor
        extends Task<Object> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private AbstractModule _module;
    private Object _input;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractModuleExecutor(final AbstractModule module,
                                  final Object input) {
        _module = module;
        _input = input;
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    @Override
    public AbstractModuleExecutor run(final ActionCallback<Object> callback) {
        super.run(this::execute);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    protected abstract void execute(final TaskInterruptor<Object> interruptor) throws Exception;

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected Object input() {
        return _input;
    }

    protected AbstractModule module() {
        return _module;
    }

    protected AbstractModule[] next() {
        if (null != _module) {
            return _module.next();
        }
        return new AbstractModule[0];
    }

    protected List<Task<Object>> doNext(final Object input) {
        final List<Task<Object>> response = new ArrayList<>();
        final AbstractModule[] modules = this.next();
        for (final AbstractModule module : modules) {
            response.add(module.run(input));
        }
        return response;
    }
}
