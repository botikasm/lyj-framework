package org.lyj.ext.db.arango.serialization;

import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.model.MapDocument;

/**
 *
 */
public class ArangoMapDocument
        extends MapDocument {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_KEY = "_key";
    private static final String FLD_REV = "_rev";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ArangoMapDocument() {
        super();
        this.init();
    }

    public ArangoMapDocument(final Object value) {
        super(value);
        this.init();
    }

    public ArangoMapDocument(final String value) {
        super(value);
        this.init();
    }
    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String key() {
        return this.getString(FLD_KEY);
    }

    public ArangoMapDocument key(final String value) {
        this.put(FLD_KEY, value);
        return this;
    }

    public String revision() {
        return this.getString(FLD_REV);
    }

    public ArangoMapDocument revision(final String value) {
        this.put(FLD_REV, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!StringUtils.hasText(this.key())) {
            this.key(uuid());
        }
    }


}
