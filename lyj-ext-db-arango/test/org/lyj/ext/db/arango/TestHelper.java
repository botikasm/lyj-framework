package org.lyj.ext.db.arango;

import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.IDatabaseCollection;
import org.lyj.ext.db.IDatabaseConnection;
import org.lyj.ext.db.configuration.DatabaseConfiguration;
import org.lyj.ext.db.configuration.DatabaseConfigurationCredential;
import org.lyj.ext.db.configuration.DatabaseConfigurationHost;
import org.lyj.ext.db.exceptions.DatabaseDoesNotExists;

/**
 *
 */
public class TestHelper {

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    public static IDatabaseCollection<String> collection(final String coll_name) throws DatabaseDoesNotExists {
        DatabaseConfiguration configuration = new DatabaseConfiguration();
        configuration.add(new DatabaseConfigurationHost().host("localhost").port(8529),
                new DatabaseConfigurationCredential().username("root").password("!qaz2WSX098"));

        ArnConnector.instance().add("sample", configuration);
        IDatabaseConnection connection = ArnConnector.instance().connection("sample");

        IDatabase db = connection.database("test");
              
        return db.collection(coll_name, String.class);
    }

    public static <T> IDatabaseCollection<T> collection(final String coll_name, final Class<T> aclass) throws DatabaseDoesNotExists {
        DatabaseConfiguration configuration = new DatabaseConfiguration();
        configuration.add(new DatabaseConfigurationHost().host("localhost").port(8529),
                new DatabaseConfigurationCredential().username("root").password("!qaz2WSX098"));

        ArnConnector.instance().add("sample", configuration);
        IDatabaseConnection connection = ArnConnector.instance().connection("sample");

        IDatabase db = connection.database("test");

        return db.collection(coll_name, aclass);
    }
}
