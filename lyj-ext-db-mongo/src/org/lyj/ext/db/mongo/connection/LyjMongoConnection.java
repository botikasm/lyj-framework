package org.lyj.ext.db.mongo.connection;


import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.connection.ClusterSettings;
import org.bson.BsonDocument;
import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;

import java.util.LinkedList;
import java.util.List;


/**
 * Connection wrapper to native mongo
 */
public class LyjMongoConnection {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static final String STATUS_NOT_CONNECTED = "not_connected";
    public static final String STATUS_CONNECTED = "connected";
    public static final String STATUS_ERROR = "error";

    private static final String ENABLED = "enabled";
    private static final String HOSTS = "hosts";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String CREDENTIALS = "credentials";
    private static final String AUTH = "auth";
    private static final String USER = "username";
    private static final String PSW = "password";
    private static final String DATABASE = "database";
    private static final String DESCRIPTION = "description";

    private static final String CREDENTIAL_NONE = "none";
    private static final String CREDENTIAL_SCRAM_SHA_1 = "SCRAM-SHA-1";
    private static final String CREDENTIAL_MONGODB_CR = "MONGODB-CR";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _raw_config;
    private final Boolean _enabled;
    private final List<ServerAddress> _hosts;
    private final List<MongoCredential> _credentials;
    private final String _description;

