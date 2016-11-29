package org.lyj.commons.io.db.jdbm;

import net.kotek.jdbm.ConcurrentSortedMap;
import net.kotek.jdbm.DB;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.io.db.jdbm.exceptions.ItemAlreadyExistsException;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.*;

/**
 * Index manager.
 * Store primary keys for a quick response.
 */
public class JDBCollectionIndexes {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final DB _db;
    private final String _coll_name;
    private final String _store_names;
    private final String _store_values;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JDBCollectionIndexes(final DB db,
                                final String collection) {
        _db = db;
        _coll_name = collection;
        _store_names = _coll_name.concat("_").concat("index_names");
        _store_values = _coll_name.concat("_").concat("index_values");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("collection=").append(_coll_name);
        sb.append(",");
        sb.append("store_names=").append(this.collection(_store_names).size());
        sb.append(",");
        sb.append("store_values=").append(this.collection(_store_names).size());
        sb.append("}");
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int count() {
        return this.collection(_store_names).size();
    }

    public JDBCollectionIndexes clear() {
        this.collection(_store_names).clear();
        this.collection(_store_values).clear();
        return this;
    }

    public JDBCollectionIndexes clearValues() {
        this.collection(_store_values).clear();
        return this;
    }

    public JDBCollectionIndexes addIndex(final String[] fields, final boolean unique) {
        final String store_names_id = encodeIndexId(fields);
        if (!this.existIndex(store_names_id)) {
            // ADD
            final JDBCollectionIndex index = new JDBCollectionIndex().name(store_names_id).unique(unique);
            this.collection(_store_names).put(store_names_id, index.map());
        } else {
            // UPDATE
            final JDBCollectionIndex index = new JDBCollectionIndex(this.collection(_store_names).get(store_names_id));
            index.name(store_names_id);
            index.unique(unique);
        }
        return this;
    }

    public boolean existIndex(final String[] fields) {
        return this.existIndex(encodeIndexId(fields));
    }

    public JDBCollectionIndex getIndex(final String[] fields) {
        final String store_names_id = encodeIndexId(fields);
        if (this.existIndex(store_names_id)) {
            return new JDBCollectionIndex(this.collection(_store_names).get(store_names_id));
        }
        return null;
    }

    public JDBCollectionIndex removeIndex(final String[] fields) {
        final String store_names_id = encodeIndexId(fields);
        if (this.existIndex(store_names_id)) {
            final JDBCollectionIndex index = new JDBCollectionIndex(this.collection(_store_names).remove(store_names_id));
            // remove also all values stored for this index

            return index;
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      i n d e x    v a l u e s
    // ------------------------------------------------------------------------

    public void insert(final Object primaryKeyValue, final Map<String, Object> item) throws ItemAlreadyExistsException {
        final JDBCollectionIndex[] indexes = this.getIndexes(item);
        if (indexes.length > 0) {
            for (final JDBCollectionIndex index : indexes) {
                final String[] names = decodeIndexId(index.name());
                final Object[] values = getValues(names, item);
                this.addPrimaryKeyValue(names, values, primaryKeyValue);
            }
        }
    }

    public void update(final Object primaryKeyValue, final Map<String, Object> item) throws ItemAlreadyExistsException {
        final JDBCollectionIndex[] indexes = this.getIndexes(item);
        if (indexes.length > 0) {
            // remove all updated field values
            for (final JDBCollectionIndex index : indexes) {
                final String[] names = decodeIndexId(index.name());
                this.removePrimaryKeyValues(names);
            }
            this.insert(primaryKeyValue, item);
        }
    }

    public void remove(final Object primaryKeyValue, final Map<String, Object> item) {
        final JDBCollectionIndex[] indexes = this.getIndexes(item);
        if (indexes.length > 0) {
            for (final JDBCollectionIndex index : indexes) {
                final String[] names = decodeIndexId(index.name());
                final Object[] values = getValues(names, item);
                this.removePrimaryKeyValue(names, values);
            }
        }
    }

    public Object[] find(final Map<String, Object> item) {
        final JDBCollectionIndex[] indexes = this.getIndexes(item);
        if (indexes.length > 0) {
            final Set<Object> result = new HashSet<>();
            for (final JDBCollectionIndex index : indexes) {
                final String[] names = decodeIndexId(index.name());
                final Object[] values = getValues(names, item);
                final Collection<Object> ids = getPrimaryKeyValues(names, values);
                if (null != ids) {
                    result.addAll(ids);
                }
            }
            return result.toArray(new Object[result.size()]);
        }
        return null;
    }

    public Object findOne(final Map<String, Object> item) {
        final Object[] ids = this.find(item);
        return (null != ids && ids.length > 0) ? ids[0] : null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private ConcurrentSortedMap<Object, Map<String, Object>> collection(final String name) {
        synchronized (this) {
            return _db.getTreeMap(name);
        }
    }

    private boolean existIndex(final String id) {
        return this.collection(_store_names).containsKey(id);
    }

    private static String encodeIndexId(final String[] fields) {
        return StringUtils.toString(fields, ".");
    }

    private static String[] decodeIndexId(final String index_id) {
        return StringUtils.split(index_id, ".");
    }

    // ------------------------------------------------------------------------
    //                      v a l u e s
    // ------------------------------------------------------------------------

    private boolean existPrimaryKeyValue(final String[] fields, final Object[] values) {
        return this.existPrimaryKeyValue(encodePrimaryKeyId(fields, values));
    }

    private Set<Object> getPrimaryKeyValues(final String[] fields, final Object[] values) {
        final String store_names_id = encodeIndexId(fields);
        if (this.existIndex(store_names_id)) {
            final JDBCollectionIndexValue value = this.getPrimaryKeyValue(encodePrimaryKeyId(fields, values));
            return null != value ? value.values() : null;
        }
        return null;
    }

    private void addPrimaryKeyValue(final String[] fields,
                                    final Object[] values,
                                    final Object primaryKeyValue) throws ItemAlreadyExistsException {
        final JDBCollectionIndex index = this.getIndex(fields);
        if (null != index) {
            final String store_values_id = encodePrimaryKeyId(fields, values);
            if (this.existPrimaryKeyValue(store_values_id)) {
                if (index.unique()) {
                    throw new ItemAlreadyExistsException(primaryKeyValue, _coll_name);
                }
            } else {
                // ADD NEW VALUE
                this.collection(_store_values).put(store_values_id,
                        new JDBCollectionIndexValue()
                                .indexName(store_values_id)
                                .fields(fields)
                                .value(primaryKeyValue).map());
            }
        }
    }

    private Collection<Object> removePrimaryKeyValue(final String[] fields,
                                                     final Object[] values) {
        final JDBCollectionIndex index = this.getIndex(fields);
        if (null != index) {
            final String store_values_id = encodePrimaryKeyId(fields, values);
            final JDBCollectionIndexValue primary_key_value = this.getPrimaryKeyValue(store_values_id);
            if (null != primary_key_value) {
                this.collection(_store_values).remove(store_values_id);

                return primary_key_value.values();
            }
        }
        return null;
    }

    private Collection<JDBCollectionIndexValue> removePrimaryKeyValues(final String[] fields) {
        final JDBCollectionIndex index = this.getIndex(fields);
        if (null != index) {
            final Collection<JDBCollectionIndexValue> result = new ArrayList<>();
            this.collection(_store_values).forEach((key, item) -> {
                final JDBCollectionIndexValue value = new JDBCollectionIndexValue(item);
                if (value.equalFields(fields)) {
                    // REMOVE
                    this.collection(_store_values).remove(value.indexName());
                    result.add(value);
                }
            });
            return result;
        }
        return null;
    }


    private boolean existPrimaryKeyValue(final String id) {
        return this.collection(_store_values).containsKey(id);
    }

    private static String encodePrimaryKeyId(final String[] fields,
                                             final Object[] values) {
        final String f = StringUtils.toString(fields, ".");
        final String v = StringUtils.toString(values, ".");
        return MD5.encode(f + v);
    }

    private JDBCollectionIndexValue getPrimaryKeyValue(final String store_values_id) {
        if (this.existPrimaryKeyValue(store_values_id)) {
            return new JDBCollectionIndexValue(this.collection(_store_values).get(store_values_id));
        }
        return null;
    }

    private JDBCollectionIndex[] getIndexes(final Map<String, Object> item) {
        final List<JDBCollectionIndex> result = new ArrayList<>();
        this.collection(_store_names).forEach((key, value) -> {
            final String[] names = decodeIndexId((String) key);
            if (containKeys(item, names)) {
                result.add(new JDBCollectionIndex(value));
            }
        });
        return result.toArray(new JDBCollectionIndex[result.size()]);
    }

    private static boolean containKeys(final Map<String, Object> item, final String[] keys) {
        for (final String key : keys) {
            if (!item.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    private static Object[] getValues(final String[] keys, final Map<String, Object> item) {
        final List<Object> result = new LinkedList<>();
        for (final String key : keys) {
            result.add(item.get(key));
        }
        return result.toArray(new Object[result.size()]);
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class JDBCollectionIndex {

        private final Map<String, Object> _data;

        private JDBCollectionIndex() {
            _data = new HashMap<>();
        }

        private JDBCollectionIndex(final Map<String, Object> data) {
            _data = data;
        }

        public Map<String, Object> map() {
            return _data;
        }

        public String name() {
            return (String) _data.get("name");
        }

        public JDBCollectionIndex name(final String value) {
            _data.put("name", value);
            return this;
        }

        public boolean unique() {
            return ConversionUtils.toBoolean(_data.get("unique"));
        }

        public JDBCollectionIndex unique(final boolean value) {
            _data.put("unique", value);
            return this;
        }

    }


    public static class JDBCollectionIndexValue {

        private final Map<String, Object> _data;

        private JDBCollectionIndexValue() {
            _data = new HashMap<>();
        }

        private JDBCollectionIndexValue(final Map<String, Object> data) {
            _data = data;
        }

        public boolean equalFields(final String[] fields) {
            if (fields.length == this.fields().size()) {
                for (final String field : fields) {
                    if (!this.fields().contains(field)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        public Map<String, Object> map() {
            return _data;
        }

        public String indexName() {
            return (String) _data.get("index_name");
        }

        public JDBCollectionIndexValue indexName(final String value) {
            _data.put("index_name", value);
            return this;
        }

        public Set<String> fields() {
            return (Set<String>) _data.get("fields"); // ids
        }

        public JDBCollectionIndexValue fields(final String[] fields) {
            _data.putIfAbsent("fields", new HashSet<>());
            this.fields().addAll(Arrays.asList(fields));
            return this;
        }

        public Set<Object> values() {
            return (Set<Object>) _data.get("values"); // ids
        }

        public JDBCollectionIndexValue value(final Object value) {
            _data.putIfAbsent("values", new HashSet<>());
            this.values().add(value);
            return this;
        }

    }


}
