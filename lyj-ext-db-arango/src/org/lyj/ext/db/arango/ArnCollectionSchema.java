package org.lyj.ext.db.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import com.arangodb.model.HashIndexOptions;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.ext.db.IDatabaseCollectionSchema;

import java.util.Arrays;
import java.util.Collection;

/**
 * Collection schema
 */
public class ArnCollectionSchema
        implements IDatabaseCollectionSchema {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final ArangoDatabase _db;

    private ArangoCollection _collection;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ArnCollectionSchema(final ArangoDatabase db,
                               final ArangoCollection collection) {
        _db = db;
        _collection = collection;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void addIndex(final String[] fields, boolean unique) {
        try {
            final HashIndexOptions options = new HashIndexOptions();
            options.unique(unique);
            _collection.createHashIndex(Arrays.asList(fields), options);
        } catch (Throwable ignored) {

        }
    }

    @Override
    public void removeIndex(final String[] fields) {
        _collection.getIndexes().forEach((index) -> {
            final Collection<String> index_fields = index.getFields();
            if (CollectionUtils.equals(fields, index_fields.toArray(new String[index_fields.size()]))) {
                _db.deleteIndex(index.getId());
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
