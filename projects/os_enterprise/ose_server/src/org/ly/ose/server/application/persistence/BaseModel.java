package org.ly.ose.server.application.persistence;

import org.json.JSONObject;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.arango.serialization.ArangoMapDocument;

/**
 *
 */
public class BaseModel
        extends ArangoMapDocument {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String FLD_COLLECTION = "collection";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public BaseModel() {
        super();
        this.checkId();
    }

    public BaseModel(final Object item) {
        super(item);
        this.checkId();
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

    private void checkId() {
        if (!StringUtils.hasText(this.key())) {
            this.key(uuid());
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static ArangoMapDocument clone(final Object item) {
        if (null != item) {
            final ArangoMapDocument new_item = new ArangoMapDocument(item);
            // reset some fields
            new_item.put(FLD_KEY, uuid());

            return new_item;
        }
        return null;
    }
}
