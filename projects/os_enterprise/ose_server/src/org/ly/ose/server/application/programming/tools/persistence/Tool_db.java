package org.ly.ose.server.application.programming.tools.persistence;

import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.persistence.DBController;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;
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

        CollectionWrapper(final IDatabaseCollection<Map> collection) {
            _collection = collection;
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public boolean connected() {
            return null != _collection;
        }

        //--  i n d e x e s  --//

        public void addIndex(final Object raw_fields, final Object raw_is_unique) {
            if (null != _collection) {
                final String[] fields = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_fields));
                final boolean is_unique = ConversionUtils.toBoolean(raw_is_unique);

                _collection.schema().addIndex(fields, is_unique);
            }
        }

        public void addGeoIndex(final Object raw_fields, final Object raw_geoJson) {
            if (null != _collection) {
                final String[] fields = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_fields));
                final boolean geoJson = ConversionUtils.toBoolean(raw_geoJson);

                _collection.schema().addGeoIndex(fields, geoJson);
            }
        }

        public void addGeoIndex(final Object raw_fields) {
            if (null != _collection) {
                final String[] fields = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_fields));

                _collection.schema().addGeoIndex(fields);
            }
        }

        public void removeIndex(final Object raw_fields) {
            if (null != _collection) {
                final String[] fields = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_fields));
                _collection.schema().removeIndex(fields);
            }
        }

        //--  u p s e r t  --//

        public Map upsert(final Object item) {
            if (null != _collection) {
                if (null != item) {
                    final Map<String, Object> entity = Converter.toJsonItem(item).map();
                    return _collection.upsert(entity);
                }
            }
            return null;
        }

        //--  r e m o v e  --//

        public boolean remove(final String key) {
            if (null != _collection) {
                return _collection.remove(key);
            }
            return false;
        }

        public Collection<Map> remove(final String query,
                                      final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.remove(query, args);
            }
            return null;
        }

        public Collection<Map> removeEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.removeEqual(args);
            }
            return null;
        }

        public Map removeOne(final String query,
                             final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.removeOne(query, args);
            }
            return null;
        }

        public Map removeOneEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.removeOneEqual(args);
            }
            return null;
        }

        //--  g e t  --//

        public Map get(final String key) {
            if (null != _collection) {
                return _collection.get(key);
            }
            return null;
        }

        public Collection<Map> find(final String query,
                                    final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.find(query, args);
            }
            return null;
        }

        public Map findOne(final String query,
                           final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.findOne(query, args);
            }
            return null;
        }

        public Collection<Map> findEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.findEqual(args);
            }
            return null;
        }

        public Map findOneEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.findOneEqual(args);
            }
            return null;
        }

        public Collection<Map> findEqualAsc(final Object raw_args,
                                            final Object raw_sort) {
            return this.findEqualAsc(raw_args, raw_sort, 0, 0);
        }

        public Collection<Map> findEqualAsc(final Object raw_args,
                                            final Object raw_sort,
                                            final Object raw_skip,
                                            final Object raw_limit) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                final int skip = ConversionUtils.toInteger(raw_skip);
                final int limit = ConversionUtils.toInteger(raw_limit);

                return _collection.findEqualAsc(args, sort, skip, limit);
            }
            return null;
        }

        public Collection<Map> findEqualDesc(final Object raw_args,
                                             final Object raw_sort) {
            return this.findEqualDesc(raw_args, raw_sort, 0, 0);
        }

        public Collection<Map> findEqualDesc(final Object raw_args,
                                             final Object raw_sort,
                                             final Object raw_skip,
                                             final Object raw_limit) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                final int skip = ConversionUtils.toInteger(raw_skip);
                final int limit = ConversionUtils.toInteger(raw_limit);

                return _collection.findEqualDesc(args, sort, skip, limit);
            }
            return null;
        }

        public Collection<Map> findLikeOrAsc(final Object raw_args,
                                             final Object raw_sort) {
            return this.findLikeOrAsc(raw_args, raw_sort, 0, 0);
        }

        public Collection<Map> findLikeOrAsc(final Object raw_args,
                                             final Object raw_sort,
                                             final Object raw_skip,
                                             final Object raw_limit) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                final int skip = ConversionUtils.toInteger(raw_skip);
                final int limit = ConversionUtils.toInteger(raw_limit);

                return _collection.findLikeOrAsc(args, sort, skip, limit);
            }
            return null;
        }

        public Collection<Map> findLikeOrDesc(final Object raw_args,
                                              final Object raw_sort) {
            return this.findLikeOrDesc(raw_args, raw_sort, 0, 0);
        }

        public Collection<Map> findLikeOrDesc(final Object raw_args,
                                              final Object raw_sort,
                                              final Object raw_skip,
                                              final Object raw_limit) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                final int skip = ConversionUtils.toInteger(raw_skip);
                final int limit = ConversionUtils.toInteger(raw_limit);

                return _collection.findLikeOrDesc(args, sort, skip, limit);
            }
            return null;
        }

    }

}
