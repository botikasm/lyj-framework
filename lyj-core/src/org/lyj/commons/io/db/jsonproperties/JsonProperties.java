package org.lyj.commons.io.db.jsonproperties;

import org.json.JSONObject;
import org.lyj.commons.io.db.jsondb.JsonDB;
import org.lyj.commons.io.db.jsondb.JsonDBCollection;
import org.lyj.commons.util.json.JsonList;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.commons.util.StringUtils;


/**
 * Implementation of JsonDB for a read/write
 * set of properties in json format supporting treemap structure.
 *
 * Use this class to create JSON like property files.
 *
 */
public class JsonProperties {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------
    private static final String DB_NAME = "settings";
    private static final String COLL_SYS = "values";

    private static final String ID = "_id";
    private static final String VALUE = "value";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonDB _db;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JsonProperties(final String root) {
        _db = JsonDB.create(root).open(DB_NAME);
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject toJson() {
        final JsonWrapper response = new JsonWrapper(new JSONObject());
        final JsonList list = _db.collection(COLL_SYS).find();
        for (final JSONObject item : list) {
            if (null != item) {
                final String key = item.optString(ID);
                final Object val = item.opt(VALUE);
                if (StringUtils.hasText(key)) {
                    response.putDeep(key, val);
                }
            }
        }
        return response.getJSONObject();
    }

    public void set(final String propName, final Object value) {
        final JSONObject item = new JSONObject();
        item.put(ID, propName);
        item.put(VALUE, value);
        _db.collection(COLL_SYS).upsert(item);
    }

    public String getString(final String propName) {
        return this.getString(propName, "");
    }

    public String getString(final String propName, final String defVal) {
        final JsonDBCollection coll = _db.collection(COLL_SYS);
        final JSONObject item = coll.findOne(ID, propName);
        if (null != item) {
            return item.optString(VALUE, defVal);
        }
        return defVal;
    }

    public int getInteger(final String propName) {
        return this.getInteger(propName, 0);
    }

    public int getInteger(final String propName, final int defVal) {
        final JsonDBCollection coll = _db.collection(COLL_SYS);
        final JSONObject item = coll.findOne(ID, propName);
        if (null != item) {
            return JsonWrapper.getInt(item, VALUE, defVal);
        }
        return defVal;
    }

    public double getDouble(final String propName) {
        return this.getDouble(propName, 0);
    }

    public double getDouble(final String propName, final double defVal) {
        final JsonDBCollection coll = _db.collection(COLL_SYS);
        final JSONObject item = coll.findOne(ID, propName);
        if (null != item) {
            return JsonWrapper.getDouble(item, VALUE, defVal);
        }
        return defVal;
    }


    public boolean getBoolean(final String propName) {
        return this.getBoolean(propName, false);
    }

    public boolean getBoolean(final String propName, final boolean defVal) {
        final JsonDBCollection coll = _db.collection(COLL_SYS);
        final JSONObject item = coll.findOne(ID, propName);
        if (null != item) {
            return JsonWrapper.getBoolean(item, VALUE, defVal);
        }
        return defVal;
    }

}
