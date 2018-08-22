package org.ly.ose.server.application.controllers.fs.temp;

import org.lyj.commons.io.cache.filecache.CacheFiles;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;

public class FSTemp
        extends CacheFiles {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static int DURATION_MS = 2 * 60* 1000; // 2 minutes cache

    private static String ROOT = PathUtils.getAbsolutePath("./files/cache");

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private FSTemp() {
        super(ROOT);
        super.duration(DURATION_MS);

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

    @Override
    public boolean has(final String key) {
        return super.has(key);
    }

    @Override
    public void put(final String key,
                    final File file) {
        super.put(key, file);
    }

    @Override
    public File getFile(final String key) {
        return super.getFile(key);
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
