package org.lyj.config.network;

import org.lyj.Lyj;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;

/**
 * Network Configuration Helper
 */
public class ConfigNetwork {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String NODE_DEFAULT = "default";

    private static final String PATH_NETWORK = "network";
    private static final String PATH_TOKEN = "token";
    private static final String PATH_HOST = "http.host";
    private static final String PATH_PORT = "http.port";
    private static final String PATH_ROOT = "http.root";

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

    public String token() {
        return this.token(null);
    }

    public String token(final String node) {
        return _network.deepString(this.path(node, PATH_TOKEN));
    }

    public String host() {
        return this.host(null);
    }

    public String host(final String node) {
        return _network.deepString(this.path(node, PATH_HOST));
    }

    public int port() {
        return this.port(null);
    }

    public int port(final String node) {
        return _network.deepInteger(this.path(node, PATH_PORT));
    }

    public String pathRoot() {
        return this.pathRoot(null);
    }

    public String pathRoot(final String node) {
        return _network.deepString(this.path(node, PATH_ROOT));
    }

    public String connectionString() {
        return this.host() + ":" + this.port();
    }

    public String connectionString(final String node) {
        return this.host(node) + ":" + this.port(node);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected String path(final String name) {
        return this.path(null, name);
    }

    protected String path(final String node, final String name) {
        return StringUtils.hasText(node) ? node + "." + name : NODE_DEFAULT + "." + name;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ConfigNetwork __instance;

    public static synchronized ConfigNetwork getInstance() {
        if (null == __instance) {
            __instance = new ConfigNetwork();
        }
        return __instance;
    }

}
