package org.ly.server.deploy.config;

import org.json.JSONObject;
import org.lyj.Lyj;
import org.lyj.commons.io.jsonrepository.JsonRepository;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

/**
 * Direct access to configuration structure
 */
public class ConfigHelper {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ENABLE_TASK = "lyj.enable_task";
    private static final String WEB_SERVER_HOST = "lyj.web_server_host";

    private static final String MAIL_SMTP = "mail.smtp";
    private static final String MAIL_CLIENT = "mail.client";


    private static final String VFS_CLOUD_FS = "vfs.cloud_fs";

    private static final String API_PORT = "server_api.http.port";
    private static final String API_ENABLED = "server_api.http.enabled";
    private static final String API_HOST = "server_api.http.host";
    private static final String API_USE_SSL = "server_api.http.use_ssl";

    private static final String SOCKET_PORT = "server_socket.http.port";
    private static final String SOCKET_ENABLED = "server_socket.http.enabled";
    private static final String SOCKET_USE_SSL = "server_socket.http.use_ssl";

    private static final String WEB_ENABLED = "server_web.http.enabled";
    private static final String WEB_ROOT = "server_web.http.root";
    private static final String WEB_DOMAIN = "server_web.http.domain";
    private static final String WEB_PORT = "server_web.http.port";
    private static final String WEB_USE_SSL = "server_web.http.use_ssl";
    private static final String WEB_404 = "server_web.http.404";

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
    //                      w e b   a p i
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
    //                      w e b   s o c k e t
    // ------------------------------------------------------------------------

    public boolean socketEnabled() {
        return _configuration.getBoolean(SOCKET_ENABLED);
    }

    public int socketPort() {
        return _configuration.getInt(SOCKET_PORT);
    }

    public boolean socketUseSSL() {
        return _configuration.getBoolean(SOCKET_USE_SSL, true);
    }

    // ------------------------------------------------------------------------
    //                      w e b   s e r v e r
    // ------------------------------------------------------------------------

    public boolean webEnabled() {
        return _configuration.getBoolean(WEB_ENABLED);
    }

    public String webRoot() {
        return PathUtils.getAbsolutePath(_configuration.getString(WEB_ROOT, "./htdocs"));
    }

    public String webDomain() {
        final String domain = _configuration.getString(WEB_DOMAIN);
        final String protocol = this.webUseSSL() ? "https://" : "http://";
        final String protocol_domain = StringUtils.hasText(domain)
                ? PathUtils.hasProtocol(domain) ? domain : protocol + domain
                : "http://localhost";
        final int port = this.webPort();
        final boolean has_port = StringUtils.split(protocol_domain, ":").length == 3;
        final String protocol_domain_port = (port > 0 && port != 80)
                ? (!has_port ? protocol_domain + ":" + port : protocol_domain)
                : protocol_domain; // no port or port 80

        return protocol_domain_port;
    }

    public int webPort() {
        return _configuration.getInt(WEB_PORT);
    }

    public boolean webUseSSL() {
        return _configuration.getBoolean(WEB_USE_SSL, true);
    }

    public String web404() {
        return _configuration.getString(WEB_404);
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
