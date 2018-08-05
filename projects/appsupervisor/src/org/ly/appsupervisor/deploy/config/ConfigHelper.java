package org.ly.appsupervisor.deploy.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.appsupervisor.app.model.ModelLauncher;
import org.lyj.Lyj;
import org.lyj.commons.io.jsonrepository.JsonRepository;
import org.lyj.commons.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Direct access to configuration structure
 */
public class ConfigHelper {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    private static final String TASK_ENABLED = "lyj.task_enabled";
    private static final String TASK_INTERVAL_SEC = "lyj.task_interval_sec";

    private static final String LAUNCHER = "launcher";
    private static final String LAUNCHER_EXEC = "launcher.exec";
    private static final String LAUNCHER_RULES = "launcher.rules";
    private static final String LAUNCHER_ACTIONS = "launcher.actions";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonRepository _configuration;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ConfigHelper() {
        _configuration = Lyj.getConfiguration();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c   ( a p i )
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p u b l i c   ( w e b )
    // ------------------------------------------------------------------------

    public boolean taskEnabled() {
        return _configuration.getBoolean(TASK_ENABLED, true);
    }

    public int taskInterval() {
        return _configuration.getInt(TASK_INTERVAL_SEC, 60 * 10);
    }

    public Map<String, ModelLauncher> launchers() {
        final Map<String, ModelLauncher> launchers = new HashMap<>();
        final JSONObject config = _configuration.getJSONObject(LAUNCHER);
        if (config.has("launchers")) {
            final JSONArray array = config.optJSONArray("launchers");
            CollectionUtils.forEach(array, (item) -> {
                final ModelLauncher launcher = new ModelLauncher(item);
                launchers.put(launcher.uid(), launcher);
            });
        } else {
            final ModelLauncher launcher = new ModelLauncher(config);
            launchers.put(launcher.uid(), launcher);
        }
        return launchers;
    }

    // ------------------------------------------------------------------------
    //                     S T A T I C
    // ------------------------------------------------------------------------

    private static ConfigHelper __instance;

    public static ConfigHelper instance() {
        if (null == __instance) {
            __instance = new ConfigHelper();
        }
        return __instance;
    }

}
