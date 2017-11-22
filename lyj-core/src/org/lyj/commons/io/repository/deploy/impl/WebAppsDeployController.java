package org.lyj.commons.io.repository.deploy.impl;

import org.lyj.Lyj;
import org.lyj.commons.io.jsonrepository.JsonRepository;
import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

/**
 * Extends this class to create a parametrized deployer.
 * Subclass must be located in root path of resources to deploy.
 * Deploy parameters are defined in a json configuration file.
 * <p>
 * i.e.:
 * <p>
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
public abstract class WebAppsDeployController {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String CONFIG_NAME = "webdeploy"; // configuration name


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

    public WebAppsDeployController() {
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
            final JsonItem config = this.initConfiguration();
            if (null != config && !config.isEmpty()) {
                _initialized = true;

                final Set<String> keys = config.keys();
                for (final String key : keys) {
                    final JsonItem item = new JsonItem(config.getJSONObject(key));
                    final String path = item.getString(FLD_DEPLOY_PATH);
                    final String[] exclude = JsonWrapper.toArrayOfString(item.getJSONArray(FLD_DEPLOY_EXCLUDE));
                    if (StringUtils.hasText(path)) {
                        _deployers.add(new WebAppsDeployer(this.getClass(), key, path, exclude));
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

    private JsonItem initConfiguration() {
        final JsonRepository config = Lyj.getConfiguration();
        if (null != config) {
            // lookup configuration into filesystem (customized configuration that override internal one)
            JsonItem param = new JsonItem(config.getJSONObject(CONFIG_NAME));
            if (param.isEmpty()) {
                // lookup configuration into internal resources
                final String json_config = ClassLoaderUtils.getResourceAsString(null,
                        this.getClass(), CONFIG_NAME.concat(".json"));
                if (StringUtils.isJSONObject(json_config)) {
                    param = new JsonItem(json_config);
                }
            }
            return param;
        }
        return null;
    }


}
