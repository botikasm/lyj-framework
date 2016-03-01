package org.lyj.automator.app.controllers.projects;

import org.json.JSONObject;
import org.lyj.automator.app.controllers.projects.modules.AbstractModule;
import org.lyj.automator.app.controllers.projects.modules.ModuleFactory;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;

/**
 * Project object.
 */
public class Project
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH_MODULE = "module";
    private static final String PATH_ENABLED = "enabled";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _name;
    private JsonWrapper _json;
    private AbstractModule _module;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Project() throws Exception {
        this("");
    }

    public Project(final String json) throws Exception {
        _json = StringUtils.isJSON(json) ? new JsonWrapper(json) : new JsonWrapper("");

        this.init();
    }

    @Override
    public String toString() {
        return _json.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JsonWrapper json() {
        return _json;
    }

    public String getName() {
        return _name;
    }

    public Project setName(final String value) {
        _name = value;
        return this;
    }

    public AbstractModule module() {
        return _module;
    }

    public boolean isEnabled() {
        return _json.deepBoolean(PATH_ENABLED, false);
    }

    public Task<Object> run(final Object input) {
        if(this.isEnabled()) {
            if (null != _module) {
                return _module.run(input);
            } else {
                super.warning("run", "No module to run in current project.");
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() throws Exception {
        JSONObject module = _json.deepJSONObject(PATH_MODULE);
        if (null == module) {
            module = new JSONObject();
            _json.putDeep(PATH_MODULE, module);
        }
        _module = ModuleFactory.getInstance().create(module);
    }

}
