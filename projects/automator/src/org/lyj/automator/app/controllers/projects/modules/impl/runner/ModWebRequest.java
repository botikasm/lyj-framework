package org.lyj.automator.app.controllers.projects.modules.impl.runner;

import org.json.JSONObject;
import org.lyj.automator.app.controllers.projects.modules.AbstractModule;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.async.future.Timed;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.JsonWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Web Request Runner
 * Run a web request using POST or GET methods
 */
public class ModWebRequest
        extends AbstractModule {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH_METHOD = "method";
    private static final String PATH_URL = "url";
    private static final String PATH_PARAMS = "params";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModWebRequest(final JSONObject json) {
        super(json);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String getMethod() {
        return super.json().deepString(PATH_METHOD, "GET");
    }

    public ModWebRequest setMethod(final String value) {
        super.json().putDeep(PATH_METHOD, value);
        return this;
    }

    public String getUrl() {
        return super.json().deepString(PATH_URL);
    }

    public ModWebRequest setUrl(final String value) {
        super.json().putDeep(PATH_URL, value);
        return this;
    }

    public JSONObject getParams() {
        return super.json().deepJSONObject(PATH_PARAMS);
    }

    public Map<String, String> getParamsMap() {
        return JsonWrapper.toMapOfString(this.getParams());
    }

    public ModWebRequest setParams(final Map<String, String> value) {
        return this.setParams(new JSONObject(value));
    }

    public ModWebRequest setParams(final JSONObject value) {
        super.json().putDeep(PATH_PARAMS, value);
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
        final AbstractModule[] next = super.next();

        final Task<Object> execution = new Task<Object>((exec) -> {
            try {
                //-- execution implementation --//
                for (final AbstractModule next_module : next) {

                    next_module.run(this.getUrl()).get();

                }
                //-- execution implementation --//

                exec.success(true);
            } catch (Throwable t) {
                exec.fail(t);
            }
        });
        return execution.run();
    }


}
