package org.ly.ose.commons.model;

import org.json.JSONObject;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.model.MapDocument;

/**
 *
 */
public class PersistentModel
        extends BaseModel {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    protected static final String FLD_REV = "_rev";


    public static final String FLD_COLLECTION = "collection";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public PersistentModel() {
        super();
    }

    public PersistentModel(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String revision() {
        return this.getString(FLD_REV);
    }

    public PersistentModel revision(final String value) {
        this.put(FLD_REV, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String collection() {
        return super.getString(FLD_COLLECTION);
    }

    public PersistentModel collection(final String value) {
        super.put(FLD_COLLECTION, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------



}
