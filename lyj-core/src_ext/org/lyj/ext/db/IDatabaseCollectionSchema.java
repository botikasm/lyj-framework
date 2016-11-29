package org.lyj.ext.db;

/**
 * Collection schema interface
 */
public interface IDatabaseCollectionSchema {

    void addIndex(final String[] fields, final boolean unique);

    void removeIndex(final String[] fields);

}
