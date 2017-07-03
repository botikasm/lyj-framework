package org.lyj.ext.db;

import java.util.Collection;
import java.util.Map;

/**
 * database
 */
public interface IDatabase {

    String name();

    String[] collectionNames();

    <T> IDatabaseCollection<T> collection(String name, Class<T> aclass);

    <T> Collection<T> find(final String query, final Map<String, Object> bindArgs, final Class<T> entityClass);

}
