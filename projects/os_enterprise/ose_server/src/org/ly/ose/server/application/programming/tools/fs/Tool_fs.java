package org.ly.ose.server.application.programming.tools.fs;

import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.controllers.fs.cloud.FSCloud;
import org.ly.ose.server.application.controllers.fs.temp.FSTemp;
import org.ly.ose.server.application.endpoints.api.routing.RouterSys;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.RandomUtils;

import java.io.File;
import java.io.IOException;

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
        final File file = this.lookupFile(file_id);
        if (null != file && file.exists()) {
            return file.length();
        }
        return 0;
    }

    public long sizeOfMb(final String file_id) throws Exception {
        final long size = this.sizeOf(file_id);
        return size > 0 ? size / (1024L * 1024L) : 0;
    }

    public boolean remove(final String file_id) throws Exception {
        return this.removeFile(file_id);
    }

    public File get(final String file_id) throws Exception {
        return this.lookupFile(file_id);
    }

    /**
     * Prepare a file for download putting it into temp cache
     *
     * @param file_id FS id
     */
    public String downloadLink(final String file_id) throws Exception {
        final File file = this.get(file_id);
        if (null != file) {
            // put to cache
            final String key = RandomUtils.randomUUID();
            FSTemp.instance().put(key, file);

            return RouterSys.urlApi("/util/download/" + IConstants.APP_TOKEN + "/" + key);
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private File lookupFile(final String cache_or_file_id) throws IOException {
        if (FSCloud.instance().has(cache_or_file_id)) {
            return FSCloud.instance().get(cache_or_file_id);
        }
        if (FSTemp.instance().has(cache_or_file_id)) {
            final File file = FSTemp.instance().getFile(cache_or_file_id);
            if (null != file) {
                return file;
            }
        }
        return null;
    }

    private boolean removeFile(final String cache_or_file_id) throws Exception {
        if (FSCloud.instance().has(cache_or_file_id)) {
            return FSCloud.instance().remove(cache_or_file_id);
        }
        if (FSTemp.instance().has(cache_or_file_id)) {
            return FSTemp.instance().remove(cache_or_file_id);
        }
        return false;
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


}
