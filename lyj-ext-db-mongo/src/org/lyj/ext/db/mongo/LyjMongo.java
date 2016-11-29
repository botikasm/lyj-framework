package org.lyj.ext.db.mongo;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.mongo.connection.LyjMongoConnection;
import org.lyj.ext.db.mongo.schema.LyjMongoSchemas;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Main Factory
 */
public class LyjMongo {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, JsonWrapper> _configurations;

    private final Map<String, LyjMongoConnection> _connections; // connections cache

    private final LyjMongoSchemas _schemas;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private LyjMongo() {
        _configurations = new HashMap<>();
        _connections = new HashMap<>();
        _schemas = new LyjMongoSchemas();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public final void init(final String name, final String json) {
        final JsonWrapper wrap = new JsonWrapper(json);
        if (wrap.isJSONObject()) {
            this.init(wrap);
        }
    }

    public final void init(final JSONObject json) {
        final JsonWrapper wrap = new JsonWrapper(json);
        if (wrap.isJSONObject()) {
            this.init(wrap);
        }
    }

    public final void init(final Object json) {
        if (null != json) {
            this.init(new JsonWrapper(StringUtils.toString(json)));
        }
    }

    public void init(final JsonWrapper config) {
        if (null != config) {
            this.getLogger().info("MONGO: reading configuration -> " + config.toString());
            final Set<String> keys = config.keys();
            for (String key : keys) {
                final JSONObject value = config.getJSONObject(key);
                if (null != value) {
                    _configurations.put(key, new JsonWrapper(value));
                }
            }
            // log configuration completed
            this.getLogger().info("Mongo Configuration Completed");
        } else {
            this.getLogger().error("MONGO: Missing configuration!");
        }
    }

    public final boolean isInitialized() {
        return _configurations.size() > 0;
    }

    public LyjMongoConnection getConnection(final String name) {
        synchronized (_connections) {
            if (_connections.containsKey(name)) {
                return _connections.get(name);
            } else {
                final JsonWrapper config = _configurations.get(name);
                if (null != config) {
                    _connections.put(name, new LyjMongoConnection(config));
                    return _connections.get(name);
                } else {
                    return null;
                }
            }
        }
    }

    public void getDatabase(final String name, final Delegates.SingleResultCallback<MongoDatabase> callback) {
        this.getDatabase(name, name, callback);
    }

    public void getDatabase(final String connectionName, final String databaseName, final Delegates.SingleResultCallback<MongoDatabase> callback) {
        LyjMongoConnection connection = this.getConnection(connectionName);
        if (null != connection) {
            connection.getDatabase(databaseName, callback);
        } else {
            Delegates.invoke(callback, new Exception("Connection not found: '" + connectionName + "'"), null);
        }
    }

    public com.mongodb.client.MongoDatabase getDatabase(final String name) throws Exception {
        return this.getDatabase(name, name);
    }

    public com.mongodb.client.MongoDatabase getDatabase(final String connectionName, final String databaseName) throws Exception {
        LyjMongoConnection connection = this.getConnection(connectionName);
        if (null != connection) {
            return connection.getDatabase(databaseName);
        } else {
           throw new Exception("Connection not found: '" + connectionName + "'");
        }
    }

    public <T> void getCollection(final String databaseName,
                              final String collectionName,
                              final Delegates.SingleResultCallback<MongoCollection<T>> callback) {
        this.getCollection(databaseName, databaseName, collectionName, callback);
    }

    public <T> void getCollection(final String connectionName,
                              final String databaseName,
                              final String collectionName,
                              final Delegates.SingleResultCallback<MongoCollection<T>> callback) {
        LyjMongoConnection connection = this.getConnection(connectionName);
        if (null != connection) {
            connection.getCollection(databaseName, collectionName, callback);
        } else {
            Delegates.invoke(callback, new Exception("Connection not found: '" + connectionName + "'"), null);
        }
    }

    public <T> com.mongodb.client.MongoCollection<T> getCollection(final String connectionName,
                                                            final String databaseName,
                                                            final String collectionName) throws Exception {
        LyjMongoConnection connection = this.getConnection(connectionName);
        if (null != connection) {
            return connection.getCollection(databaseName, collectionName);
        } else {
            throw new Exception("Connection not found: '" + connectionName + "'");
        }
    }

    public LyjMongoSchemas getSchemas() {
        return _schemas;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void initCodecs() {
        /**
         CodecRegistry codecRegistry =
         CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
         MongoClient.getDefaultCodecRegistry());
         */
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static LyjMongo __instance;

    public static LyjMongo getInstance() {
        if (null == __instance) {
            __instance = new LyjMongo();
        }
        return __instance;
    }

}
