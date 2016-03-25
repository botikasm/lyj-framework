package org.lyj.commons.io.jdbm;


import net.kotek.jdbm.ConcurrentSortedMap;
import net.kotek.jdbm.DB;
import org.lyj.commons.io.jdbm.exceptions.ItemAlreadyExistsException;
import org.lyj.commons.io.jdbm.util.FilterUtils;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.RandomUtils;

import java.io.IOError;
import java.util.*;

/**
 * Collection Implementation
 */
public class JDBCollection {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ID = "_id"; // primary key

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final DB _db;
    private final String _name;
    private final boolean _enable_transactions;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JDBCollection(final DB db, final boolean enableTransactions, final String name) {
        _db = db;
        _name = name;
        _enable_transactions = enableTransactions;
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String name() {
        return _name;
    }

    public void commit() {
        if (this.active()) {
            _db.commit();
        }
    }

    public void rollback() {
        if (this.active()) {
            _db.rollback();
        }
    }

    public Collection<Map<String, Object>> find() {
        if (this.active()) {
            return this.collection().values();
        } else {
            return new ArrayList<>();
        }
    }

    public Collection<Map<String, Object>> find(final Map<String, Object> filter) {
        if (this.active()) {
            final List<Map<String, Object>> response = new LinkedList<>();
            if (hasID(filter)) {
                final Map<String, Object> item = this.collection().get(filter.get(ID));
                if (null != item) {
                    response.add(item);
                }
            } else {
                this.collection().forEach((id, item) -> {
                    if (FilterUtils.match(item, filter)) {
                        response.add(item);
                    }
                });
            }
            return response;
        } else {
            return new ArrayList<>();
        }
    }

    public Map<String, Object> findOne(final Map<String, Object> filter) {
        if (this.active()) {
            if (hasID(filter)) {
                return this.collection().get(filter.get(ID));
            } else {
                final List<Map<String, Object>> response = new ArrayList<>();
                try {
                    this.collection().forEach((key, item) -> {
                        if (FilterUtils.match(item, filter)) {
                            response.add(item);
                            throw new RuntimeException("found item");
                        }
                    });
                } catch (Throwable ignored) {
                }
                return response.size() > 0 ? response.get(0) : null;
            }
        } else {
            return null;
        }
    }

    public boolean exists(final Object id) {
        if (this.active()) {
            return collection().containsKey(id);
        } else {
            return false;
        }
    }

    public Map<String, Object> upsert(final Map<String, Object> item) {
        try {
            if (this.active()) {
                if (hasID(item)) {
                    if (this.exists(item.get(ID))) {
                        // update
                        return this.updateOne(MapBuilder.create(String.class, Object.class).put(ID, item.get(ID)).toMap(), item);
                    } else {
                        // insert
                        return this.insert(item);
                    }
                } else {
                    // insert
                    return this.insert(item);
                }
            }
        } catch (Throwable t) {
            if (t instanceof IOError) {
                throw new RuntimeException(t.getMessage());
            }
        }
        return null;
    }

    public Map<String, Object> insert(final Map<String, Object> item) throws ItemAlreadyExistsException {
        try {
            if (this.active()) {
                if (!hasID(item)) {
                    item.put(ID, RandomUtils.randomUUID(true));
                }
                final ConcurrentSortedMap<Object, Map<String, Object>> collection = this.collection();
                if (collection.containsKey(item.get(ID))) {
                    // error, item exists
                    throw new ItemAlreadyExistsException(item.get(ID), _name);
                } else {
                    collection.put(item.get(ID), item);
                }
                this.autoCommit();
                return item;
            }
        } catch (Throwable t) {
            if (t instanceof IOError) {
                throw new RuntimeException(t.getMessage());
            }
        }
        return null;
    }

    public Collection<Map<String, Object>> update(final Map<String, Object> filter,
                                                  final Map<String, Object> data) throws ItemAlreadyExistsException {
        try {
            if (this.active()) {
                final Collection<Map<String, Object>> items = this.find(filter);
                for (final Map<String, Object> item : items) {
                    merge(item, data);
                }
                this.autoCommit();
                return items;
            }
        } catch (Throwable t) {
            if (t instanceof IOError) {
                throw new RuntimeException(t.getMessage());
            }
        }
        return null;
    }

    public Map<String, Object> updateOne(final Map<String, Object> filter,
                                         final Map<String, Object> data) {
        try {
            if (this.active()) {
                final Map<String, Object> item = this.findOne(filter);
                if (null != item) {
                    merge(item, data);
                }
                this.autoCommit();
                return item;
            }
        } catch (Throwable t) {
            if (t instanceof IOError) {
                throw new RuntimeException(t.getMessage());
            }
        }
        return null;
    }

    public void clear() {
        if (this.active()) {
            this.collection().clear();
            this.autoCommit();
        }
    }

    public Collection<Map<String, Object>> remove(final Map<String, Object> filter) {
        final LinkedList<Map<String, Object>> response = new LinkedList<>();
        if (this.active()) {
            if (hasID(filter)) {
                response.add(this.collection().remove(filter.get(ID)));
            } else {
                final ConcurrentSortedMap<Object, Map<String, Object>> collection = this.collection();
                collection.forEach((key, item) -> {
                    if (FilterUtils.match(item, filter)) {
                        response.add(item);
                        collection.remove(item.get(ID));
                    }
                });
            }
            this.autoCommit();
        }
        return response;
    }

    public Map<String, Object> removeOne(final Map<String, Object> filter) {
        final List<Map<String, Object>> response = new ArrayList<>();
        if (this.active()) {
            if (hasID(filter)) {
                response.add(this.collection().remove(filter.get(ID)));
            } else {
                try {
                    final ConcurrentSortedMap<Object, Map<String, Object>> collection = this.collection();
                    collection.forEach((key, item) -> {
                        if (FilterUtils.match(item, filter)) {
                            response.add(item);
                            collection.remove(item.get(ID));
                            throw new RuntimeException("found item");
                        }
                    });
                } catch (Throwable ignored) {
                }
            }
            this.autoCommit();
        }
        return response.size() > 0 ? response.get(0) : null;
    }

    public long count() {
        if (this.active()) {
            return this.collection().size();
        } else {
            return 0;
        }
    }

    public long count(final Map<String, Object> filter) {
        if (this.active()) {
            if (null == filter || filter.size() == 0) {
                return this.count();
            } else {
                if (hasID(filter)) {
                    return 1;
                } else {
                    final Counter count = new Counter();
                    final ConcurrentSortedMap<Object, Map<String, Object>> collection = this.collection();
                    collection.forEach((key, item) -> {
                        if (FilterUtils.match(item, filter)) {
                            count.inc();
                        }
                    });
                    return count.value();
                }
            }
        } else {
            return 0;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean active() {
        return null != _db;
    }

    private ConcurrentSortedMap<Object, Map<String, Object>> collection() {
        return _db.getTreeMap(_name);
    }

    private void autoCommit() {
        if (this.active() && !_enable_transactions) {
            this.commit();
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static boolean hasID(final Map<String, Object> item) {
        return null != item && item.containsKey(ID);
    }

    private static void merge(final Map<String, Object> item, final Map<String, Object> data) {
        if (null != item && null != data) {
            for (final Map.Entry<String, Object> entry : data.entrySet()) {
                item.put(entry.getKey(), entry.getValue());
            }
        }
    }

}
