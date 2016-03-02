package org.lyj.automator.app.controllers.projects.modules.impl.generator;

import org.json.JSONObject;
import org.lyj.automator.app.controllers.projects.modules.AbstractModule;
import org.lyj.automator.app.controllers.projects.modules.AbstractModuleExecutor;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.async.future.Timed;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Fixed Rate generator.
 * Generates task at fixed rate.
 */
public class ModFixedRate
        extends AbstractModule {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH_UNIT = "unit";
    private static final String PATH_DELAY = "delay";
    private static final String PATH_COUNT = "count"; // num threads
    private static final String PATH_REPEAT = "repeat"; // num cicles

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModFixedRate(final JSONObject json) {
        super(json);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String getUnit() {
        return super.json().deepString(PATH_UNIT, DateUtils.sMILLISECOND);
    }

    public ModFixedRate setUnit(final String value) {
        super.json().putDeep(PATH_UNIT, value);
        return this;
    }

    public ModFixedRate setUnit(final int value) {
        super.json().putDeep(PATH_UNIT, DateUtils.UnitFromInt(value));
        return this;
    }

    public long getDelay() {
        return super.json().deepLong(PATH_DELAY);
    }

    public ModFixedRate setDelay(final long value) {
        super.json().putDeep(PATH_DELAY, value);
        return this;
    }

    public int getCount() {
        return super.json().deepInteger(PATH_COUNT);
    }

    public ModFixedRate setCount(final int value) {
        super.json().putDeep(PATH_COUNT, value);
        return this;
    }

    public int getRepeat() {
        return super.json().deepInteger(PATH_REPEAT, 1);
    }

    public ModFixedRate setRepeat(final int value) {
        super.json().putDeep(PATH_REPEAT, value);
        return this;
    }

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
                //-- execution implementation --//
                final ModFixedRate module = (ModFixedRate) super.module();
                final TimeUnit unit = DateUtils.TimeUnitFromString(module.getUnit());
                final long delay = module.getDelay();
                final int count = module.getCount();
                final int repeat = module.getRepeat();
                final AbstractModule[] next = super.next();

                final Timed timer = new Timed(unit, 0, delay, 0, repeat);
                final List<Task<Object>> tasks = new ArrayList<>();
                timer.start((timer_interruptor) -> {
                    for (int i = 0; i < count; i++) {
                        tasks.add(new Task<Object>((t) -> {
                            try {

                                Async.joinAll( super.doNext(timer_interruptor) );

                                t.success(null);
                            } catch (Throwable err) {
                                t.fail(err);
                            }
                        }));
                    }
                    Async.joinAll(tasks);
                });
                timer.join();
                //-- execution implementation --//

                interruptor.success(true);
            } catch (Throwable t) {
                interruptor.fail(t);
            }
        }


    }


}
