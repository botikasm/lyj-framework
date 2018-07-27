package org.ly.ose.server.application.programming.tools.persistence;

import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.persistence.DBController;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.IDatabaseCollection;
import org.lyj.ext.script.utils.Converter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Database utility class
 * *
 * Usage:
 * $db.collection('test').find().....
 * $db.name('system_utils').collection('test').find()....
 * *
 * *
 */
public class Tool_db
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "db"; // used as $db.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _package_name;
    private String _db_name; // usually equals program name with a custom prefix. i.e. "ose_program_system_utils"

    private CollectionsController __collections;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_db(final OSEProgram program) {
        super(NAME, program);

        _package_name = super.info().namespace() + "_" + super.info().name();
        _db_name = _package_name; // program name
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {
        if (null != __collections) {
            __collections.close();
            __collections = null;
        }
    }

    public Tool_db name(final String name) throws Exception {
        // protect access to system databases
        if (OSEProgram.isProtected(name)) {
            throw new Exception(FormatUtils.format("Cannot assign name '%s' to database because this is a protected package.", name));
        }
        _db_name = DBController.DBNameProgram(name);
        return this;
    }

    public String name() {
        return StringUtils.replace(_db_name, IConstants.DB_PROGRAM_PREFIX, "");
    }

    /**
     * Returns a collection wrapper
     */
    public CollectionWrapper collection(final String name) {
        if (null == __collections) {
            __collections = new CollectionsController(this);
        }
        return __collections.collection(name);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * Controller for collections
     */
    private static class CollectionsController {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private final Tool_db _tool_db;
        private final Map<String, IDatabase> _databases;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------


        private CollectionsController(final Tool_db tool) {
            _tool_db = tool;

            _databases = new HashMap<>();
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------


        public void close() {
            if (!_databases.isEmpty()) {
                _databases.forEach((key, db) -> {
                    try {
                        // should cloese database?
                    } catch (Throwable ignored) {
                        //ignored
                    }
                });
                _databases.clear();
            }
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private IDatabase database() {
            final String db_name = DBController.DBNameProgram(_tool_db.name());
            if (!_databases.containsKey(db_name)) {
                _databases.put(db_name, DBController.instance().db(db_name));
            }
            return _databases.get(db_name);
        }

        private CollectionWrapper collection(final String name) {
            final IDatabase db = this.database();
            if (null != db) {
                return new CollectionWrapper(db.collection(name, Map.class));
            }
            return null;
        }

    }


    public static class CollectionWrapper {

        private final IDatabaseCollection<Map> _collection;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        private CollectionWrapper(final IDatabaseCollection<Map> collection) {
            _collection = collection;
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public boolean connected() {
            return null != _collection;
        }

        public Map upsert(final Object item) {
            if (null != _collection) {
                if (null != item) {
                    final Map<String, Object> entity = Converter.toJsonItem(item).map();
                    return _collection.upsert(entity);
                }
            }
            return null;
        }

        public Map get(final String key) {
            if (null != _collection) {
                return _collection.get(key);
            }
            return null;
        }

        public Collection<Map> find(final String query,
                                    final Object bindArgs) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(bindArgs).map();
                return _collection.find(query, args);
            }
            return null;
        }


    }

}
