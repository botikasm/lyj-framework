package org.ly.ose.server.application.persistence;

import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONArray;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringEscapeUtils;
import org.lyj.commons.util.converters.JsonConverter;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.IDatabaseCollection;
import org.lyj.ext.script.program.engines.javascript.EngineJavascript;
import org.lyj.ext.script.program.engines.javascript.utils.JavascriptConverter;
import org.lyj.ext.script.utils.Converter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DBHelper {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, IDatabase> _cache_databases;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DBHelper() {
        _cache_databases = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {
        if (!_cache_databases.isEmpty()) {
            _cache_databases.forEach((key, db) -> {
                try {
                    // should close database?
                } catch (Throwable ignored) {
                    //ignored
                }
            });
            _cache_databases.clear();
        }
    }

    public IDatabase database(final String db_name) {
        if (!_cache_databases.containsKey(db_name)) {
            _cache_databases.put(db_name, DBController.instance().db(db_name));
        }
        return _cache_databases.get(db_name);
    }

    public IDatabaseCollection<PersistentModel> collection(final String db_name,
                                                           final String coll_name) {
        final IDatabase db = this.database(db_name);
        if (null != db) {
            return db.collection(coll_name, PersistentModel.class);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static DBHelper __instance;

    public static synchronized DBHelper instance() {
        if (null == __instance) {
            __instance = new DBHelper();
        }
        return __instance;
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class CollectionWrapper {

        private final IDatabaseCollection<PersistentModel> _collection;
        private final boolean _auto_convert_to_jsobjects;
        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public CollectionWrapper(final IDatabaseCollection<PersistentModel> collection,
                                 final boolean autoconvert) {
            _collection = collection;
            _auto_convert_to_jsobjects = autoconvert;
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public boolean connected() {
            return null != _collection;
        }

        //--  i n d e x e s  --//

        public void addIndex(final Object raw_fields,
                             final Object raw_is_unique) {
            if (null != _collection) {
                final String[] fields = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_fields));
                final boolean is_unique = ConversionUtils.toBoolean(raw_is_unique);

                _collection.schema().addIndex(fields, is_unique);
            }
        }

        public void addGeoIndex(final Object raw_fields,
                                final Object raw_is_geoJson) {
            if (null != _collection) {
                final String[] fields = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_fields));
                final boolean geoJson = ConversionUtils.toBoolean(raw_is_geoJson);

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

        public Object upsert(final Object item) {
            if (null != _collection) {
                if (null != item) {
                    final PersistentModel entity = new PersistentModel(Converter.toJsonItem(item));
                    return convert(_collection.upsert(entity));
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

        public Object remove(final String query,
                                    final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return convert(_collection.remove(query, args));
            }
            return null;
        }

        public Object removeEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return convert(_collection.removeEqual(args));
            }
            return null;
        }

        public Object removeOne(final String query,
                                final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return convert(_collection.removeOne(query, args));
            }
            return null;
        }

        public Object removeOneEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return convert(_collection.removeOneEqual(args));
            }
            return null;
        }

        //--  g e t  --//

        public Object get(final String key) {
            if (null != _collection) {
                return convert(_collection.get(key));
            }
            return null;
        }

        public Object find(final String query,
                                  final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return convert(_collection.find(query, args));
            }
            return null;
        }

        public Object findOne(final String query,
                              final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return convert(_collection.findOne(query, args));
            }
            return null;
        }

        public Object findEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return convert(_collection.findEqual(args));
            }
            return null;
        }

        public Object findOneEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return convert(_collection.findOneEqual(args));
            }
            return null;
        }

        public Object findEqualAsc(final Object raw_args,
                                          final Object raw_sort) {
            return this.findEqualAsc(raw_args, raw_sort, 0, 0);
        }

        public Object findEqualAsc(final Object raw_args,
                                          final Object raw_sort,
                                          final Object raw_skip,
                                          final Object raw_limit) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                final int skip = ConversionUtils.toInteger(raw_skip);
                final int limit = ConversionUtils.toInteger(raw_limit);

                return convert(_collection.findEqualAsc(args, sort, skip, limit));
            }
            return null;
        }

        public Object findEqualDesc(final Object raw_args,
                                           final Object raw_sort) {
            return this.findEqualDesc(raw_args, raw_sort, 0, 0);
        }

        public Object findEqualDesc(final Object raw_args,
                                           final Object raw_sort,
                                           final Object raw_skip,
                                           final Object raw_limit) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                final int skip = ConversionUtils.toInteger(raw_skip);
                final int limit = ConversionUtils.toInteger(raw_limit);

                return convert(_collection.findEqualDesc(args, sort, skip, limit));
            }
            return null;
        }

        public Object findLikeOrAsc(final Object raw_args,
                                           final Object raw_sort) {
            return this.findLikeOrAsc(raw_args, raw_sort, 0, 0);
        }

        public Object findLikeOrAsc(final Object raw_args,
                                           final Object raw_sort,
                                           final Object raw_skip,
                                           final Object raw_limit) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                final int skip = ConversionUtils.toInteger(raw_skip);
                final int limit = ConversionUtils.toInteger(raw_limit);

                return convert(_collection.findLikeOrAsc(args, sort, skip, limit));
            }
            return null;
        }

        public Object findLikeOrDesc(final Object raw_args,
                                            final Object raw_sort) {
            return this.findLikeOrDesc(raw_args, raw_sort, 0, 0);
        }

        public Object findLikeOrDesc(final Object raw_args,
                                            final Object raw_sort,
                                            final Object raw_skip,
                                            final Object raw_limit) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                final int skip = ConversionUtils.toInteger(raw_skip);
                final int limit = ConversionUtils.toInteger(raw_limit);

                return convert(_collection.findLikeOrDesc(args, sort, skip, limit));
            }
            return null;
        }

        //--  c o u n t  --//

        public long count() {
            if (null != _collection) {
                return _collection.count();
            }
            return 0;
        }

        public long count(final String query,
                          final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.count(query, args);
            }
            return 0;
        }

        public long countEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.countEqual(args);
            }
            return 0;
        }

        public long countLikeOr(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.countLikeOr(args);
            }
            return 0;
        }

        public long countNotEqual(final Object raw_args) {
            if (null != _collection) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                return _collection.countNotEqual(args);
            }
            return 0;
        }

        //--  f o r   e a c h  --//

        public void forEach(final JSObject callback) {
            if (null != _collection && null != callback) {
                _collection.forEach((item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEach(final String query,
                            final Object raw_args,
                            final JSObject callback) {
            if (null != _collection && null != callback) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                _collection.forEach(query, args, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEachAsc(final Object raw_sort,
                               final JSObject callback) {
            if (null != _collection && null != callback) {
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                _collection.forEachAsc(sort, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEachDesc(final Object raw_sort,
                                final JSObject callback) {
            if (null != _collection && null != callback) {
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                _collection.forEachDesc(sort, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEachEqual(final Object raw_args,
                                 final JSObject callback) {
            if (null != _collection && null != callback) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                _collection.forEachEqual(args, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEachEqualAsc(final Object raw_args,
                                    final Object raw_sort,
                                    final JSObject callback) {
            if (null != _collection && null != callback) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                _collection.forEachEqualAsc(args, sort, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEachEqualDesc(final Object raw_args,
                                     final Object raw_sort,
                                     final JSObject callback) {
            if (null != _collection && null != callback) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                _collection.forEachEqualDesc(args, sort, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEachNotEqual(final Object raw_args,
                                    final JSObject callback) {
            if (null != _collection && null != callback) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                _collection.forEachNotEqual(args, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEachNotEqualAsc(final Object raw_args,
                                       final Object raw_sort,
                                       final JSObject callback) {
            if (null != _collection && null != callback) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                _collection.forEachNotEqualAsc(args, sort, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        public void forEachNotEqualDesc(final Object raw_args,
                                        final Object raw_sort,
                                        final JSObject callback) {
            if (null != _collection && null != callback) {
                final Map<String, Object> args = Converter.toJsonItem(raw_args).map();
                final String[] sort = JsonWrapper.toArrayOfString(Converter.toJsonArray(raw_sort));
                _collection.forEachNotEqualDesc(args, sort, (item) -> {
                    final Object callback_response = callback.call(null, convert(item));
                    return ConversionUtils.toBoolean(callback_response); // continue loop?
                });
            }
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        private Object convert(final Collection<PersistentModel> data) {
            try {
                if (_auto_convert_to_jsobjects) {
                    final JSONArray array = JsonConverter.toArray(data);
                    return JavascriptConverter.toScriptObject(array);
                }
            } catch (Throwable ignored) {
                // ignored
            }
            return data;
        }

        private Object convert(final PersistentModel data) {
            try {
                if (_auto_convert_to_jsobjects) {
                    return JavascriptConverter.toScriptObject(data);
                }
            } catch (Throwable ignored) {
                // ignored
            }
            return data;
        }

    }


}
