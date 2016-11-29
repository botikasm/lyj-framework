package org.lyj.ext.db.configuration;

import org.json.JSONObject;
import org.lyj.commons.util.JsonItem;

/**
 * host item
 */
public class DatabaseConfigurationHost
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH = "path"; // used for file databases
    private static final String HOST = "host";
    private static final String PORT = "port";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DatabaseConfigurationHost() {
        super();
    }

    public DatabaseConfigurationHost(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String path() {
        return super.getString(PATH);
    }

    public DatabaseConfigurationHost path(final String value) {
        super.put(PATH, value);
        return this;
    }

    public String host() {
        return super.getString(HOST);
    }

    public DatabaseConfigurationHost host(final String value) {
        super.put(HOST, value);
        return this;
    }

    public int port() {
        return super.getInt(PORT);
    }

    public DatabaseConfigurationHost port(final int value) {
        super.put(PORT, value);
        return this;
    }

}
