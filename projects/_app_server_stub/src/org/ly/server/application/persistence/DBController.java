package org.ly.server.application.persistence;

import org.json.JSONObject;
import org.ly.server.IConstants;
import org.lyj.Lyj;
import org.lyj.commons.io.jsonrepository.JsonRepository;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.IDatabaseConnection;
import org.lyj.ext.db.arango.ArnConnector;
import org.lyj.ext.db.configuration.DatabaseConfiguration;
import org.lyj.ext.db.exceptions.DatabaseDoesNotExists;

/**
 * Main DB controller
 */
public class DBController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String CONNECTION = "CONNECTION";

    private static final String DB_GLOBAL = IConstants.DB_GLOBAL; // global database
    private static final String DB_CUSTOM_PREFIX = IConstants.DB_CUSTOM_PREFIX;

    private static final String DB_CONFIGURATION_PATH = IConstants.DB_CONFIGURATION_PATH;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonRepository _config;

    private DatabaseConfiguration _db_config;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DBController() {
        _config = Lyj.getConfiguration();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public DatabaseConfiguration configuration() {
        return _db_config;
    }

    public void close() {

    }

    public void open() {
        this.init();
    }

    public IDatabaseConnection connection() {
        if (!ArnConnector.instance().contains(CONNECTION)) {
            ArnConnector.instance().add(CONNECTION, _db_config);
        }
        return ArnConnector.instance().connection(CONNECTION);
    }

    public IDatabase db() {
        try {
            return this.connection().database(DB_GLOBAL);
        } catch (final DatabaseDoesNotExists t) {
            super.error("db", t);
        }
        return null;
    }

    public IDatabase db(final String db_name) {
        try {
            //final String db_name = DB_BOT_PREFIX.concat(name);
            return this.connection().database(db_name);
        } catch (final DatabaseDoesNotExists t) {
            super.error("db", t);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        super.info("init", "DATABASE: START CONFIGURATION");

        final JSONObject configuration = _config.getJSONObject(DB_CONFIGURATION_PATH);
        if (null != configuration) {

            _db_config = new DatabaseConfiguration(configuration);

        } else {

            _db_config = new DatabaseConfiguration().enabled(false);

            super.error("init", "Missing Configuration!");
        }

        super.info("init", "DATABASE: FINISH CONFIGURATION");
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static DBController __instance;

    public static synchronized DBController instance() {
        if (null == __instance) {
            __instance = new DBController();
        }
        return __instance;
    }

    public static String DBName() {
        return DB_GLOBAL;
    }

    public static String DBName(final String company_uid) {
        return DB_CUSTOM_PREFIX.concat(company_uid);
    }

}
