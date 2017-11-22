package org.lyj.ext.db.configuration;

import org.lyj.commons.util.json.JsonItem;

/**
 * host item
 */
public class DatabaseConfigurationCredential
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String TYPE = "type"; // auth mode or credential type
    private static final String DATABASE = "database"; // usually a database name
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DatabaseConfigurationCredential() {
        super();
    }

    public DatabaseConfigurationCredential(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String type() {
        return super.getString(TYPE);
    }

    public DatabaseConfigurationCredential type(final String value) {
        super.put(TYPE, value);
        return this;
    }

    public String database() {
        return super.getString(DATABASE);
    }

    public DatabaseConfigurationCredential database(final String value) {
        super.put(DATABASE, value);
        return this;
    }

    public String username() {
        return super.getString(USERNAME);
    }

    public DatabaseConfigurationCredential username(final String value) {
        super.put(USERNAME, value);
        return this;
    }

    public String password() {
        return super.getString(PASSWORD);
    }

    public DatabaseConfigurationCredential password(final String value) {
        super.put(PASSWORD, value);
        return this;
    }

}
