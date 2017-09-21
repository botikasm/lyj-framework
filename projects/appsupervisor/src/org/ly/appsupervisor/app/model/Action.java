package org.ly.appsupervisor.app.model;

import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.JsonWrapper;

public class Action
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s
    // ------------------------------------------------------------------------

    public static final String COMMAND_START = "start"; // run application
    public static final String COMMAND_STOP = "stop";   // stop application

    private static final String FLD_COMMANDS = "commands"; // commands to execute

    private static final String FLD_EMAIL_TARGET = "email.target"; // array of addresses
    private static final String FLD_EMAIL_MESSAGE = "email.message";
    private static final String FLD_EMAIL_CONNECTION_HOST = "email.connection.host";
    private static final String FLD_EMAIL_CONNECTION_PORT = "email.connection.port";
    private static final String FLD_EMAIL_CONNECTION_IS_TLS = "email.connection.is_tls";
    private static final String FLD_EMAIL_CONNECTION_IS_SSL = "email.connection.is_ssl";
    private static final String FLD_EMAIL_CONNECTION_USERNAME = "email.connection.username";
    private static final String FLD_EMAIL_CONNECTION_PASSWORD = "email.connection.password";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Action() {
        super();
    }

    public Action(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String[] commands() {
        return JsonWrapper.toArrayOfString(super.getJSONArray(FLD_COMMANDS));
    }

    public String[] emailTarget() {
        return JsonWrapper.toArrayOfString(super.getJSONArray(FLD_EMAIL_TARGET));
    }

    public String emailMessage() {
        return super.getString(FLD_EMAIL_MESSAGE);
    }

    public String emailConnectionHost() {
        return super.getString(FLD_EMAIL_CONNECTION_HOST);
    }

    public int emailConnectionPort() {
        return super.getInt(FLD_EMAIL_CONNECTION_PORT);
    }

    public boolean emailConnectionIsTls() {
        return super.getBoolean(FLD_EMAIL_CONNECTION_IS_TLS);
    }

    public boolean emailConnectionIsSsl() {
        return super.getBoolean(FLD_EMAIL_CONNECTION_IS_SSL);
    }

    public String emailConnectionUsername() {
        return super.getString(FLD_EMAIL_CONNECTION_USERNAME);
    }

    public String emailConnectionPassword() {
        return super.getString(FLD_EMAIL_CONNECTION_PASSWORD);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
