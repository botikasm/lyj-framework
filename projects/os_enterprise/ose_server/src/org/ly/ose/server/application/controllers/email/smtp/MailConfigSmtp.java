package org.ly.ose.server.application.controllers.email.smtp;

import org.json.JSONObject;
import org.lyj.Lyj;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.json.JsonWrapper;

/**
 * Server SMTP
 * {
 * * "enabled": true,
 * * "connection": {
 * * "host": "SSL0.OVH.NET",
 * * "port": "465",
 * * "is_tls": true,
 * * "is_ssl": true,
 * * "username": "xxxx",
 * * "password": "xxxx"
 * * },
 * * "info": {
 * * "admin_addresses": [
 * * "xxxxxx"
 * * ],
 * * "from":"3botika<xxxx>",
 * * "reply_to":"3botinka<xxxx>"
 * * }
 * * }
 */
public class MailConfigSmtp
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t a n t s
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
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _enabled;

    private String _info_from;
    private String _info_reply_to;

    private String _connection_host;
    private int _connection_port;
    private String _connection_username;
    private String _connection_password;
    private boolean _connection_is_ssl;
    private boolean _connection_is_tls;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * @param config_path i.e. "mail.smtp"
     */
    public MailConfigSmtp(final String config_path) throws Exception {
        this.init(config_path);
    }

    public MailConfigSmtp(final JSONObject config) throws Exception {
        this.init(config);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean enabled() {
        return _enabled;
    }

    public String infoFrom() {
        return _info_from;
    }

    public String infoReplyTo() {
        return _info_reply_to;
    }

    public String connectionHost() {
        return _connection_host;
    }

    public int connectionPort() {
        return _connection_port;
    }

    public String connectionUsername() {
        return _connection_username;
    }

    public String connectionPassword() {
        return _connection_password;
    }

    public boolean connectionIsSSLS() {
        return _connection_is_ssl;
    }

    public boolean connectionIsTLS() {
        return _connection_is_tls;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final String config_path) throws Exception {
        try {
            this.init(Lyj.getConfiguration(true).getJSONObject(config_path));
        } catch (Exception err) {
            _enabled = false;
            throw err;
        }
    }

    private void init(final JSONObject raw_config) throws Exception {
        try {
            final JsonWrapper config = new JsonWrapper(raw_config);
            _enabled = config.deepBoolean(FLD_ENABLED);

            _info_from = config.deepString(FLD_INFO_FROM);
            _info_reply_to = config.deepString(FLD_INFO_REPLY_TO);

            _connection_host = config.deepString(FLD_CONNECTION_HOST);
            _connection_port = config.deepInteger(FLD_CONNECTION_PORT);
            _connection_username = config.deepString(FLD_CONNECTION_USER);
            _connection_password = config.deepString(FLD_CONNECTION_PSW);
            _connection_is_ssl = config.deepBoolean(FLD_CONNECTION_IS_SSL);
            _connection_is_tls = config.deepBoolean(FLD_CONNECTION_IS_TLS);
        } catch (Exception err) {
            _enabled = false;
            throw err;
        }
    }
}
