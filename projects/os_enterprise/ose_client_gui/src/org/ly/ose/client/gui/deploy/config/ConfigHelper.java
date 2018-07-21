package org.ly.ose.client.gui.deploy.config;

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

    private static final String ENABLE_TASK = "lyj.enable_task";
    private static final String WEB_SERVER_HOST = "lyj.web_server_host";

    private static final String API_PORT = "api.http.port";
    private static final String API_ENABLED = "api.http.enabled";
    private static final String API_HOST = "api.http.host";
    private static final String API_USE_SSL = "api.http.use_ssl";

    private static final String MAIL_SMTP = "mail.smtp";
    private static final String MAIL_CLIENT = "mail.client";


    private static final String VFS_CLOUD_FS = "vfs.cloud_fs";

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

    public String webServerHost() { return _configuration.getString(WEB_SERVER_HOST, ""); }


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
    //                      m a i l
    // ------------------------------------------------------------------------

    public JSONObject mailStmp() {
        return _configuration.getJSONObject(MAIL_SMTP);
    }

    public JSONObject mailClient() {
        return _configuration.getJSONObject(MAIL_CLIENT);
    }

    // ------------------------------------------------------------------------
    //                      c l o u d   f s
    // ------------------------------------------------------------------------

    public JSONObject vfsCloudFS() {
        return _configuration.getJSONObject(VFS_CLOUD_FS);
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
