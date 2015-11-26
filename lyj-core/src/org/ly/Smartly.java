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

package org.ly;

import org.json.JSONObject;
import org.ly.commons.io.jsonrepository.JsonRepository;
import org.ly.commons.io.repository.deploy.FileDeployer;
import org.ly.commons.lang.CharEncoding;
import org.ly.commons.util.JsonWrapper;
import org.ly.commons.util.PathUtils;
import org.ly.commons.util.StringUtils;
import org.ly.config.Deployer;
import org.ly.launcher.SmartlyPackageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Smartly {

    private static final String SMARTLY = "smartly";
    private static final String SMARTLY_APP_ID = SMARTLY.concat(".app_id");
    private static final String SMARTLY_DEBUG = SMARTLY.concat(".debug");
    private static final String SMARTLY_SILENT = SMARTLY.concat(".silent");
    private static final String SMARTLY_LANG = SMARTLY.concat(".lang");

    private SmartlyPackageLoader _packageLoader;

    public Smartly() {

    }

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

    public void run(final SmartlyPackageLoader packageLoader) throws Exception {
        this.init(packageLoader);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final SmartlyPackageLoader packageLoader) throws Exception {

        // Run Smartly configuration deployer
        (new Deployer(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES), isSilent())).deploy();

        //-- init package loader --//
        _packageLoader = packageLoader;

        // run all packages and flush deployers queue.
        // this allow packages to access deployed files
        _packageLoader.load();

        // packages are loaded
        // now run remaining deployers if any
        FileDeployer.deployAll();

        // set configuration last time
        Smartly._configuration = new JsonRepository(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES));

        // set loaded package names
        Smartly._packages = _packageLoader.getPackageNames();

        // notify all packages Smartly is ready.
        _packageLoader.ready();

        //-- add shutdown hook --//
        Runtime.getRuntime().addShutdownHook(new SmartlyShutdownHook(_packageLoader));
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static String __home;
    private static ClassLoader __classLoader;
    private static String[] __langCodes;
    private static JsonWrapper __langMap;
    private static SmartlyLogger __logger;
    private static JsonRepository _configuration;
    private static Set<String> _packages; // loaded packages
    private static Map<String, Object> _launcherArgs;
    private static String[] _launcherRemainingArgs;

    public static SmartlyLogger getLogger() {
        if (null == __logger) {
            __logger = new SmartlyLogger(isSilent());
        }
        return __logger;
    }

    public static SmartlyLogger getLogger(final Object sender) {
        return new SmartlyLogger(sender, isSilent());
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

    public static boolean hasPackage(final String name) {
        return null != _packages ? _packages.contains(name) : false;
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
        return getConfiguration().getString(SMARTLY_APP_ID);
    }

    public static boolean isDebugMode() {
        return getConfiguration().getBoolean(SMARTLY_DEBUG);
    }

    public static boolean isSilent() {
        try {
            return getConfiguration(true).getBoolean(SMARTLY_SILENT);
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static String getLang() {
        return getConfiguration().getString(SMARTLY_LANG);
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
        return SmartlyPathManager.getConfigurationPath();
    }

    public static String getConfigurationPath(final Class clazz) {
        return SmartlyPathManager.getConfigurationPath(clazz);
    }

    public static String getAbsolutePath(final String relativePath) {
        return SmartlyPathManager.getAbsolutePath(relativePath);
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

    public static void register(final FileDeployer deployer) {
        FileDeployer.register(deployer);
    }


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
