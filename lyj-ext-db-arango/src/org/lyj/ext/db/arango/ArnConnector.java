package org.lyj.ext.db.arango;

import org.lyj.ext.db.AbstractConnectionManager;
import org.lyj.ext.db.IDatabaseConnection;
import org.lyj.ext.db.configuration.DatabaseConfiguration;

/**
 * ArangoDB main controller
 */
public class ArnConnector
        extends AbstractConnectionManager {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    @Override
    protected IDatabaseConnection create(final DatabaseConfiguration configuration) {
        return new ArnConnection(configuration);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ArnConnector __instance;

    public static ArnConnector instance() {
        if (null == __instance) {
            __instance = new ArnConnector();
        }
        return __instance;
    }

}
