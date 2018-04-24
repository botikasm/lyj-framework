package org.ly.licensemanager.deploy.config;

import org.lyj.commons.util.json.JsonItem;

/**
 * mail.smtp helper
 * <p>
 * {
 * "enabled": true,
 * "connection": {
 * "host": "SSL0.OVH.NET",
 * "port": "465",
 * "is_tls": true,
 * "is_ssl": true,
 * "username": "bot@botfarmy.com",
 * "password": "!qaz2WSX098"
 * },
 * "info": {
 * "admin_addresses": [
 * "angelo.geminiani@funnygain.com"
 * ],
 * "from":"Funny Gain<support@funnygain.com>",
 * "reply_to":"Funny Gain<support@funnygain.com>"
 * }
 * }
 */
public class ConfigMailSmtp extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    private static final String FLD_ENABLED = "enabled";
    private static final String FLD_INFO_FROM = "info.from";
    private static final String FLD_INFO_REPLY_TO = "info.reply_to";

    private static final String FLD_CONNECTION_HOST = "connection.host";
    private static final String FLD_CONNECTION_PORT = "connection.port";
    private static final String FLD_CONNECTION_USER = "connection.username";
    private static final String FLD_CONNECTION_PSW = "connection.password";
    private static final String FLD_CONNECTION_IS_TLS = "connection.is_tls";
    private static final String FLD_CONNECTION_IS_SSL = "connection.is_ssl";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ConfigMailSmtp(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean enabled() {
        return super.getBoolean(FLD_ENABLED);
    }

    public String infoFrom() {
        return super.getString(FLD_INFO_FROM);
    }

    public String infoReplyTo() {
        return super.getString(FLD_INFO_REPLY_TO);
    }

    public String connectionHost() {
        return super.getString(FLD_CONNECTION_HOST);
    }

    public int connectionPort() {
        return super.getInt(FLD_CONNECTION_PORT);
    }

    public String connectionUsername() {
        return super.getString(FLD_CONNECTION_USER);
    }

    public String connectionPassword() {
        return super.getString(FLD_CONNECTION_PSW);
    }

    public boolean connectionIsSSLS() {
        return super.getBoolean(FLD_CONNECTION_IS_SSL);
    }

    public boolean connectionIsTLS() {
        return super.getBoolean(FLD_CONNECTION_IS_TLS);
    }

}


