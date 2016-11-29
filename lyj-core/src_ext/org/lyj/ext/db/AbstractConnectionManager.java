package org.lyj.ext.db;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.db.configuration.DatabaseConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Connection container.
 */
public abstract class AbstractConnectionManager
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, DatabaseConfiguration> _configurations;
    private final Map<String, IDatabaseConnection> _connections;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractConnectionManager() {
        _configurations = new HashMap<>();
        _connections = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void add(final String name, final DatabaseConfiguration configuration) {
        _configurations.put(name, configuration);
    }

    public boolean contains(final String name) {
        return _configurations.containsKey(name);
    }

    public void remove(final String name) {
        _connections.remove(name);
        _configurations.remove(name);
    }

    public IDatabaseConnection connection(final String name) {
        if (!_connections.containsKey(name)) {
            // add connection from configuration
            if (_configurations.containsKey(name)) {
                // create new connection
                final IDatabaseConnection connection = this.create(_configurations.get(name));
                if (null != connection) {
                    _connections.put(name, connection);
                }
            }
        }
        return _connections.get(name);
    }

    public void close() {
        if (!_connections.isEmpty()) {
            _connections.values().forEach(IDatabaseConnection::close);
        }
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    protected abstract IDatabaseConnection create(final DatabaseConfiguration configuration);

}
