package org.lyj.ext.db.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.IndexEntity;
import com.arangodb.model.GeoIndexOptions;
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
            _collection.ensureHashIndex(Arrays.asList(fields), options);
        } catch (Throwable ignored) {

        }
    }

    /**
     * Creates a geo-spatial index on all documents using location as path to the coordinates.
     * The value of the attribute has to be an array with at least two numeric values.
     * The array must contain the latitude (first value) and the longitude (second value).
     * @param fields ["location"]
     */
    @Override
    public void addGeoIndex(final String[] fields) {
        this.addGeoIndex(fields, false);
    }

    /**
     * Creates a geo-spatial index on all documents using location as path to the coordinates.
     * The value of the attribute has to be an array with at least two numeric values.
     * The array must contain the latitude (first value) and the longitude (second value).
     * @param fields  ["location"]
     * @param geoJson To create a geo on an array attribute that contains longitude first, set the geoJson attribute to true.
     */
    @Override
    public void addGeoIndex(final String[] fields, boolean geoJson) {
        try {
            final GeoIndexOptions options = new GeoIndexOptions();
            options.geoJson(geoJson);
            _collection.ensureGeoIndex(Arrays.asList(fields), options);
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
