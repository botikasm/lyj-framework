package org.lyj.ext.mongo.service;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.lyj.commons.Delegates;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.mongo.LyjMongo;

import static com.mongodb.client.model.Filters.eq;

/**
 *
 */
public abstract class AbstractService {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static final String ID = "_id";

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected abstract String getConnectionName();

    protected abstract String getDatabaseName();

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
                         final Delegates.SingleResultCallback<Long> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
                collection.count(filter, (count, error) -> {
                    if (null == error) {
                        final Long value = ConversionUtils.toLong(count);
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
                        final Delegates.SingleResultCallback<JSONArray> callback) {
        this.find(collection_name, filter, 0, 0, null, callback);
    }

    protected void find(final String collection_name, final Bson filter, final int skip, final int limit,
                        final Bson sort, final Delegates.SingleResultCallback<JSONArray> callback) {
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
                final JSONArray array = new JSONArray();
                FindIterable<Document> iterable = collection.find().filter(filter).skip(skip).limit(limit);
                if (null != sort) {
                    iterable = iterable.sort(sort);
                }
                iterable.forEach((document) -> {
                    array.put(document);
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
        this.getCollection(collection_name, (err, collection) -> {
            if (null == err) {
                collection.find(eq(ID, id)).first((final Object item, final Throwable error) -> {
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

}
