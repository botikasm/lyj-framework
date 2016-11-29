package org.lyj.ext.db.mongo.service;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.AggregateIterable;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.lyj.commons.Delegates;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.mongo.ILyjMongoConstants;
import org.lyj.ext.db.mongo.LyjMongo;
import org.lyj.ext.db.mongo.utils.LyjMongoObjects;

import java.util.LinkedList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 *
 */
public abstract class AbstractService
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static final String ID = ILyjMongoConstants.F_ID;

    private static final String $PUSH = ILyjMongoConstants.$PUSH;

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    public abstract String getConnectionName();

    public abstract String getDatabaseName();

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void runCommandAsync(final Bson command, final Delegates.SingleResultCallback<Object> callback) {
        this.getDatabaseAsync((err, db) -> {
            if (null == err) {
                db.runCommand(command, (result, t) -> {
                    if (null != t) {
                        Delegates.invoke(callback, t, null);
                    } else {
                        Delegates.invoke(callback, null, result);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    public Document runCommand(final Bson command) throws Exception {
        final com.mongodb.client.MongoDatabase database = this.getDatabase();
        return database.runCommand(command);
    }

    // ------------------------------------------------------------------------
    //                      l o g g i n g
    // ------------------------------------------------------------------------

    // log emitter

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    public String UUID() {
        return RandomUtils.randomUUID(true);
    }

    protected String toMD5(final String text) {
        if (StringUtils.hasText(text)) {
            return MD5.encode(text).toLowerCase();
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      a s y n c
    // ------------------------------------------------------------------------

    protected void getDatabaseAsync(final Delegates.SingleResultCallback<MongoDatabase> callback) {
        LyjMongo.getInstance().getDatabase(this.getConnectionName(), this.getDatabaseName(), callback);
    }

    protected <T> void getCollectionAsync(final String collectionName,
                                          final Delegates.SingleResultCallback<MongoCollection<T>> callback) {
        LyjMongo.getInstance().getCollection(this.getConnectionName(), this.getDatabaseName(), collectionName, callback);
    }

    protected void countAsync(final String collection_name, final Bson afilter,
                              final Delegates.SingleResultCallback<Integer> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                final Bson filter = LyjMongoObjects.notNull(afilter);
                collection.count(filter, (count, error) -> {
                    if (null == error) {
                        final int value = ConversionUtils.toInteger(count);
                        Delegates.invoke(callback, null, value);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void findAsync(final String collection_name, final Bson filter,
                             final Delegates.SingleResultCallback<List<Document>> callback) {
        this.findAsync(collection_name, filter, 0, 0, null, callback);
    }

    protected void findAsync(final String collection_name, final Bson filter, final int skip, final int limit,
                             final Bson sort, final Delegates.SingleResultCallback<List<Document>> callback) {
        this.findAsync(collection_name, filter, skip, limit, sort, null, callback);
    }

    protected void findAsync(final String collection_name, final Bson afilter, final int skip, final int limit,
                             final Bson sort, final Bson projection,
                             final Delegates.SingleResultCallback<List<Document>> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                final List<Document> array = new LinkedList<Document>();
                final Bson filter = LyjMongoObjects.notNull(afilter);
                FindIterable iterable = collection.find().filter(filter).skip(skip).limit(limit);
                if (null != sort) {
                    iterable = iterable.sort(sort);
                }
                if (null != projection) {
                    iterable = iterable.projection(projection);
                }
                iterable.forEach((document) -> {
                    array.add((Document) document);
                }, (Void, error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, array);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected <T> void findFieldAsync(final String collection_name, final Bson filter, final int skip, final int limit,
                                      final Bson sort, final String fieldName, final boolean allow_duplicates,
                                      final Delegates.SingleResultCallback<List<T>> callback) {
        final Document projection = new Document(fieldName, 1);
        this.findAsync(collection_name, filter, skip, limit, sort, projection, (err, data) -> {
            if (null != err) {
                Delegates.invoke(callback, err, null);
            } else {
                final List<T> result = new LinkedList<>();
                for (final Document item : data) {
                    try {
                        final T value = (T) item.get(fieldName);
                        if (allow_duplicates || !result.contains(value)) {
                            result.add(value);
                        }
                    } catch (Throwable ignored) {
                    }
                }
                Delegates.invoke(callback, null, result);
            }
        });
    }

    protected void findOneAsync(final String collection_name, final Bson filter,
                                final Delegates.SingleResultCallback<Document> callback) {
        this.findOneAsync(collection_name, filter, null, callback);
    }

    protected void findOneAsync(final String collection_name,
                                final Bson filter,
                                final Bson projection,
                                final Delegates.SingleResultCallback<Document> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                FindIterable iterable = collection.find().filter(filter);
                if (null != projection) {
                    iterable = iterable.projection(projection);
                }
                iterable.first((item, error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, (Document) item);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void findByIdAsync(final String collection_name, final String id,
                                 final Delegates.SingleResultCallback<Document> callback) {
        this.findByIdAsync(collection_name, id, null, callback);
    }

    protected void findByIdAsync(final String collection_name, final String id,
                                 final Bson projection,
                                 final Delegates.SingleResultCallback<Document> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                FindIterable iterable = collection.find(eq(ID, id));
                if (!LyjMongoObjects.isEmpty(projection)) {
                    iterable = iterable.projection(projection);
                }
                iterable.first((item, error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, (Document) item);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void findIdsAsync(final String collection_name, final Bson filter,
                                final int skip, final int limit, final Bson sort,
                                final Delegates.SingleResultCallback<List<String>> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                final List<String> array = new LinkedList<String>();
                FindIterable iterable = collection.find().filter(filter).projection(Projections.include(ID)).skip(skip).limit(limit);
                if (null != sort) {
                    iterable = iterable.sort(sort);
                }
                iterable.forEach((document) -> {
                    array.add(((Document) document).getString(ID));
                }, (Void, error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, array);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void existsAsync(final String collection_name, final String id,
                               final Delegates.SingleResultCallback<Boolean> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                collection.count(eq(ID, id), (count, error) -> {
                    if (null == error) {
                        final Long value = ConversionUtils.toLong(count);
                        Delegates.invoke(callback, null, value > 0);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void updateAsync(final String collection_name, final String id, final Document data,
                               final Delegates.SingleResultCallback<Document> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                final Document action = new Document("$set", data);
                final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
                options.returnDocument(ReturnDocument.AFTER);
                collection.findOneAndUpdate(eq(ID, id), action, options, (final Object item, final Throwable error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, (Document) item);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void updateAsync(final String collection_name, final Bson filter, final Document data,
                               final Delegates.SingleResultCallback<UpdateResult> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                final Document action = new Document("$set", data);
                final UpdateOptions options = new UpdateOptions();
                options.upsert(true);
                collection.updateMany(filter, action, options, new SingleResultCallback<UpdateResult>() {
                    @Override
                    public void onResult(UpdateResult result, Throwable error) {
                        if (null == error) {
                            Delegates.invoke(callback, null, result);
                        } else {
                            Delegates.invoke(callback, error, null);
                        }
                    }
                });
                //collection.updateMany(filter, action, options, (UpdateResult result, Throwable error) -> {});
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void updateFieldAsync(final String collection_name, final String id, final String field_id,
                                    final Object field_value, final Delegates.SingleResultCallback<Document> callback) {
        final Document data = new Document(field_id, field_value);
        this.updateAsync(collection_name, id, data, callback);
    }

    protected void pushAsync(final String collection_name, final String id, final String fieldName, final Document data,
                             final Delegates.SingleResultCallback<UpdateResult> callback) {
        this.pushAsync(collection_name, id, new Document(fieldName, data), callback);
    }

    protected void pushAsync(final String collection_name, final String id, final Document data,
                             final Delegates.SingleResultCallback<UpdateResult> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                final Document update = new Document($PUSH, data);
                final Document filter = new Document(ID, id);
                collection.updateOne(filter, update, new SingleResultCallback<UpdateResult>() {
                    @Override
                    public void onResult(UpdateResult result, Throwable error) {
                        if (null == error) {
                            Delegates.invoke(callback, null, result);
                        } else {
                            Delegates.invoke(callback, error, null);
                        }
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void removeAsync(final String collection_name, final String id,
                               final Delegates.SingleResultCallback<Document> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                collection.findOneAndDelete(eq(ID, id), (document, error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, (Document) document);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void removeAllAsync(final String collection_name, final Bson filter,
                                  final Delegates.SingleResultCallback<DeleteResult> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                collection.deleteMany(filter, new SingleResultCallback<DeleteResult>() {
                    @Override
                    public void onResult(DeleteResult deleteResult, Throwable error) {
                        if (null == error) {
                            Delegates.invoke(callback, null, deleteResult);
                        } else {
                            Delegates.invoke(callback, error, null);
                        }
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void removeAllAndReturnIdsAsync(final String collection_name, final Bson filter,
                                              final Delegates.SingleResultCallback<List<String>> callback) {
        try {
            final List<String> id_list = this.findIds(collection_name, filter, 0, 0, null);
            this.removeAllAsync(collection_name, filter, (err, deleteResult) -> {
                if (null != err) {
                    Delegates.invoke(callback, err, null);
                } else {
                    Delegates.invoke(callback, null, id_list);
                }
            });
        } catch (Throwable t) {
            Delegates.invoke(callback, t, null);
        }
    }

    protected void insertAsync(final String collection_name, final Document item,
                               final Delegates.SingleResultCallback<Document> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                // check _id
                if (null == item.get(ID)) {
                    item.put(ID, this.UUID());
                }
                collection.insertOne(item, (Void, error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, item);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void insertAsync(final String collection_name, final List<Document> items,
                               final Delegates.SingleResultCallback<List<Document>> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                collection.insertMany(items, (Void, error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, items);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }

    protected void aggregateAsync(final String collection_name, final List<Document> pipeline,
                                  final Delegates.SingleResultCallback<List<Document>> callback) {
        this.getCollectionAsync(collection_name, (err, collection) -> {
            if (null == err) {
                final List<Document> array = new LinkedList<Document>();
                final AggregateIterable iterable = collection.aggregate(pipeline);
                iterable.forEach((document) -> {
                    array.add((Document) document);
                }, (Void, error) -> {
                    if (null == error) {
                        Delegates.invoke(callback, null, array);
                    } else {
                        Delegates.invoke(callback, error, null);
                    }
                });
            } else {
                Delegates.invoke(callback, err, null);
            }
        });
    }


    protected void upsertAsync(final String collection_name, final Document item,
                               final Delegates.SingleResultCallback<Document> callback) {
        final String id = item.getString(ID);
        if (StringUtils.hasText(id)) {
            this.findByIdAsync(collection_name, id, new Document(ID, 1), (err, existing) -> {
                if (null != err) {
                    Delegates.invoke(callback, err, null);
                } else {
                    if (null != existing) {
                        item.remove(ID);
                        this.updateAsync(collection_name, id, item, callback);
                    } else {
                        this.insertAsync(collection_name, item, callback);
                    }
                }
            });
        } else {
            Delegates.invoke(callback, new Exception("Unable to insert or update object: Missing ID"), null);
        }
    }

    // ------------------------------------------------------------------------
    //                      s y n c
    // ------------------------------------------------------------------------

    protected com.mongodb.client.MongoDatabase getDatabase() throws Exception {
        return LyjMongo.getInstance().getDatabase(this.getConnectionName(), this.getDatabaseName());
    }

    protected <T> com.mongodb.client.MongoCollection<T> getCollection(final String collectionName) throws Exception {
        return LyjMongo.getInstance().getCollection(this.getConnectionName(), this.getDatabaseName(), collectionName);
    }

    protected long count(final String collection_name, final Bson filter) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        return collection.count(filter);
    }

    protected List<Document> find(final String collection_name, final Bson filter) throws Exception {
        return this.find(collection_name, filter, 0, 0, null);
    }

    protected List<Document> find(final String collection_name, final Bson filter, final int skip, final int limit,
                                  final Bson sort) throws Exception {
        return this.find(collection_name, filter, skip, limit, sort, null);
    }

    protected List<Document> find(final String collection_name, final Bson afilter, final int skip, final int limit,
                                  final Bson sort, final Bson projection) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);

        final List<Document> array = new LinkedList<Document>();
        final Bson filter = LyjMongoObjects.notNull(afilter);
        com.mongodb.client.FindIterable<Document> iterable = collection.find().filter(filter).skip(skip).limit(limit);
        if (null != sort) {
            iterable = iterable.sort(sort);
        }
        if (null != projection) {
            iterable = iterable.projection(projection);
        }

        iterable.forEach((Block<Document>) document -> {
            if (null != document) {
                array.add(document);
            }
        });
        return array;
    }

    protected <T> List<T> findField(final String collection_name, final Bson afilter, final int skip, final int limit,
                                    final Bson sort, final String fieldName, final boolean allow_duplicates) throws Exception {
        final Bson projection = new Document(fieldName, 1);
        final Bson filter = LyjMongoObjects.notNull(afilter);
        final List<Document> data = this.find(collection_name, filter, skip, limit, sort, projection);
        final List<T> result = new LinkedList<>();
        for (final Document item : data) {
            try {
                final T value = (T) item.get(fieldName);
                if (allow_duplicates || !result.contains(value)) {
                    result.add(value);
                }
            } catch (Throwable ignored) {
            }
        }
        return result;
    }

    protected Document findOne(final String collection_name,
                               final Bson filter) throws Exception {
        return this.findOne(collection_name, filter, null);
    }

    protected Document findOne(final String collection_name,
                               final Bson afilter,
                               final Bson projection) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        final Bson filter = LyjMongoObjects.notNull(afilter);
        com.mongodb.client.FindIterable<Document> iterable = collection.find().filter(filter);
        if (null != projection) {
            iterable = iterable.projection(projection);
        }
        return iterable.first();
    }


    protected Document findById(final String collection_name, final String id) throws Exception {
        return this.findById(collection_name, id, new Document());
    }

    protected Document findById(final String collection_name, final String id, final Bson projection) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        com.mongodb.client.FindIterable<Document> iterable = collection.find(eq(ID, id));
        if (!LyjMongoObjects.isEmpty(projection)) {
            iterable = iterable.projection(projection);
        }
        return iterable.first();
    }

    protected List<String> findIds(final String collection_name, final Bson afilter,
                                   final int skip, final int limit, final Bson sort) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        final List<String> array = new LinkedList<String>();
        final Bson filter = LyjMongoObjects.notNull(afilter);
        com.mongodb.client.FindIterable<Document> iterable = collection.find().filter(filter).projection(Projections.include(ID)).skip(skip).limit(limit);
        if (null != sort) {
            iterable = iterable.sort(sort);
        }
        iterable.forEach((Block<Document>) document -> {
            array.add(document.getString(ID));
        });
        return array;
    }

    protected boolean exists(final String collection_name, final String id) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        return collection.count(eq(ID, id)) > 0;
    }

    protected Document update(final String collection_name, final String id, final Document data) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        final Document action = new Document("$set", data);
        final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);
        return collection.findOneAndUpdate(eq(ID, id), action, options);
    }

    protected UpdateResult update(final String collection_name, final Bson afilter, final Document data) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        final Document action = new Document("$set", data);
        final UpdateOptions options = new UpdateOptions();
        options.upsert(true);
        final Bson filter = LyjMongoObjects.notNull(afilter);
        return collection.updateMany(filter, action, options);
    }

    protected Document updateField(final String collection_name, final String id, final String field_id,
                                   final Object field_value) throws Exception {
        final Document data = new Document(field_id, field_value);
        return this.update(collection_name, id, data);
    }

    protected UpdateResult push(final String collection_name, final String id, final String fieldName, final Document data) throws Exception {
        return this.push(collection_name, id, new Document(fieldName, data));
    }

    protected UpdateResult push(final String collection_name, final String id, final Document data) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        final Document update = new Document($PUSH, data);
        final Document filter = new Document(ID, id);
        return collection.updateOne(filter, update);
    }

    protected Document remove(final String collection_name, final String id) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        return collection.findOneAndDelete(eq(ID, id));
    }

    protected DeleteResult removeAll(final String collection_name, final Bson filter) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        return collection.deleteMany(filter);

    }

    protected List<String> removeAllAndReturnIdsAsync(final String collection_name, final Document filter) throws Exception {
        final List<String> id_list = this.findIds(collection_name, filter, 0, 0, null);
        final DeleteResult deleteResult = this.removeAll(collection_name, filter);
        return id_list;
    }

    protected Document insert(final String collection_name, final Document item) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        // check _id
        if (null == item.get(ID)) {
            item.put(ID, this.UUID());
        }
        collection.insertOne(item);
        return item;
    }

    protected List<Document> insert(final String collection_name, final List<Document> items) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        collection.insertMany(items);
        return items;
    }

    protected List<Document> aggregate(final String collection_name, final List<Document> pipeline) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        final List<Document> array = new LinkedList<Document>();
        final com.mongodb.client.AggregateIterable<Document> iterable = collection.aggregate(pipeline);
        iterable.forEach((Block<Document>) array::add);
        return array;
    }

    protected Document upsert(final String collection_name, final Document item) throws Exception {
        final String id = item.getString(ID);
        if (StringUtils.hasText(id)) {
            final Document existing = this.findById(collection_name, id, new Document(ID, 1));
            if (null != existing) {
                item.remove(ID);
                return this.update(collection_name, id, item);
            } else {
                return this.insert(collection_name, item);
            }
        } else {
            throw new Exception("Unable to insert or update object: Missing ID");
        }
    }

    protected com.mongodb.client.FindIterable<Document> findIterable(final String collection_name,
                                                                     final Bson afilter,
                                                                     final int skip, final int limit,
                                                                     final Bson sort, final Bson projection) throws Exception {
        final com.mongodb.client.MongoCollection<Document> collection = this.getCollection(collection_name);
        final Bson filter = LyjMongoObjects.notNull(afilter);
        com.mongodb.client.FindIterable<Document> iterable = collection.find().filter(filter).skip(skip).limit(limit);
        if (null != sort) {
            iterable = iterable.sort(sort);
        }
        if (null != projection) {
            iterable = iterable.projection(projection);
        }
        return iterable;
    }

}
