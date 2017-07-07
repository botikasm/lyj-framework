package org.lyj.ext.db;

/**
 * Collection schema interface
 */
public interface IDatabaseCollectionSchema {

    void addIndex(final String[] fields, final boolean unique);

    void addGeoIndex(final String[] fields);

    void addGeoIndex(final String[] fields, final boolean geoJson);

    void removeIndex(final String[] fields);

}
