package org.ly.ose.server.application.programming.tools.persistence;

import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.persistence.DBController;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.OSEProgramInfo;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.StringUtils;

/**
 * Database utility class
 * Usage:
 * $db.collection('test').find().....
 */
public class Tool_db
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "db"; // used as $db.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _db_name; // usually equals program name with a custom prefix. i.e. "ose_program_system_utils"

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_db(final OSEProgram program) {
        super(NAME, program);

        this.setName(super.info().namespace() + "_" + super.info().name()); // program name
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void setName(final String name) {
        _db_name = DBController.DBNameProgram(name);
    }

    public String getName() {
        return StringUtils.replace(_db_name, IConstants.DB_PROGRAM_PREFIX, "");
    }

}
