package org.lyj.commons.io.db.jdbm;

import net.kotek.jdbm.DB;
import net.kotek.jdbm.DBMaker;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.util.Set;

/**
 * Database Wrapper.
 * <p>
 * Remember to close connection before close app, or data will be corrupted.
 */
public class JDB {

    private final static String PRIMARY_KEY = "_id";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;

    private boolean _open;
    private String _secret;
    private boolean _enable_transactions;
    private DB _db;
    private Set<String> _collectionNames;
    private String _primary_key;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JDB(final String root) {
        _root = root;
        _secret = "password";
        _enable_transactions = false;
        _primary_key = PRIMARY_KEY;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public JDB secret(final String value) {
        _secret = value;
        return this;
    }

    public String secret() {
        return _secret;
    }

    public JDB enableTransaction(final boolean value) {
        _enable_transactions = value;
        return this;
    }

    public boolean enableTransaction() {
        return _enable_transactions;
    }

    public String primaryKey() {
        return _primary_key;
    }

    public JDB primaryKey(final String value) {
        if (StringUtils.hasText(value)) {
            _primary_key = value;
        }
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JDB open(final String name) {
        if (!_open) {
            try {
                final String db_root = PathUtils.concat(_root, name);
                FileUtils.mkdirs(PathUtils.getParent(db_root));
                _db = DBMaker.newFileDB(new File(db_root))
                        .closeOnJvmShutdown()
                        .encryptionEnable(_secret)
                        .make();
                _collectionNames = _db.getHashSet("_metadata");
                _open = true;
            } catch (Throwable t) {
                this.close();
                this.handleError(t);
            }
        }
        return this;
    }

    public JDB close() {
        try {
            if (null != _db) {
                _db.close();
            }
        } finally {
            _open = false;
        }
        return this;
    }

    public JDBCollection collection(final String name) {
        if (null != _db && _open) {
            this.addName(name);
            return new JDBCollection(_db, _enable_transactions, name, _primary_key);
        }
        return null;
    }

    public Set<String> collections() {
        return _collectionNames;
    }

    public boolean closed() {
        return !_open;
    }

    public void defrag() {
        if (_open) {
            _db.defrag();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handleError(final Throwable t) {

    }

    private void addName(final String collectionName) {
        synchronized (this) {
            _collectionNames.add(collectionName);
            _db.commit();
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


    public static JDB create(final String root) {
        return new JDB(root);
    }

}
