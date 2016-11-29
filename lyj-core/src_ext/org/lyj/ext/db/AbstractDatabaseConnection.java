package org.lyj.ext.db;

import org.json.JSONObject;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.ext.db.configuration.DatabaseConfiguration;

/**
 *
 */
public abstract class AbstractDatabaseConnection
        implements IDatabaseConnection{

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final DatabaseConfiguration _configuration;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractDatabaseConnection(final DatabaseConfiguration config) {
        _configuration = config;
    }

    @Override
    public String toString() {
        final JSONObject sb = new JSONObject();
        sb.put("configuration", _configuration.json());

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public DatabaseConfiguration configuration() {
        return _configuration;
    }

    public boolean hasDatabase(final String name) {
        return CollectionUtils.contains(this.databaseNames(), name);
    }


}


