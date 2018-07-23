package org.ly.server.application.importer;

import org.lyj.commons.util.json.JsonItem;

/**
 * Configuration file for import package
 */
public class ImportConfig
        extends JsonItem {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String CONFIG_FILE_NAME = "package.json";

    private static final String FLD_TYPE = "type";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ImportConfig(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String type() {
        return super.getString(FLD_TYPE);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
