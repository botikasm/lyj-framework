package org.ly.ose.server.application.controllers.fs.temp;

import org.lyj.commons.io.cache.filecache.CacheFiles;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

public class FSTemp
        extends CacheFiles {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static String ROOT = PathUtils.getAbsolutePath("./files/cache");

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private FSTemp() {
        super(ROOT);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void open() {
        super.open();
    }

    @Override
    public void close() {
        super.close();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
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
