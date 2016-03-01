/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lyj;

import org.json.JSONObject;
import org.lyj.commons.io.jsonrepository.JsonRepository;
import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.config.Deployer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Lyj {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String LYJ = "lyj";
    private static final String LYJ_APP_ID = LYJ.concat(".app_id");
    private static final String LYJ_DEBUG = LYJ.concat(".debug");
    private static final String LYJ_SILENT = LYJ.concat(".silent");
    private static final String LYJ_LANG = LYJ.concat(".lang");

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Lyj() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Set with reflection from AbstractLauncher
     * BeanUtils.setValueIfAny(_runnerInstance, "mappedArgs", _argsMap);
     *
     * @param mappedArgs
     */
    public void setMappedArgs(final Map<String, Object> mappedArgs) {
        _launcherArgs = mappedArgs;
    }

    public void setRemainArgs(final String[] remainingArgs) {
        _launcherRemainingArgs = remainingArgs;
    }

    public void run() throws Exception {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() throws Exception {

        // register main configuration deployer that add remainig files
        //FileDeployer.register(new Deployer(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES), isSilent()));

        // packages are loaded
        // now run remaining deployers if any
        FileDeployer.deployAll();

        // set configuration last time
        Lyj._configuration = new JsonRepository(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES));
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static String __home;
    private static ClassLoader __classLoader;
    private static String[] __langCodes;
    private static JsonWrapper __langMap;
    private static LyjLogger __logger;
    private static JsonRepository _configuration;
    private static Map<String, Object> _launcherArgs;
    private static String[] _launcherRemainingArgs;

    public static LyjLogger getLogger() {
        if (null == __logger) {
            __logger = new LyjLogger(isSilent());
        }
        return __logger;
    }

    public static LyjLogger getLogger(final Object sender) {
        return new LyjLogger(sender, isSilent());
    }

    public static String getHome() {
        if (null == __home) {
            __home = System.getProperty(IConstants.SYSPROP_HOME);
            if (!StringUtils.hasText(__home)) {
                __home = ".";
            }
        }
        return __home;
    }

    public static ClassLoader getClassLoader() {
        if (null == __classLoader) {
            __classLoader = Thread.currentThread().getContextClassLoader();
        }
        return __classLoader;
    }

    public static Map<String, Object> getLauncherArgs() {
        return _launcherArgs;
    }

    public static String[] getLauncherRemainArgs() {
        return _launcherRemainingArgs;
    }

    public static boolean isTestUnitMode() {
        return getLauncherArgs().containsKey("t") && (Boolean) getLauncherArgs().get("t");
    }

    public static boolean isAdminMode() {
        return getLauncherArgs().containsKey("a") && (Boolean) getLauncherArgs().get("a");
    }

    public static String getAppId() {
        return getConfiguration().getString(LYJ_APP_ID);
    }

    public static boolean isDebugMode() {
        return getConfiguration().getBoolean(LYJ_DEBUG);
    }

    public static boolean isSilent() {
        try {
            return getConfiguration(true).getBoolean(LYJ_SILENT);
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static String getLang() {
        return getConfiguration().getString(LYJ_LANG);
    }

    public static String getCharset() {
        return CharEncoding.getDefault();
    }

    public static JsonRepository getConfiguration() {
        return _configuration;
    }

    public static JsonRepository getConfiguration(final boolean live) throws Exception {
        return live ? new JsonRepository(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES)) : _configuration;
    }


    public static String getConfigurationPath() {
        return PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES);
    }

    public static String getAbsolutePath(final String relativePath) {
        return PathUtils.getAbsolutePath(relativePath);
    }

    public static String getLangCode() {
        return "en";
    }

    public static String[] getLanguages() {
        if (null == __langCodes) {
            initLanguages();
        }
        return __langCodes;
    }

    public static JsonWrapper getLanguagesHelper() {
        if (null == __langMap) {
            initLanguages();
        }
        return __langMap;
    }

    public static void registerDeployer(final FileDeployer deployer) {
        FileDeployer.register(deployer);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static void initLanguages() {
        __langMap = new JsonWrapper(new JSONObject());
        final Set<String> langs = new HashSet<String>();
        final List<JSONObject> configLanguages = _configuration.getList("languages");
        for (final JSONObject lang : configLanguages) {
            final String code = JsonWrapper.getString(lang, "code");
            final String label = JsonWrapper.getString(lang, "label");
            langs.add(code);
            __langMap.putOpt(code, label);
        }
        __langCodes = langs.toArray(new String[langs.size()]);
    }

}