    private String _status;
    private Throwable _error;
    private com.mongodb.async.client.MongoClient _client_async;
    private com.mongodb.MongoClient _client;
    private List<String> _database_names;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LyjMongoConnection(final JsonWrapper configuration) {

        _status = STATUS_NOT_CONNECTED;

        _database_names = new LinkedList<>();

        _raw_config = configuration.toString();
        _enabled = configuration.getBoolean(ENABLED);
        _description = configuration.getString(DESCRIPTION);

        // hosts
        _hosts = new LinkedList<>();
        if (_enabled) {
            JSONObject[] data = JsonWrapper.toArrayOfJSONObject(configuration.getJSONArray(HOSTS));
            for (JSONObject item : data) {
                JsonWrapper wrap = new JsonWrapper(item);
                String host = wrap.getString(HOST);
                int port = wrap.getInt(PORT);
                if (StringUtils.hasText(host) && port > 0) {
                    _hosts.add(new ServerAddress(host, port));
                }
            }
        }

        // credentials
        _credentials = new LinkedList<>();
        if (_enabled) {
            JSONObject[] data = JsonWrapper.toArrayOfJSONObject(configuration.getJSONArray(CREDENTIALS));
            this.fillCredentials(data, _credentials);
        }

        this.getLogger().info("Mongo Connection Initialized: " + this.toString());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " +
                _raw_config;
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public String getStatus() {
        return _status;
    }

    public boolean isConnected() {
        return _status.equals(STATUS_CONNECTED);
    }

    public boolean isNotConnected() {
        return _status.equals(STATUS_NOT_CONNECTED);
    }

    public boolean isConnectionError() {
        return _status.equals(STATUS_ERROR);
    }

    public Throwable getConnectionError() {
        return _error;
    }

    public void getDatabase(final String name, final Delegates.SingleResultCallback<com.mongodb.async.client.MongoDatabase> callback) {
        if (_enabled) {
            this.getAsyncClient((err, client) -> {
                if (null == err) {
                    if (null != client) {
                        final com.mongodb.async.client.MongoDatabase db = client.getDatabase(name);
                        if (null != db) {
                            Delegates.invoke(callback, null, db);
                        } else {
                            Delegates.invoke(callback, new Exception("Database not found: '" + name + "'"), null);
                        }
                    } else {
                        Delegates.invoke(callback, new Exception("Missing Client Response."), null);
                    }
                } else {
                    Delegates.invoke(callback, err, null);
                }
            });
        } else {
            Delegates.invoke(callback, new Exception("This Connection is not enabled."), null);
        }
    }

    public MongoDatabase getDatabase(final String name) throws Exception {
        final MongoClient client = getClient();
        if (null != client) {
            final MongoDatabase database = client.getDatabase(name);
            if (null != database) {
                return database;
            } else {
                throw new Exception("Database not found: '" + name + "'");
            }
        } else {
            throw new Exception("Missing Client Response.");
        }
    }

    public void getDatabaseNames(final Delegates.SingleResultCallback<List<String>> callback) {

        if (_enabled) {
            if (_database_names.size() > 0) {
                Delegates.invoke(callback, null, _database_names);
            } else {
                // getting client, implicitly generates a database names list
                this.getAsyncClient((err, client) -> {
                    if (null == err) {
                        if (null != client) {
                            Delegates.invoke(callback, null, _database_names);
                        } else {
                            Delegates.invoke(callback, new Exception("Missing Client Response."), null);
                        }
                    } else {
                        Delegates.invoke(callback, err, null);
                    }
                });
            }
        } else {
            Delegates.invoke(callback, new Exception("This Connection is not enabled."), null);
        }
    }

    public List<String> getDatabaseNames() throws Exception {
        if (_database_names.size() > 0) {
            return _database_names;
        } else {
            final MongoClient client = getClient();
            if (null != client) {
                try {
                    final MongoIterable<String> names = _client.listDatabaseNames();
                    names.forEach((Block<String>) s -> {
                        _database_names.add(s);
                    });
                } catch (Throwable t) {
                    if (!this.manageException("listDatabaseNames", t)) {
                        // GRAVE ERROR
                        _status = STATUS_ERROR;
                        _error = t;
                    }
                }
                return _database_names;
            } else {
                throw new Exception("Missing Client Response.");
            }
        }
    }

    public <T> void getCollection(final String databaseName,
                                  final String collectionName,
                                  final Delegates.SingleResultCallback<com.mongodb.async.client.MongoCollection<T>> callback) {
        if (_enabled) {
            this.getDatabase(databaseName, (err, db) -> {
                if (null == err) {
                    if (null != db) {
                        final com.mongodb.async.client.MongoCollection collection = db.getCollection(collectionName);
                        if (null != collection) {
                            Delegates.invoke(callback, null, collection);
                        } else {
                            Delegates.invoke(callback,
                                    new Exception(FormatUtils.format("Collection '%s' not found in database '%s'.",
                                            collectionName, databaseName)), null);
                        }
                    } else {
                        Delegates.invoke(callback, new Exception("Missing Client Response."), null);
                    }
                } else {
                    Delegates.invoke(callback, err, null);
                }
            });
        } else {
            Delegates.invoke(callback, new Exception("This Connection is not enabled."), null);
        }
    }

    public <T> MongoCollection<T> getCollection(final String databaseName,
                                                final String collectionName) throws Exception {
        final MongoDatabase database = getDatabase(databaseName);
        if (null != database) {
            final MongoCollection collection = database.getCollection(collectionName);
            if (null != collection) {
                return collection;
            } else {
                throw new Exception(FormatUtils.format("Collection '%s' not found in database '%s'.",
                        collectionName, databaseName));
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void fillCredentials(final JSONObject[] configCredentials, final List<MongoCredential> out) {
        for (JSONObject item : configCredentials) {
            JsonWrapper wrap = new JsonWrapper(item);
            String auth = wrap.getString(AUTH);
            String username = wrap.getString(USER);
            String password = wrap.getString(PSW);
            String database = wrap.getString(DATABASE);
            if (StringUtils.hasText(username)
                    && StringUtils.hasText(password)
                    && StringUtils.hasText(database)) {
                final MongoCredential credential = this.getCredential(auth, database, username, password);
                if (null != credential) {
                    out.add(credential);
                }
            }
        }
    }

    private MongoCredential getCredential(final String auth, final String database, final String username,
                                          final String password) {
        final MongoCredential result;
        if (CREDENTIAL_NONE.equals(auth)) {
            result = null;
        } else if (CREDENTIAL_MONGODB_CR.equals(auth)) {
            result = MongoCredential.createMongoCRCredential(username,
                    database,
                    password.toCharArray());
        } else if (CREDENTIAL_SCRAM_SHA_1.equals(auth)) {
            result = MongoCredential.createScramSha1Credential(username,
                    database,
                    password.toCharArray());
        } else {
            result = MongoCredential.createCredential(username, database, password.toCharArray());
        }
        return result;
    }

    private com.mongodb.async.client.MongoClientSettings getAsyncSettings() {
        // hosts
        ClusterSettings clusterSettings = ClusterSettings.builder()
                .hosts(_hosts)
                .description(_description).build();

        if (_credentials.size() > 0) {
            return com.mongodb.async.client.MongoClientSettings.builder().credentialList(_credentials).clusterSettings(clusterSettings).build();
        } else {
            return com.mongodb.async.client.MongoClientSettings.builder().clusterSettings(clusterSettings).build();
        }
    }


    private synchronized void getAsyncClient(final Delegates.SingleResultCallback<com.mongodb.async.client.MongoClient> callback) {
        if (null == _client_async) {

            _client_async = com.mongodb.async.client.MongoClients.create(this.getAsyncSettings());

            final com.mongodb.async.client.MongoIterable<String> names = _client_async.listDatabaseNames();
            names.forEach((String s) -> {
                _database_names.add(s);
            }, (Void aVoid, Throwable t) -> {
                _status = STATUS_CONNECTED;
                _error = null;
                if (null != t) {
                    if (!this.manageException("listDatabaseNames", t)) {
                        // GRAVE ERROR
                        _status = STATUS_ERROR;
                        _error = t;
                    }
                } else {
                    getLogger().info("Successfully Connected to Databases: " + CollectionUtils.toCommaDelimitedString(_database_names));
                }
                Delegates.invoke(callback, _error, _client_async);
            });
        } else {
            Delegates.invoke(callback, null, _client_async);
        }
    }

    private synchronized com.mongodb.MongoClient getClient() {
        if (null == _client) {
            try {
                _client = new MongoClient(_hosts, _credentials);

                _status = STATUS_CONNECTED;
                _error = null;
                getLogger().info("Successfully Connected to Databases: " + CollectionUtils.toCommaDelimitedString(_database_names));
            } catch (Throwable t) {
                _status = STATUS_ERROR;
                _error = t;
            }
        }
        return _client;
    }

    private boolean manageException(final String action, final Throwable t) {
        boolean managed = true;
        if (t instanceof MongoTimeoutException) {
            // SEVERE
            final String message = FormatUtils.format("Unable to connect to database executing action '%s': %s", action, t);
            getLogger().error(message, t);
            managed = false;
        } else if (t instanceof MongoSecurityException) {
            // WARNING
            getLogger().warning("Security Error occurred executing action '%s': %s",
                    action, ExceptionUtils.getRealMessage(t));
            managed = false;
        } else if (t instanceof MongoCommandException) {
            final BsonDocument response = ((MongoCommandException) t).getResponse();
            if (null != response) {
                final JsonWrapper wrap = new JsonWrapper(response.toJson());
                final String errmsg = wrap.getString("errmsg");
                if ("unauthorized".equals(errmsg)) {
                    // MANAGED
                    getLogger().info("Successfully Connected to Database, but client is not " +
                            "authorized and cannot execute action '%s'.", action);
                } else {
                    // WARNING
                    getLogger().warning("Command Error occurred executing action '%s': %s",
                            action, ExceptionUtils.getRealMessage(t));
                    managed = false;
                }
            } else {
                // WARNING
                getLogger().warning("Command Error occurred executing action '%s': %s",
                        action, ExceptionUtils.getRealMessage(t));
                managed = false;
            }

        } else {
            // SEVERE
            final String message = FormatUtils.format("Error '%s' occurred: %s",
                    t.getClass().getSimpleName(), ExceptionUtils.getRealMessage(t));
            getLogger().error(message, t);
            managed = false;
        }
        return managed;
    }


}
