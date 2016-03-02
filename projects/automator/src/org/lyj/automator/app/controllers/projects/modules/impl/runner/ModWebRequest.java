package org.lyj.automator.app.controllers.projects.modules.impl.runner;

import org.json.JSONObject;
import org.lyj.automator.app.controllers.projects.modules.AbstractModule;
import org.lyj.automator.app.controllers.projects.modules.AbstractModuleExecutor;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.async.future.Timed;
import org.lyj.commons.network.http.client.HttpClient;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FormatUtils;
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

    public Map<String, Object> getParamsMap() {
        return JsonWrapper.toMap(this.getParams());
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
                final ModWebRequest module = (ModWebRequest) super.module();
                Object output;

                //-- execution implementation --//
                final String method = module.getMethod().toLowerCase();
                final String url = module.getUrl();
                final Map<String, Object> params = module.getParamsMap();

                Task<String> task = new Task<>((t) -> {
                    HttpClient http = new HttpClient();
                    if (HttpClient.isPOST(method)) {
                        http.post(url, params, (err, response) -> {
                            if (null != err) {
                                t.fail(err);
                            } else {
                                t.success(response);
                            }
                        });
                    } else {
                        http.get(url, params, (err, response) -> {
                            if (null != err) {
                                t.fail(err);
                            } else {
                                t.success(response);
                            }
                        });
                    }
                });
                try {
                    output = task.run().get();
                }catch(Throwable t){
                    output = t;
                }
                //-- execution implementation --//

                Async.joinAll( super.doNext(output) );


                interruptor.success(true);
            } catch (Throwable t) {
                interruptor.fail(t);
            }
        }


    }

}
