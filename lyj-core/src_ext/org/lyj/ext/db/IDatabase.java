package org.lyj.ext.db;

/**
 * database
 */
public interface IDatabase {

    String name();

    String[] collectionNames();

    <T> IDatabaseCollection<T> collection(String name, Class<T> aclass);

}
