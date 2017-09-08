package org.ly.applauncher.deploy.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.Lyj;
import org.lyj.commons.io.jsonrepository.JsonRepository;

/**
 * Direct access to configuration structure
 */
public class ConfigHelper {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    private static final String TASK_ENABLED = "lyj.task_enabled";
    private static final String TASK_INTERVAL_SEC = "lyj.task_interval_sec";

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

    public String launcherExec() {
        return _configuration.getString(LAUNCHER_EXEC);
    }

    public JSONArray rules() {
        return _configuration.getJSONArray(LAUNCHER_RULES);
    }

    public JSONObject actions() {
        return _configuration.getJSONObject(LAUNCHER_ACTIONS);
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
