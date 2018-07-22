package org.ly.ose.commons.model;

import org.json.JSONObject;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.model.MapDocument;

/**
 *
 */
public class BaseModel
        extends MapDocument {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    protected static final String FLD_KEY = "_key";
    protected static final String FLD_REV = "_rev";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    public static final String FLD_COLLECTION = "collection";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public BaseModel() {
        super();
        this.init();
    }

    public BaseModel(final Object item) {
        super(item);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String key() {
        return this.getString(FLD_KEY);
    }

    public BaseModel key(final String value) {
        this.put(FLD_KEY, value);
        return this;
    }

    public String revision() {
        return this.getString(FLD_REV);
    }

    public BaseModel revision(final String value) {
        this.put(FLD_REV, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public BaseModel putAllNotEmpty(final JSONObject values,
                                    final boolean only_existing_fields) {
        super.putAllNotEmpty(values, only_existing_fields, new String[]{"_*"});
        return this;
    }

    public String collection() {
        return super.getString(FLD_COLLECTION);
    }

    public BaseModel collection(final String value) {
        super.put(FLD_COLLECTION, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!StringUtils.hasText(this.key())) {
            this.key(uuid());
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static BaseModel clone(final Object item) {
        if (null != item) {
            final BaseModel new_item = new BaseModel(item);
            // reset some fields
            new_item.put(FLD_KEY, uuid());

            return new_item;
        }
        return null;
    }
}
