package org.ly.ose.server.application.controllers.email.pop;

import org.json.JSONObject;
import org.lyj.Lyj;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.json.JsonWrapper;

public class MailConfigPop
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _enabled;
    private String _server;
    private int _port;
    private String _email;
    private String _user;
    private String _psw;
    private String _subject;
    private String[] _authorized;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * @param config_path i.e. "mail.client"
     */
    public MailConfigPop(final String config_path) {
        this.init(config_path);
    }

    public MailConfigPop(final JSONObject config) {
        this.init(config);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean enabled() {
        return _enabled;
    }

    public String server() {
        return _server;
    }

    public int port() {
        return _port;
    }

    public String email() {
        return _email;
    }

    public String user() {
        return _user;
    }

    public String psw() {
        return _psw;
    }

    public String subject() {
        return _subject;
    }

    public String[] authorized() {
        return _authorized;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final String config_path) {
        try {
            this.init(Lyj.getConfiguration(true).getJSONObject(config_path));
        } catch (Throwable t) {
            super.error("init", t);
        }
    }

    private void init(final JSONObject raw_config) {
        try {
            final JsonWrapper config = new JsonWrapper(raw_config);

            _enabled = config.deepBoolean("enabled");
            _server = config.deepString("pop3.server");
            _port = config.deepInteger("pop3.port");
            _email = config.deepString("pop3.email");
            _user = config.deepString("pop3.user");
            _psw = config.deepString("pop3.psw");
            _subject = config.deepString("subject");
            _authorized = JsonWrapper.toArrayOfString(config.deepJSONArray("authorized"));

        } catch (Throwable t) {
            super.error("init", t);
        }
    }


}
