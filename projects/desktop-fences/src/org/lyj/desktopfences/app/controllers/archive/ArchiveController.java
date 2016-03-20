package org.lyj.desktopfences.app.controllers.archive;

import org.lyj.commons.io.jsondb.JsonDB;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopfences.app.IConstants;

/**
 *
 */
public class ArchiveController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ARCHIVE_PATH = IConstants.ARCHIVE_PATH;
    private static final String ARCHIVE_FILES = PathUtils.combine(ARCHIVE_PATH, "files");
    private static final String ARCHIVE_DATA = PathUtils.combine(ARCHIVE_PATH, "data");

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonDB _db;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ArchiveController() {
        _db = new JsonDB(ARCHIVE_DATA).open("db");
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void put(final ArchiveFile file,
                    final boolean move){

    }

    // ------------------------------------------------------------------------
    //                      i n i t
    // ------------------------------------------------------------------------

    private void init() {

    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ArchiveController __instance;

    public static ArchiveController instance() {
        if (null == __instance) {
            __instance = new ArchiveController();
        }
        return __instance;
    }

}
