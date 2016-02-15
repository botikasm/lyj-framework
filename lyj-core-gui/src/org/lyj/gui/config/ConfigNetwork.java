package org.lyj.gui.config;

import org.lyj.Lyj;
import org.lyj.commons.util.JsonWrapper;

/**
 * Configuration Helper
 */
public class ConfigNetwork {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH_NETWORK = "network";
    private static final String PATH_HOST = "http.host";
    private static final String PATH_PORT = "http.port";
    private static final String PATH_PATH_API = "http.path_api";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonWrapper _network;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ConfigNetwork() {
        _network = new JsonWrapper(Lyj.getConfiguration().getJSONObject(PATH_NETWORK));
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String host() {
        return _network.deepString(PATH_HOST);
    }

    public int port() {
        return _network.deepInteger(PATH_PORT);
    }

    public String connectionString() {
        return this.host() + ":" + this.port();
    }

    public String pathApi(){
        return _network.deepString(PATH_PATH_API);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ConfigNetwork __instance;

    public static ConfigNetwork getInstance() {
        if (null == __instance) {
            __instance = new ConfigNetwork();
        }
        return __instance;
    }

}
