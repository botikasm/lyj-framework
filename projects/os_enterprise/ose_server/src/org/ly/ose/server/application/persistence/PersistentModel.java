package org.ly.ose.server.application.persistence;

import org.lyj.ext.db.arango.serialization.ArangoMapDocument;

import java.util.Collection;
import java.util.Set;

/**
 *
 */
public class PersistentModel
        extends ArangoMapDocument {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String FLD_COLLECTION = "collection";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public PersistentModel() {
        super();
    }

    public PersistentModel(final Object item) {
        super(item);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


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

    private void init(){


    }

}
