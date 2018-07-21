package org.ly.ose.server.application.persistence;

import org.json.JSONArray;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.IDatabaseCollection;
import org.lyj.ext.db.arango.serialization.ArangoMapDocument;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public abstract class BaseService<T extends ArangoMapDocument>
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    protected final static String FLD_KEY = "_key";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final IDatabase _db;
    private final Class<T> _entity_class;
    private final IDatabaseCollection<T> _collection;
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public BaseService(final IDatabase db,
                       final String collection_name,
                       final Class<T> entity_class) {
        _db = db;
        _entity_class = entity_class;
        _collection = this.collection(collection_name);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public IDatabaseCollection<T> collection() {
        return _collection;
    }

    public boolean exists(final String id) {
        return this.collection().exists(id);
    }

    public T get(final String id) {
        final T item = this.collection().get(id);
        return item;
    }

    public boolean remove(final String id) {
        return this.collection().remove(id);
    }

    public Collection<T> removeEqual(final Map<String, Object> bindArgs) {
        return this.collection().removeEqual(bindArgs);
    }

    public T upsert(final T item) {
        return this.collection().upsert(item);
    }

    public int insertAll(final Collection<T> entities) {
        return this.collection().insert(entities);
    }

    public long count() {
        return this.collection().count();
    }

    public T updateField(final String id, final String field_name, final Object field_value) {
        final T item = this.collection().get(id);
        if (null != item) {
            item.put(field_name, field_value);
            return this.upsert(item);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected JSONArray toJSONArray(final Collection<T> items) {
        final JSONArray response = new JSONArray();
        if (null != items) {
            for (final ArangoMapDocument item : items) {
                response.put(item.json());
            }
        }
        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private IDatabaseCollection<T> collection(final String name) {
        return _db.collection(name, _entity_class);
    }


}
