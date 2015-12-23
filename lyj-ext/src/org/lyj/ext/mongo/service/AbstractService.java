package org.lyj.ext.mongo.service;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.AggregateIterable;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.lyj.commons.Delegates;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.mongo.LyjMongo;

import java.util.LinkedList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 *
 */
public abstract class AbstractService
        extends AbstractLogEmitter{

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static final String ID = "_id";

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    public abstract String getConnectionName();

    public abstract String getDatabaseName();

    // ------------------------------------------------------------------------
    //                      l o g g i n g
    // ------------------------------------------------------------------------

    // log emitter

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void getCollection(final String collectionName,
                                 final Delegates.SingleResultCallback<MongoCollection> callback) {
        LyjMongo.getInstance().getCollection(this.getConnectionName(), this.getDatabaseName(), collectionName, callback);
    }

    protected String toMD5(final String text) {
        if (StringUtils.hasText(text)) {
            return MD5.encode(text).toLowerCase();
        }
        return "";
    }

    protected void count(final String collection_name, final Bson filter,
                         final Delegates.SingleResultCallback<Integer> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
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

    protected void find(final String collection_name, final Bson filter,
                        final Delegates.SingleResultCallback<List<Document>> callback) {
        this.find(collection_name, filter, 0, 0, null, callback);
    }

    protected void find(final String collection_name, final Bson filter, final int skip, final int limit,
                        final Bson sort, final Delegates.SingleResultCallback<List<Document>> callback) {
        this.find(collection_name, filter, skip, limit, sort, null, callback);
    }

    protected void find(final String collection_name, final Bson filter, final int skip, final int limit,
                        final Bson sort, final Bson projection, final Delegates.SingleResultCallback<List<Document>> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
                final List<Document> array = new LinkedList<Document>();
                FindIterable<Document> iterable = collection.find().filter(filter).skip(skip).limit(limit);
                if (null != sort) {
                    iterable = iterable.sort(sort);
                }
                if (null != projection) {
                    iterable = iterable.projection(projection);
                }
                iterable.forEach((document) -> {
                    array.add(document);
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

    protected <T> void findField(final String collection_name, final Bson filter, final int skip, final int limit,
                                 final Bson sort, final String fieldName, final boolean allow_duplicates,
                                 final Delegates.SingleResultCallback<List<T>> callback) {
        final Document projection = new Document(fieldName, 1);
        this.find(collection_name, filter, skip, limit, sort, projection, (err, data) -> {
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

    protected void findOne(final String collection_name, final Bson filter,
                           final Delegates.SingleResultCallback<Document> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
                collection.find(filter).first((final Object item, final Throwable error) -> {
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

    protected void findById(final String collection_name, final String id,
                            final Delegates.SingleResultCallback<Document> callback) {
        this.findById(collection_name, id, null, callback);
    }

    protected void findById(final String collection_name, final String id, final Bson projection,
                            final Delegates.SingleResultCallback<Document> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
                FindIterable<Document> iterable = collection.find(eq(ID, id));
                if (null != projection) {
                    iterable = iterable.projection(projection);
                }
                iterable.first((final Document item, final Throwable error) -> {
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

    protected void findIds(final String collection_name, final Bson filter, final int skip, final int limit, final Bson sort,
                           final Delegates.SingleResultCallback<List<String>> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
                final List<String> array = new LinkedList<String>();
                FindIterable<Document> iterable = collection.find().filter(filter).projection(Projections.include(ID)).skip(skip).limit(limit);
                if (null != sort) {
                    iterable = iterable.sort(sort);
                }
                iterable.forEach((document) -> {
                    array.add(document.getString(ID));
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

    protected void exists(final String collection_name, final String id,
                          final Delegates.SingleResultCallback<Boolean> callback) {
        this.getCollection(collection_name, (err, collection) -> {
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

    protected void update(final String collection_name, final String id, final Bson data,
                          final Delegates.SingleResultCallback<Document> callback) {
        this.getCollection(collection_name, (err, collection) -> {
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

    protected void update(final String collection_name, final Bson filter, final Bson data,
                          final Delegates.SingleResultCallback<UpdateResult> callback) {
        this.getCollection(collection_name, (err, collection) -> {
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

    protected void updateField(final String collection_name, final String id, final String field_id,
                               final Object field_value, final Delegates.SingleResultCallback<Document> callback) {
        final Document data = new Document(field_id, field_value);
        this.update(collection_name, id, data, callback);
    }

    protected void remove(final String collection_name, final String id,
                          final Delegates.SingleResultCallback<Document> callback) {
        this.getCollection(collection_name, (err, collection) -> {
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

    protected void insert(final String collection_name, final Document item,
                          final Delegates.SingleResultCallback<Document> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
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

    protected void aggregate(final String collection_name, final List<Document> pipeline,
                             final Delegates.SingleResultCallback<List<Document>> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
                final List<Document> array = new LinkedList<Document>();
                final AggregateIterable<Document> iterable = collection.aggregate(pipeline);
                iterable.forEach((document) -> {
                    array.add(document);
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

}
