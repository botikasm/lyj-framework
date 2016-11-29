package org.lyj.ext.db.exceptions;

import org.lyj.commons.util.FormatUtils;

/**
 *
 */
public class DatabaseDoesNotExists extends Exception {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DatabaseDoesNotExists(final String name) {
        super(FormatUtils.format("Database '%s' does not exists!", name));
    }

}
