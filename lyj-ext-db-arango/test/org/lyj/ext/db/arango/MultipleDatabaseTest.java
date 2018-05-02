package org.lyj.ext.db.arango;


import org.junit.Test;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.IDatabaseConnection;
import org.lyj.ext.db.configuration.DatabaseConfiguration;
import org.lyj.ext.db.configuration.DatabaseConfigurationCredential;
import org.lyj.ext.db.configuration.DatabaseConfigurationHost;
import org.lyj.ext.db.exceptions.DatabaseDoesNotExists;

import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class MultipleDatabaseTest {


    private static final int MAX_DATABASE = 1000;
    private static final String PREFIX = "db_test_";


    @Test
    public void fullTest() throws Exception {
        IDatabaseConnection connection = connection();

        final String[] names = connection.databaseNames();
        System.out.println("Databases: " + StringUtils.toString(names));

        this.drop(connection);

        this.create(connection);
    }

    @Test
    public void createTest() throws Exception {
        IDatabaseConnection connection = connection();

        final String[] names = connection.databaseNames();
        System.out.println("Databases: " + StringUtils.toString(names));

        this.create(connection);
    }

    @Test
    public void dropTest() throws Exception {
        IDatabaseConnection connection = connection();

        final String[] names = connection.databaseNames();
        System.out.println("Databases: " + StringUtils.toString(names));

        this.drop(connection);
    }

    @Test
    public void countTest() throws Exception {
        IDatabaseConnection connection = connection();

        final String[] names = connection.databaseNames();
        System.out.println("Databases: " + names.length);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void drop(final IDatabaseConnection connection) throws DatabaseDoesNotExists {
        final String[] names = connection.databaseNames();
        int count = 0;
        for (final String name : names) {
            if (name.startsWith(PREFIX)) {
                IDatabase db = connection.database(name);
                if (db.drop()) {
                    count++;
                } else {
                    System.out.println("CANNOT REMOVE: " + name);
                }
            }
        }
        System.out.println("REMOVED DATABASES: " + count);
    }

    private void create(final IDatabaseConnection connection) throws DatabaseDoesNotExists {
        int count = 0;
        for (int i = 0; i < MAX_DATABASE; i++) {
            final String name = PREFIX + i;
            IDatabase db = connection.database(name);
            if (null != db) {
                count++;
            }
        }
        System.out.println("CREATED DATABASES: " + count);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static IDatabaseConnection __connection;

    private static IDatabaseConnection connection() {
        if (null == __connection) {
            DatabaseConfiguration configuration = new DatabaseConfiguration();
            configuration.add(new DatabaseConfigurationHost().host("localhost").port(8529),
                    new DatabaseConfigurationCredential().username("root").password("!qaz2WSX098"));

            System.out.println("Configuration: " + configuration.toString());

            ArnConnector.instance().add("sample", configuration);
            __connection = ArnConnector.instance().connection("sample");
            assertNotNull(__connection);
            System.out.println("Connection: " + __connection.toString());
        }
        return __connection;
    }
}