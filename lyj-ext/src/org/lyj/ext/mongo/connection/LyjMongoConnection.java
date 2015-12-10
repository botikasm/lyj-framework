package org.lyj.ext.mongo.connection;


import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.*;
import com.mongodb.connection.ClusterSettings;
import org.json.JSONObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;

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
    private MongoClient _client;
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
            for (JSONObject item : data) {
                JsonWrapper wrap = new JsonWrapper(item);
                String auth = wrap.getString(AUTH);
                String username = wrap.getString(USER);
                String password = wrap.getString(PSW);
                String database = wrap.getString(DATABASE);
                if (StringUtils.hasText(username) && StringUtils.hasText(password) && StringUtils.hasText(database)) {
                    _credentials.add(this.getCredential(auth, database, username, password));
                }
            }
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

    public Throwable getConnectionError(){
        return _error;
    }

    public void getDatabase(final String name, final Delegates.SingleResultCallback<MongoDatabase> callback) {
        if (_enabled) {
            this.getClient((err, client) -> {
                if (null == err) {
                    if (null != client) {
                        final MongoDatabase db = client.getDatabase(name);
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

    public void getDatabaseNames(final Delegates.SingleResultCallback<List<String>> callback) {
        if (_enabled) {
            if (_database_names.size() > 0) {
                Delegates.invoke(callback, null, _database_names);
            } else {
                // getting client, implicitly generates a database names list
                this.getClient((err, client) -> {
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

    public void getCollection(final String databaseName,
                              final String collectionName,
                              final Delegates.SingleResultCallback<MongoCollection> callback) {
        if (_enabled) {
            this.getDatabase(databaseName, (err, db) -> {
                if (null == err) {
                    if (null != db) {
                        final MongoCollection collection = db.getCollection(collectionName);
                        if (null!=collection) {
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

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private MongoCredential getCredential(final String auth, final String database, final String username,
                                          final String password) {
        if (CREDENTIAL_MONGODB_CR.equals(auth)) {
            MongoCredential.createMongoCRCredential(username,
                    database,
                    password.toCharArray());
        } else if (CREDENTIAL_SCRAM_SHA_1.equals(auth)) {
            MongoCredential.createScramSha1Credential(username,
                    database,
                    password.toCharArray());
        }
        return MongoCredential.createCredential(username, database, password.toCharArray());
    }

    private MongoClientSettings getSettings() {
        // hosts
        ClusterSettings clusterSettings = ClusterSettings.builder()
                .hosts(_hosts)
                .description(_description).build();

        return MongoClientSettings.builder().credentialList(_credentials).clusterSettings(clusterSettings).build();
    }

    private void getClient(final Delegates.SingleResultCallback<MongoClient> callback) {
        if (null == _client) {
            _client = MongoClients.create(this.getSettings());
            MongoIterable<String> names = _client.listDatabaseNames();
            names.forEach((String s) -> {
                _database_names.add(s);
            }, (Void aVoid, Throwable t) -> {
                if (null != t) {
                    getLogger().error("Connection Error", t);
                    _status = STATUS_ERROR;
                    _error = t;
                } else {
                    getLogger().info("Successfully Connected to Databases: " + CollectionUtils.toCommaDelimitedString(_database_names));
                    _status = STATUS_CONNECTED;
                    _error = null;
                }
                Delegates.invoke(callback, t, _client);
            });
        } else {
            Delegates.invoke(callback, null, _client);
        }
    }

    private void initConnection() {
        MongoClientSettings settings = this.getSettings();

    }

}
