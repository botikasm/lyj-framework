package org.ly.licensemanager.deploy.config;

import org.lyj.Lyj;
import org.lyj.commons.io.jsonrepository.JsonRepository;

/**
 * Direct access to configuration structure
 */
public class ConfigHelper {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ENABLE_TASK = "lyj.enable_task";

    private static final String API_PORT = "api.http.port";
    private static final String API_ENABLED = "api.http.enabled";
    private static final String API_HOST = "api.http.host";
    private static final String API_USE_SSL = "api.http.use_ssl";

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
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean enableTask() {
        return _configuration.getBoolean(ENABLE_TASK, true);
    }

    // ------------------------------------------------------------------------
    //                      a p i
    // ------------------------------------------------------------------------

    public boolean apiEnabled() {
        return _configuration.getBoolean(API_ENABLED);
    }

    public String apiHost() {
        return _configuration.getString(API_HOST);
    }

    public int apiPort() {
        return _configuration.getInt(API_PORT);
    }

    public boolean apiUseSSL() {
        return _configuration.getBoolean(API_USE_SSL, true);
    }

    // ------------------------------------------------------------------------
    //                     S T A T I C
    // ------------------------------------------------------------------------

    private static ConfigHelper __instance;

    public static synchronized ConfigHelper instance() {
        if (null == __instance) {
            __instance = new ConfigHelper();
        }
        return __instance;
    }

}
