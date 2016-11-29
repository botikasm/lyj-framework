package org.lyj.commons.io.db.jdbm;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.IDatabaseCollection;

import java.util.Map;
import java.util.Set;

/**
 *
 */
public class LocalStorage
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String DEF_ROOT = "localstorage";
    private static final String DEF_DB = "db";
    private static final String FLD_ID = "_id";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final String _db_name;
    private final JDB _db;

    private boolean _open;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LocalStorage() {
        this(DEF_ROOT, DEF_DB);
    }

    public LocalStorage(final String root,
                        final String dbName) {

        _root = PathUtils.getAbsolutePath(root);
        _db_name = dbName;
        _open = false;

        // ensure root exists
        FileUtils.tryMkdirs(_root);

        // init database
        _db = JDB.create(_root).primaryKey(FLD_ID);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } finally {
            super.finalize();
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public LocalStorage primaryKey(final String value) {
        if (StringUtils.hasText(value)) {
            _db.primaryKey(value);
        }
        return this;
    }

    public String name() {
        return _db_name;
    }

    public boolean open() {
        if (!_open) {
            _open = true;

            // open database
            _db.open(_db_name);
        }
        return _open;
    }

    public boolean close() {
        if (_open) {
            _open = false;
            if (!_db.closed()) {
                _db.close();
            }
        }
        return _open;
    }

    public boolean isOpen() {
        return _open;
    }

    public String[] collectionNames() {
        if (null != _db) {
            final Set<String> names = _db.collections();
            return names.toArray(new String[names.size()]);
        }
        return new String[0];
    }

    public JDBCollection collection(final String name) {
        if (!_db.closed() && _open) {
            return _db.collection(name);
        }
        return null;
    }





}
