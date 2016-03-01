package org.lyj.automator.app.controllers.projects.modules;

import org.json.JSONObject;
import org.lyj.automator.app.controllers.projects.modules.impl.generator.ModFixedRate;
import org.lyj.automator.app.controllers.projects.modules.impl.output.ModSystemOut;
import org.lyj.automator.app.controllers.projects.modules.impl.runner.ModWebRequest;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * factory for modules.
 */
public final class ModuleFactory {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH_TYPE = "type";

    // generator
    private static final String TYPE_GEN_FIXED_RATE = "FixedRate";
    // runner
    private static final String TYPE_RUN_WEB_REQUEST = "WebRequest";
    // outputs
    private static final String TYPE_OUT_SYSTEM_OUTPUT = "SystemOut";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Class<? extends AbstractModule>> _modules;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ModuleFactory() {
        _modules = new HashMap<>();

        // generators
        this.register(TYPE_GEN_FIXED_RATE, ModFixedRate.class);

        // runners
        this.register(TYPE_RUN_WEB_REQUEST, ModWebRequest.class);

        // outputs
        this.register(TYPE_OUT_SYSTEM_OUTPUT, ModSystemOut.class);

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void register(final String typeName, final Class<? extends AbstractModule> typeClass) {
        _modules.put(typeName, typeClass);
    }

    public AbstractModule create(final JSONObject json) throws Exception {
        final String type = JsonWrapper.getString(json, PATH_TYPE);
        if(StringUtils.hasText(type)) {
            try {
                final Class<? extends AbstractModule> moduleClass = _modules.get(type);
                if (null != moduleClass) {
                    return (AbstractModule) ClassLoaderUtils.newInstance(moduleClass, new Object[]{json});
                }
            }catch(Throwable t){
                throw new Exception(FormatUtils.format("Unable to create Module of Type '%s'", type), t);
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ModuleFactory __instance;

    public static ModuleFactory getInstance() {
        if (null == __instance) {
            __instance = new ModuleFactory();
        }
        return __instance;
    }
}
