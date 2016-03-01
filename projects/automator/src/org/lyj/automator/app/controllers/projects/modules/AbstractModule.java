package org.lyj.automator.app.controllers.projects.modules;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.JsonWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Base module
 */
public abstract class AbstractModule
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH_NEXT = "next";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private JsonWrapper _json;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractModule(final JSONObject json) {
        _json = new JsonWrapper(json);
    }

    @Override
    public String toString() {
        return _json.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public abstract Task<Object> run(final Object input);

    public JsonWrapper json() {
        return _json;
    }

    public AbstractModule[] next(){
        final List<AbstractModule> response = new ArrayList<>();
        final JSONArray items = _json.deepJSONArray(PATH_NEXT);
        CollectionUtils.forEach(items, (item, key, index)->{
            try{
                if(item instanceof JSONObject) {
                    final AbstractModule module = ModuleFactory.getInstance().create((JSONObject) item);
                    if(null!=module){
                        response.add(module);
                    } else {
                        super.warning("next", FormatUtils.format("Unsupported Module Found: %s", item.toString()));
                    }
                } else {
                    super.warning("next", FormatUtils.format("Invalid Module Found: %s", item));
                }
            }catch(Throwable t){
                super.error("next", t);
            }
        });
        return response.toArray(new AbstractModule[response.size()]);
    }

}
