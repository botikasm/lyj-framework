package org.ly.ose.server.application.controllers.fs.upload;

import org.ly.ose.server.application.controllers.fs.temp.FSTemp;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.RandomUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FSUpload {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private FSUpload() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * @param file_info JSON object containing file info
     */
    public String onFileUpload(final org.lyj.ext.netty.server.web.handlers.impl.UploadHandler.FileInfo file_info) {
        try {
            if (file_info.contentLength() > 0) {
                final String temp_file = file_info.localeAbsolute();
                if (FileUtils.exists(temp_file)) {
                    // move file inside temporary storage
                    final String cache_key = this.moveToCache(temp_file);

                    // now updates file_info with new storage path (cache ID) to use from client
                    file_info.put("cache_key", cache_key);
                    file_info.localeAbsolute(cache_key);
                    file_info.localeRoot("");
                    file_info.localeRelative("");


                } else {
                    // something wrong in temp folder
                }
            } else {
                // zero content length
            }

            return "";   // no errors
        } catch (Throwable t) {
            return t.getMessage();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String moveToCache(final String temp_file) throws IOException {
        try {
            final File file = new File(temp_file);
            if (file.exists()) {
                final String key = RandomUtils.randomUUID();
                FSTemp.instance().put(key, file);
                return key;
            } else {
                throw new FileNotFoundException(temp_file);
            }
        } finally {
            FileUtils.delete(temp_file);
        }
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static FSUpload __instance;

    public static synchronized FSUpload instance() {
        if (null == __instance) {
            __instance = new FSUpload();
        }
        return __instance;
    }


}
