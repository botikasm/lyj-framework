package org.lyj.ext.db.arango;

import com.arangodb.ArangoDB;
import org.lyj.ext.db.AbstractDatabaseConnection;
import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.arango.serialization.ArangoJsonItemSerializer;
import org.lyj.ext.db.configuration.DatabaseConfiguration;
import org.lyj.ext.db.configuration.DatabaseConfigurationCredential;
import org.lyj.ext.db.configuration.DatabaseConfigurationHost;
import org.lyj.ext.db.exceptions.DatabaseDoesNotExists;

import java.util.Collection;

/**
 * Connection wrapper
 */
public class ArnConnection
        extends AbstractDatabaseConnection {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private ArangoDB __connection;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ArnConnection(final DatabaseConfiguration config) {
        super(config);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } finally {
            super.finalize();
        }
    }

    @Override
    public boolean open() {
        return null != this.connection();
    }

    @Override
    public boolean close() {
        try {
            if (null != __connection) {
                __connection.shutdown();
                __connection = null;
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public IDatabase database(final String name) throws DatabaseDoesNotExists {
        return this.createDb(name);
    }

    @Override
    public String[] databaseNames() {
        final Collection<String> names = this.connection().getDatabases();
        return names.toArray(new String[names.size()]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private ArangoDB connection() {
        if (null == __connection) {
            final DatabaseConfigurationCredential[] credentials = super.configuration().credentials();
            final DatabaseConfigurationHost[] hosts = super.configuration().hosts();
            final String username = credentials.length > 0 ? credentials[0].username() : "";
            final String password = credentials.length > 0 ? credentials[0].password() : "";
            final String host = hosts.length > 0 ? hosts[0].host() : "";
            final int port = hosts.length > 0 ? hosts[0].port() : 0;

            __connection = new ArangoDB.Builder()
                    .host(host, port)
                    .user(username).password(password)
                    //.registerModule(new ArangoJsonItemSerializer())
                    .build();
        }
        return __connection;
    }

    private IDatabase createDb(final String name) throws DatabaseDoesNotExists {
        if (super.configuration().autocreate()) {
            return new ArnDatabase(this.connection(), name);
        } else if (super.hasDatabase(name)) {
            return new ArnDatabase(this.connection(), name);
        } else {
            throw new DatabaseDoesNotExists(name);
        }

    }

}
