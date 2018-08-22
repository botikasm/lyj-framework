package org.ly.ose.server.application.programming.tools.fs;

import org.ly.ose.server.application.controllers.fs.cloud.FSCloud;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;

import java.io.File;

/**
 * File System
 */
public class Tool_fs
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "fs";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_fs(final OSEProgram program) {
        super(NAME, program);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    /**
     * Move a cached file (identified by cache_id) into managed file system.
     *
     * @param cache_id ID of temp cache manager.
     * @return File ID (cloud_id). Retun empty string if file is no more in cache
     * @throws Exception Problem moving file
     */
    public String storeFromCache(final String cache_id) throws Exception {
        return FSCloud.instance().moveFromCache(cache_id);
    }

    public long sizeOf(final String file_id) throws Exception {
        final File file = FSCloud.instance().get(file_id);
        if (file.exists()) {
            return file.length();
        }
        return 0;
    }

    public long sizeOfMb(final String file_id) throws Exception {
        return this.sizeOf(file_id) / (1024L * 1024L);
    }

    public boolean remove(final String file_id) throws Exception {
        return FSCloud.instance().remove(file_id);
    }

    public File get(final String file_id) throws Exception {
        return FSCloud.instance().get(file_id);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


}
