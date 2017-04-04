package org.lyj.ext.db.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentDeleteEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.entity.MultiDocumentEntity;
import org.lyj.commons.Delegates;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.AbstractDatabaseCollection;
import org.lyj.ext.db.IDatabase;

import java.util.*;

/**
 *
 */
public class ArnCollection<T>
        extends AbstractDatabaseCollection<T> {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_KEY = "_key";

    private static final String ARRAY_EXPANSION_OPERATOR = "[*]"; // https://docs.arangodb.com/3.1/AQL/Advanced/ArrayOperators.html#array-expansion

    private static final String QUERY_FOR = "FOR t IN %s";
    private static final String QUERY_FILTER = "FILTER";
    private static final String QUERY_FILTER_EQUAL = "t.%s == @%s";
    private static final String QUERY_FILTER_IN = "@%s IN t.%s";
    private static final String QUERY_RETURN = "RETURN t";
    private static final String QUERY_REMOVE = "REMOVE t IN %s LET removed = OLD RETURN removed";
    private static final String QUERY_SORT_SINGLE = "SORT {{comma_sep_fields_1}} {{mode_1}}";
    private static final String QUERY_SORT_MULTI = "SORT {{comma_sep_fields_1}} {{mode_1}}, {{comma_sep_fields_2}} {{mode_2}}";

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

    public long count(final String query, final Map<String, Object> bindArgs) {
        try {
            long count = 0;
            final ArangoCursor<T> cursor = _db.query(query, bindArgs, null, super.entityClass());
            while (cursor.hasNext()) {
                cursor.next();
                count++;
            }
            return count;
        } catch (Throwable ignored) {
            return 0;
        }
    }

    public long countEqual(final Map<String, Object> bindArgs) {
        final String query = this.queryEqual(bindArgs, null, 0);
        return this.count(query, bindArgs);
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

    public int insert(final Collection<T> entities) {
        if (null != entities && !entities.isEmpty()) {
            final MultiDocumentEntity<DocumentCreateEntity<T>> new_entities = this.collection().insertDocuments(entities);
            return new_entities.getDocuments().size();
        }
        return 0;
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
        final String query = this.queryEqual(bindArgs, null, 0);
        return this.removeOne(query, bindArgs);
    }

    public Collection<T> removeEqual(final Map<String, Object> bindArgs) {
        final String query = this.queryEqual(bindArgs, null, 0);
        return this.remove(query, bindArgs);
    }

    public T get(final Object key) {
        return this.collection().getDocument(this.key(key), super.entityClass());
    }

    public T get(final String key) {
        return this.collection().getDocument(this.key(key), super.entityClass());
    }

    @Override
    public void forEach(final Delegates.FunctionArg<T, Boolean> callback) {
        if (null != callback) {
            final String query = this.queryNoParams();
            this.forEach(query, callback);
        }
    }

    @Override
    public void forEachAsc(final String[] sort, final Delegates.FunctionArg<T, Boolean> callback) {
        if (null != callback) {
            final String query = this.queryNoParams(this.sortMap(SORT_ASC, sort));
            this.forEach(query, callback);
        }
    }

    @Override
    public void forEachDesc(final String[] sort, final Delegates.FunctionArg<T, Boolean> callback) {
        if (null != callback) {
            final String query = this.queryNoParams(this.sortMap(SORT_DESC, sort));
            this.forEach(query, callback);
        }
    }

    @Override
    public void forEach(final String query, final Map<String, Object> bindArgs,
                        final Delegates.FunctionArg<T, Boolean> callback) {
        if (null != callback) {
            final ArangoCursor<T> cursor = _db.query(query, bindArgs, null, super.entityClass());
            while (cursor.hasNext()) {
                final Boolean response = callback.call(cursor.next());
                if (null != response && response) {
                    break;
                }
            }
        }
    }

    @Override
    public void forEachEqual(final Map<String, Object> bindArgs, final Delegates.FunctionArg<T, Boolean> callback) {
        if (null != callback) {
            final String query = this.queryEqual(bindArgs, null, 0);
            this.forEach(query, bindArgs, callback);
        }
    }

    public void forEachEqualAsc(final Map<String, Object> bindArgs, final String[] sort,
                                final Delegates.FunctionArg<T, Boolean> callback) {
        if (null != callback) {
            final String query = this.queryEqual(bindArgs, this.sortMap(SORT_ASC, sort), 0);
            this.forEach(query, bindArgs, callback);
        }
    }

    public void forEachEqualDesc(final Map<String, Object> bindArgs, final String[] sort,
                                 final Delegates.FunctionArg<T, Boolean> callback) {
        if (null != callback) {
            final String query = this.queryEqual(bindArgs, this.sortMap(SORT_DESC, sort), 0);
            this.forEach(query, bindArgs, callback);
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
        final String query = this.queryEqual(bindArgs, null, 0);
        return this.findOne(query, bindArgs);
    }

    public Collection<T> findEqual(final Map<String, Object> bindArgs) {
        final String query = this.queryEqual(bindArgs, null, 0);
        return this.find(query, bindArgs);
    }

    public Collection<T> findEqual(final Map<String, Object> bindArgs, final String[] sort) {
        final String query = this.queryEqual(bindArgs, this.sortMap(SORT_ASC, sort), 0);
        return this.find(query, bindArgs);
    }

    public Collection<T> findEqual(final Map<String, Object> bindArgs, final String[] sort, final int limit) {
        final String query = this.queryEqual(bindArgs, this.sortMap(SORT_ASC, sort), limit);
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


    private Map<String, String[]> sortMap(final String mode, final String[] fields) {
        final Map<String, String[]> sort = new HashMap<>();
        sort.put(mode, fields);
        return sort;
    }

    private String sort(final Map<String, String[]> sort_params) {
        if (null != sort_params && sort_params.size() > 0) {
            final String tpl = sort_params.size() > 1 ? QUERY_SORT_MULTI : QUERY_SORT_SINGLE;
            final Counter counter = new Counter(1);
            final Map<String, String> tpl_map = new HashMap<>();
            sort_params.forEach((mode, fields) -> {
                if (null != fields) {
                    tpl_map.put("comma_sep_fields_" + counter.valueAsInt(), this.toString(fields));
                    tpl_map.put("mode_" + counter.valueAsInt(), mode.toUpperCase());
                    counter.inc();
                }
            });
            if (!tpl_map.isEmpty()) {
                return FormatUtils.formatTemplate(tpl, "{{", "}}", tpl_map);
            }
        }
        return "";
    }

    private String toString(final String[] field_names) {
        final StringBuilder sb = new StringBuilder();
        for (final String field_name : field_names) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            if (field_name.startsWith("t.")) {
                sb.append(field_name);
            } else {
                sb.append("t.").append(field_name);
            }
        }
        return sb.toString();
    }

    private String queryNoParams() {
        return FormatUtils.format(QUERY_NO_PARAMS, super.name());
    }

    private String queryNoParams(final Map<String, String[]> sort_fields) {
        final StringBuilder sb = new StringBuilder();
        sb.append(FormatUtils.format(QUERY_FOR, super.name()));

        // sort
        final String sort = this.sort(sort_fields);
        if (StringUtils.hasText(sort)) {
            sb.append(" ").append(sort);
        }

        // return
        sb.append(" ").append(QUERY_RETURN);
        return sb.toString();
    }

    private String queryEqual(final Map<String, Object> params,
                              final Map<String, String[]> sort_fields,
                              final int limit) {
        final Set<String> names = params.keySet();
        final StringBuilder sb = new StringBuilder();
        sb.append(FormatUtils.format(QUERY_FOR, super.name()));

        // filter
        sb.append(" ").append(QUERY_FILTER).append(" ");
        final Counter count = new Counter(0);
        for (final String name : names) {
            if (count.value() > 0) {
                sb.append(" && ");
            }
            if (name.contains(ARRAY_EXPANSION_OPERATOR)) {
                final String fld_val = MD5.encode(name);
                params.put(fld_val, params.get(name)); // add value to parameters
                params.remove(name);
                sb.append(FormatUtils.format(QUERY_FILTER_IN, fld_val, name));
            } else if (name.contains(".")) {
                final String fld_name = MD5.encode(name);
                params.put(fld_name, params.get(name)); // add value to parameters
                params.remove(name);
                sb.append(FormatUtils.format(QUERY_FILTER_EQUAL, name, fld_name));
            } else {
                sb.append(FormatUtils.format(QUERY_FILTER_EQUAL, name, name));
            }

            count.inc();
        }

        // sort
        final String sort = this.sort(sort_fields);
        if (StringUtils.hasText(sort)) {
            sb.append(" ").append(sort);
        }

        // limit
        if (limit > 0) {
            sb.append(" ").append("LIMIT ").append(limit);
        }

        // return
        sb.append(" ").append(QUERY_RETURN);
        return sb.toString();
    }

    private String queryRemove(final String query_select) {
        return query_select.replaceFirst(QUERY_RETURN, FormatUtils.format(QUERY_REMOVE, super.name()));
        //return query_select.concat(" ").concat(FormatUtils.format(QUERY_REMOVE, super.name()));
    }

    private void forEach(final String query,
                         final Delegates.FunctionArg<T, Boolean> callback) {
        final ArangoCursor<T> cursor = _db.query(query, null, null, super.entityClass());
        while (cursor.hasNext()) {
            final Boolean response = callback.call(cursor.next());
            if (null != response && response) {
                break;
            }
        }
    }


}

