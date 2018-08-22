package org.ly.ose.server.application.controllers.upload;

import org.lyj.commons.util.FileUtils;

public class UploadController {


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * @param file_info JSON object containing file info
     */
    public String onFileUpload(final org.lyj.ext.netty.server.web.handlers.impl.UploadHandler.FileInfo file_info) {
        if (file_info.contentLength() > 0) {
            final String temp_file = file_info.localeAbsolute();
            if (FileUtils.exists(temp_file)) {
                // move file inside temporary storage

            } else {
                // something wrong in temp folder
            }
        } else {
            // zero content length
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------



    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static UploadController __instance;

    public static synchronized UploadController instance() {
        if (null == __instance) {
            __instance = new UploadController();
        }
        return __instance;
    }



}
