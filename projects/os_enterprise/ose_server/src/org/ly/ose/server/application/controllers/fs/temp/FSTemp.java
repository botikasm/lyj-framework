package org.ly.ose.server.application.controllers.fs.temp;

import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

public class FSTemp {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static String ROOT = PathUtils.getAbsolutePath("./fs_cache");

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private FSTemp() {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(){
        FileUtils.tryMkdirs(ROOT);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static FSTemp __instance;

    public static synchronized FSTemp instance() {
        if (null == __instance) {
            __instance = new FSTemp();
        }
        return __instance;
    }
}
