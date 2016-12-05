package org.lyj.ext.db.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentDeleteEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import org.lyj.commons.Delegates;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.AbstractDatabaseCollection;
import org.lyj.ext.db.IDatabase;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ArnCollection<T>
        extends AbstractDatabaseCollection<T> {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_KEY = "_key";

    private static final String QUERY_FOR = "FOR t IN %s";
    private static final String QUERY_FILTER = "FILTER";
    private static final String QUERY_FILTER_EQUAL = "t.%s == @%s";
    private static final String QUERY_RETURN = "RETURN t";
    private static final String QUERY_REMOVE = "REMOVE t IN %s LET removed = OLD RETURN removed";

    private static final String QUERY_NO_PARAMS = QUERY_FOR.concat(" ").concat(QUERY_RETURN);

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final ArangoDatabase _db;

    private ArangoCollection __collection;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ArnCollection(final IDatabase parent,
                         final ArangoDatabase db,
                         final String name,
                         final Class<T> entity_class) {
        super(parent, name, entity_class);
        _db = db;
        try {
            _db.createCollection(name);
        } catch (Throwable ignore) {
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public ArnCollectionSchema schema() {
        return new ArnCollectionSchema(_db, this.collection());
    }

    @Override
    public long count() {
        return this.collection().count().getCount();
    }

    public boolean exists(final Object key) {
        return this.collection().documentExists(this.key(key));
    }

    public boolean exists(final String key) {
        return this.collection().documentExists(this.key(key));
    }

    public T insert(final T entity) {
        if (null != entity) {
            final DocumentCreateEntity<T> new_entity = this.collection().insertDocument(entity);
            final String new_key = new_entity.getKey();
            return StringUtils.hasText(new_key) ? this.get(new_key) : null;
        }
        return null;
    }

    public T update(final T entity) {
        if (null != entity) {
            final String key = this.key(entity);
            return this.update(key, entity);
        }
        return null;
    }

    public T update(final String key, final T entity) {
        if (null != entity && StringUtils.hasText(key)) {
            final DocumentUpdateEntity<T> new_entity = this.collection().updateDocument(key, entity);
            final String new_key = new_entity.getKey();
            return StringUtils.hasText(new_key) ? this.get(new_key) : null;
        }
        return null;
    }

    public T upsert(final T entity) {
        final String key = this.key(entity);
        if (null == key) {
            return this.insert(entity);
        } else {
            if (this.exists(key)) {
                return this.update(key, entity);
            } else {
                return this.insert(entity);
            }
        }
    }

    public boolean remove(final Object key) {
        if (null != key) {
            if (key instanceof String) {
                if (StringUtils.hasText((String) key)) {
                    return this.remove((String) key);
                }
            } else if (key instanceof Map) {
                return removeEqual((Map) key).size() > 0;
            } else {
                final String akey = this.key(key);
                return this.remove(akey);
            }
        }
        return false;
    }

    public boolean remove(final String key) {
        try {
            if (StringUtils.hasText(key)) {
                final DocumentDeleteEntity response = this.collection().deleteDocument(this.key(key));
                if (null != response) {
                    return true;
                }
            }
        } catch (Throwable ignored) {

        }
        return false;
    }

    public T removeOne(final String query, final Map<String, Object> bindArgs) {
        final String query_remove = this.queryRemove(query);
        final ArangoCursor<T> cursor = _db.query(query_remove, bindArgs, null, super.entityClass());
        if ((cursor.hasNext())) {
            return cursor.next();
        }
        return null;
    }

    public Collection<T> remove(final String query, final Map<String, Object> bindArgs) {
        final String query_remove = this.queryRemove(query);
        final Collection<T> response = new LinkedList<T>();
        final ArangoCursor<T> cursor = _db.query(query_remove, bindArgs, null, super.entityClass());
        while (cursor.hasNext()) {
            response.add(cursor.next());
        }
        return response;
    }

    public T removeOneEqual(final Map<String, Object> bindArgs) {
        final String query = this.queryEqual(bindArgs.keySet());
        return this.removeOne(query, bindArgs);
    }

    public Collection<T> removeEqual(final Map<String, Object> bindArgs) {
        final String query = this.queryEqual(bindArgs.keySet());
        return this.remove(query, bindArgs);
    }

    public T get(final Object key) {
        return this.collection().getDocument(this.key(key), super.entityClass());
    }

    public T get(final String key) {
        return this.collection().getDocument(this.key(key), super.entityClass());
    }

    @Override
    public void forEach(final Delegates.Callback<T> callback) {
        if (null != callback) {
            final String query = this.queryNoParams();
            final ArangoCursor<T> cursor = _db.query(query, null, null, super.entityClass());
            while (cursor.hasNext()) {
                callback.handle(cursor.next());
            }
        }
    }

    public void forEach(final String query, final Map<String, Object> bindArgs, final Delegates.Callback<T> callback) {
        if (null != callback) {
            final ArangoCursor<T> cursor = _db.query(query, bindArgs, null, super.entityClass());
            while (cursor.hasNext()) {
                callback.handle(cursor.next());
            }
        }
    }

    public T findOne(final String query, final Map<String, Object> bindArgs) {
        final ArangoCursor<T> cursor = _db.query(query, bindArgs, null, super.entityClass());
        if ((cursor.hasNext())) {
            return cursor.next();
        }
        return null;
    }

    public Collection<T> find(final String query, final Map<String, Object> bindArgs) {
        final Collection<T> response = new LinkedList<T>();
        final ArangoCursor<T> cursor = _db.query(query, bindArgs, null, super.entityClass());
        while (cursor.hasNext()) {
            response.add(cursor.next());
        }
        return response;
    }

    public T findOneEqual(final Map<String, Object> bindArgs) {
        final String query = this.queryEqual(bindArgs.keySet());
        return this.findOne(query, bindArgs);
    }

    public Collection<T> findEqual(final Map<String, Object> bindArgs) {
        final String query = this.queryEqual(bindArgs.keySet());
        return this.find(query, bindArgs);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private ArangoCollection collection() {
        if (null == __collection) {
            __collection = _db.collection(super.name());
        }
        return __collection;
    }

    private String key(final Object entity) {
        return (String) super.getFieldValue(entity, FLD_KEY);
    }

    private String queryNoParams() {
        return FormatUtils.format(QUERY_NO_PARAMS, super.name());
    }

    private String queryEqual(final Set<String> names) {
        final StringBuilder sb = new StringBuilder();
        sb.append(FormatUtils.format(QUERY_FOR, super.name()));
        sb.append(" ").append(QUERY_FILTER).append(" ");
        final Counter count = new Counter(0);
        names.forEach((name) -> {
            if(count.value()>0){
                sb.append(" && ");
            }
            sb.append(FormatUtils.format(QUERY_FILTER_EQUAL, name, name));
            count.inc();
        });
        sb.append(" ");
        sb.append(QUERY_RETURN);
        return sb.toString();
    }

    private String queryRemove(final String query_select) {
        return query_select.replaceFirst(QUERY_RETURN, FormatUtils.format(QUERY_REMOVE, super.name()));
        //return query_select.concat(" ").concat(FormatUtils.format(QUERY_REMOVE, super.name()));
    }
}

