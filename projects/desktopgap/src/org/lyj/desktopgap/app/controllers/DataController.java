package org.lyj.desktopgap.app.controllers;

import org.json.JSONObject;
import org.lyj.Lyj;
import org.lyj.commons.io.jsondb.JsonDB;
import org.lyj.commons.io.jsondb.JsonDBCollection;
import org.lyj.commons.io.jsondb.exceptions.JsonDBInvalidItemException;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.desktopgap.app.IConstants;

/**
 * Manage persistence for internal database (JsonDb)
 */
public class DataController extends AbstractLogEmitter implements IConstants {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final String DEFAULT_NAME = "_default"; // default database
    private final String ID = "_id";
    private final String FLD_VALUE = "value";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DataController() {
        JsonDB.create(this.root()).open(DEFAULT_NAME).close(); // create, but don't alloc memory (late initialized)
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject put(final String collectionName, final String key, final Object value) {
        try {
            final JsonDBCollection collection = this.collection(collectionName);
            final JSONObject item = new JSONObject();
            item.put(ID, key);
            item.put(FLD_VALUE, value);
            return collection.upsert(item);
        } catch (JsonDBInvalidItemException t) {
            return null;
        }
    }

    public Object get(final String collectionName, final String key) {
        final JsonDBCollection collection = this.collection(collectionName);
        final JSONObject item = collection.findOne(ID, key);
        if (null != item) {
            return item.opt(FLD_VALUE);
        }
        return null;
    }

    public void setConnected(final boolean value) {
        this.put(COLL_CONNECTION, COLL_CONNECTION_CONNECTED, value);
    }

    public boolean getConnected(){
        return ConversionUtils.toBoolean(this.get(COLL_CONNECTION, COLL_CONNECTION_CONNECTED));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String root() {
        return Lyj.getAbsolutePath("data/db");
    }

    private JsonDB database() {
        return this.database(DEFAULT_NAME);
    }

    private JsonDB database(final String name) {
        return new JsonDB(this.root()).open(name);
    }

    private JsonDBCollection collection(final String name) {
        return this.collection(DEFAULT_NAME, name);
    }

    private JsonDBCollection collection(final String db, final String name) {
        return this.database(db).collection(name);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static DataController __instance;

    public static DataController instance() {
        if (null == __instance) {
            __instance = new DataController();
        }
        return __instance;
    }

}
