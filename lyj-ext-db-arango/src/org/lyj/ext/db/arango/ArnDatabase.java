package org.lyj.ext.db.arango;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.CollectionEntity;
import org.lyj.ext.db.AbstractDatabase;
import org.lyj.ext.db.IDatabaseCollection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ArnDatabase
        extends AbstractDatabase {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final ArangoDB _db_connection;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ArnDatabase(final ArangoDB db, final String name) {
        super(name);
        _db_connection = db;
        try {
            _db_connection.createDatabase(name); // creates the database
        } catch (Throwable ignored) {
            // database already exists
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public String name() {
        return super.name();
    }

    public String[] collectionNames() {
        if (null != _db_connection) {
            final Set<String> result = new HashSet<>();
            final Collection<CollectionEntity> names = this.db().getCollections();
            names.forEach((collection) -> {
                result.add(collection.getName());
            });
            return result.toArray(new String[result.size()]);
        }
        return new String[0];
    }

    @Override
    public <T> IDatabaseCollection<T> collection(final String name, final Class<T> entityClass) {
        return new ArnCollection<>(this, this.db(), name, entityClass);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private ArangoDatabase db() {
        return _db_connection.db(super.name());
    }

}
