package org.lyj.automator.app.controllers.projects.modules.impl.output;

import org.json.JSONObject;
import org.lyj.automator.app.controllers.projects.modules.AbstractModule;
import org.lyj.automator.app.controllers.projects.modules.AbstractModuleExecutor;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.JsonWrapper;

import java.util.Map;

/**
 * System.out Output
 * <p>
 * Write output in System.out
 */
public class ModSystemOut
        extends AbstractModule {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModSystemOut(final JSONObject json) {
        super(json);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public Task<Object> run(final Object input) {
        return this.runThis(input);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }

    private Task<Object> runThis(final Object input) {
        final AbstractModule[] next = super.next();

        return new Executor(this, input).run();
    }

    // ------------------------------------------------------------------------
    //                      I N N E R
    // ------------------------------------------------------------------------

    private static class Executor
            extends AbstractModuleExecutor {

        public Executor(final AbstractModule module,
                        final Object input) {
            super(module, input);
        }

        @Override
        protected void execute(final TaskInterruptor<Object> interruptor) throws Exception {
            try {
                System.out.println(this.getMessage(super.input()));
                interruptor.success(true);
            } catch (Throwable t) {
                interruptor.fail(t);
            }
        }

        private String getMessage(final Object input) {
            final StringBuilder sb = new StringBuilder();
            sb.append("[").append(FormatUtils.formatDate(DateUtils.now(), "yyyy-MM-dd hh:mm:ss")).append("] ");
            if (null != input) {
                sb.append(input.toString());
            }
            return sb.toString();
        }

    }

}
