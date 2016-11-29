package org.lyj.ext.db;

import org.lyj.commons.Delegates;

import java.util.Collection;
import java.util.Map;

/**
 * database collection
 */
public interface IDatabaseCollection<T> {

    IDatabaseCollectionSchema schema();

    String name();

    long count();

    boolean exists(final Object key);

    T insert(final T entity);

    T update(final T entity);

    T upsert(final T entity);

    boolean remove(final Object key);

    T get(final Object key);

    void forEach(final Delegates.Callback<T> callback);

    void forEach(final String query, final Map<String, Object> bindArgs, final Delegates.Callback<T> callback);

    T removeOne(final String query, final Map<String, Object> bindArgs);

    Collection<T> remove(final String query, final Map<String, Object> bindArgs);

    T removeOneEqual(final Map<String, Object> bindArgs);

    Collection<T> removeEqual(final Map<String, Object> bindArgs);

    T findOne(final String query, final Map<String, Object> bindArgs);

    Collection<T> find(final String query, final Map<String, Object> bindArgs);

    T findOneEqual(final Map<String, Object> bindArgs);

    Collection<T> findEqual(final Map<String, Object> bindArgs);



}
