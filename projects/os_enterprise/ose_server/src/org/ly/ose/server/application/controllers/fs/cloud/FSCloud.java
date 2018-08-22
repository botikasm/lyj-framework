package org.ly.ose.server.application.controllers.fs.cloud;

import org.ly.ose.server.application.controllers.fs.temp.FSTemp;
import org.ly.ose.server.deploy.config.ConfigHelper;
import org.lyj.commons.io.cloudfs.CloudFS;
import org.lyj.commons.io.cloudfs.configuration.CloudFSConfig;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class FSCloud {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final CloudFS _cloud;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private FSCloud() {
        _cloud = new CloudFS(new CloudFSConfig(ConfigHelper.instance().vfsCloudFS()));
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Lookup a file in cache and move into cloud FS
     *
     * @param key Cache key
     * @return Full cloud path
     */
    public String moveFromCache(final String key) throws Exception {
        if (StringUtils.hasText(key)) {
            if (FSTemp.instance().has(key)) {
                final File file = FSTemp.instance().getFile(key);
                if (_cloud.canStore(file)) {
                    final String cloud_id = _cloud.copy(file);

                    // remove from cache
                    FSTemp.instance().remove(key);

                    return cloud_id;
                } else {
                    throw new Exception("SEVERE: Cloud storage cannot host more files. Space is exhausted.");
                }
            } else {
                throw new Exception("Cache not found or expired for key: " + key);
            }
        } else {
            throw new Exception("'key' is required to get a file from cache!");
        }
    }

    public boolean remove(final String cloud_id) throws IOException {
        return _cloud.remove(cloud_id);
    }

    public File get(final String cloud_id) throws IOException {
        return _cloud.get(cloud_id);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static FSCloud __instance;

    public static synchronized FSCloud instance() {
        if (null == __instance) {
            __instance = new FSCloud();
        }
        return __instance;
    }

}
