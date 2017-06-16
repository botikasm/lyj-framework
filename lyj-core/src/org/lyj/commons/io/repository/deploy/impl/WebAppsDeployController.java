package org.lyj.commons.io.repository.deploy.impl;

import org.lyj.Lyj;
import org.lyj.commons.io.jsonrepository.JsonRepository;
import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

/**
 * {
 * "app": {
 * "deploy": {
 * "path": "./htdocs"
 * }
 * },
 * "landing_pages": {
 * "deploy": {
 * "path": "./htdocs/landing_pages"
 * }
 * }
 * }
 */
public class WebAppsDeployController {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PARAM_NAME = "webapp"; // configuration name


    private static final String FLD_DEPLOY_PATH = "deploy.path";
    private static final String FLD_DEPLOY_EXCLUDE = "deploy.exclude";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Collection<FileDeployer> _deployers;

    private boolean _initialized;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private WebAppsDeployController() {
        _deployers = new LinkedList<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void deploy() throws Exception {
        // avoid running multiple times
        if (this.init()) {
            for (final FileDeployer deployer : _deployers) {
                deployer.deploy(deployer.getTargetFolder(), true);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean init() throws Exception {
        if (!_initialized) {
            final JsonRepository config = Lyj.getConfiguration();
            if (null != config) {
                _initialized = true;

                final JsonItem param = new JsonItem(config.getJSONObject(PARAM_NAME));
                final Set<String> keys = param.keys();
                for (final String key : keys) {
                    final JsonItem item = new JsonItem(param.getJSONObject(key));
                    final String path = item.getString(FLD_DEPLOY_PATH);
                    final String[] exclude = JsonWrapper.toArrayOfString(item.getJSONArray(FLD_DEPLOY_EXCLUDE));
                    if (StringUtils.hasText(path)) {
                        _deployers.add(new WebAppsDeployer(key, path, exclude));
                    }
                }

                // exit
                return true;
            } else {
                // configuration is not ready.
                throw new Exception("Configuration is not Ready!");
            }
        }
        return false;
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static WebAppsDeployController __instance;

    public static WebAppsDeployController instance() {
        if (null == __instance) {
            __instance = new WebAppsDeployController();
        }
        return __instance;
    }


}
