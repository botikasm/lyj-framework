package org.lyj.ext.mongo.schema;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.geojson.GeoJsonObjectType;
import org.bson.Document;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.mongo.LyjMongo;

import java.util.*;

/**
 * Schema manager for a document.
 */
public abstract class AbstractSchema
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, LyjMongoIndex> _indexes;
    private final Map<String, LyjMongoField> _fields;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractSchema() {
        _indexes = new LinkedHashMap<>();
        _fields = new LinkedHashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    public abstract String getConnectionName();

    public abstract String getDatabaseName();

    public abstract String getCollectionName();

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void index(final String fieldName) {
        this.index(fieldName, false);
    }

    public void index(final String fieldName, final boolean unique) {
        if (StringUtils.hasText(fieldName)) {
            final LyjMongoIndex idx = new LyjMongoIndex(fieldName, 1);
            idx.setUnique(unique);
            _indexes.put(idx.getSignature(), idx);
        }
    }

    public void index(final Document index) {
        this.index(index, false);
    }

    public void index(final Document index, final boolean unique) {
        if (null != index) {
            final LyjMongoIndex idx = new LyjMongoIndex(index);
            idx.setUnique(unique);
            if (idx.hasFields()) {
                _indexes.put(idx.getSignature(), idx);
            }
        }
    }

    public void index(final Map<String, Integer> index) {
        this.index(index, false);
    }

    public void index(final Map<String, Integer> index, final boolean unique) {
        if (null != index) {
            final LyjMongoIndex idx = new LyjMongoIndex(index);
            idx.setUnique(unique);
            if (idx.hasFields()) {
                _indexes.put(idx.getSignature(), idx);
            }
        }
    }

    public void indexGeo(final String geoSpatialFieldName) {
        this.indexGeo(geoSpatialFieldName, null);
    }

    public void indexGeo(final String geoSpatialFieldName, final Document compound) {
        if (StringUtils.hasText(geoSpatialFieldName)) {
            final Document index = null == compound ? new Document() : compound;
            index.put(geoSpatialFieldName, LyjMongoIndex.SPHERE_INDEX);
            final LyjMongoIndex idx = new LyjMongoIndex(index);
            idx.setUnique(false);
            if (idx.hasFields()) {
                _indexes.put(idx.getSignature(), idx);
            }
        }
    }

    public LyjMongoField field(final LyjMongoField field) {
        _fields.put(field.getName(), field);

        // add index
        if (field.isIndex()) {
            this.index(field.getName(), field.isUnique());
        }
        return field;
    }

    public LyjMongoField[] fields() {
        final Collection<LyjMongoField> result = _fields.values();
        return result.toArray(new LyjMongoField[result.size()]);
    }

    public boolean hasField(final String name) {
        return _fields.containsKey(name);
    }

    public void ensureIndexes(final Delegates.SingleResultCallback<List<Document>> callback) {
        this.initIndexes(callback);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected final void getCollection(final Delegates.SingleResultCallback<MongoCollection> callback) {
        LyjMongo.getInstance().getCollection(this.getConnectionName(),
                this.getDatabaseName(), this.getCollectionName(), callback);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initIndexes(final Delegates.SingleResultCallback<List<Document>> callback) {
        final Collection<Task<LyjMongoIndex>> tasks = new ArrayList<>();
        final Collection<LyjMongoIndex> indexes = _indexes.values();
        for (final LyjMongoIndex index : indexes) {
            tasks.add(this.initIndex(index));
        }

        // run and wait
        Async.joinAll(tasks);

        // create response
        final List<Document> response = new LinkedList<>();

        // get response
        for (final Task<LyjMongoIndex> task : tasks) {
            final LyjMongoIndex index = task.getSilent();
            if (null != index) {
                final Document item = Document.parse(index.toString());
                response.add(item);
                if (task.hasError()) {
                    item.put("error", ExceptionUtils.getMessage(task.getError()));
                }
            }
        }

        Delegates.invoke(callback, null, response);
    }

    private Task<LyjMongoIndex> initIndex(final LyjMongoIndex index) {
        return new Task<LyjMongoIndex>((final Task<LyjMongoIndex> task) -> {
            final Set<String> names = index.getFieldNames();
            if (index.hasFields()) {
                this.getCollection((err, collection) -> {
                    if (null != err) {
                        task.fail(err);
                    } else {
                        this.createIndex(collection, index, (err1, response) -> {
                            if (null != err1) {
                                task.fail(err1, index);
                            } else {
                                index.setName(response);
                                task.success(index);
                            }
                        });
                    }
                });
            }
        });
    }

    private void createIndex(final MongoCollection collection, final LyjMongoIndex index,
                             final Delegates.SingleResultCallback<String> callback) {
        // mongo index
        final Document mongo_index = index.toDocument();
        final IndexOptions options = new IndexOptions();
        options.unique(index.isUnique());
        if (StringUtils.hasText(index.getName())) {
            options.name(index.getName());
        }

        // create new index or overwrite existing
        //collection.createIndex(mongo_index, options, (final String indexName, final Throwable err)->{});
        collection.createIndex(mongo_index, options, new SingleResultCallback<String>() {
            @Override
            public void onResult(String indexName, Throwable err) {
                Delegates.invoke(callback, err, indexName);
            }
        });
    }


}
