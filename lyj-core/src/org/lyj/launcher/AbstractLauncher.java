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

package org.lyj.launcher;


import org.lyj.IConstants;
import org.lyj.Lyj;
import org.lyj.commons.Delegates;
import org.lyj.commons.cmdline.cmdparser.CmdLineParser;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.util.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractLauncher {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // launcher args variables.
    private static final String APPDIR = "APPDIR"; // user.dir
    private static final String USERHOME = "USERHOME"; // user.home

    private static final String DEFAULT_CLASSPATH = "lib/**";
    private static final String SYSPROP_CLASSPATH = "lyj.classpath";

    private static final String ENVPROP_HOME = "LYJ_HOME";

    private static final String CLASSPATH_SMARTLY = Lyj.class.getCanonicalName();
    private static final String LAUCHER_CLASS = "org/lyj/launcher/LyjLauncher.class";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String[] _args;
    private final Map<String, Object> _argsMap;
    private String[] _argsRemain;
    private Class _runnerClass;
    private Object _runnerInstance;
    private boolean _initialized;
    private Delegates.Handler _handler;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected AbstractLauncher(final String[] args) {
        _args = args;
        _argsMap = new HashMap<String, Object>();
        _initialized = false;
        try {
            final ClassLoader loader = this.init(args);

            _runnerClass = loader.loadClass(CLASSPATH_SMARTLY);
            _runnerInstance = _runnerClass.newInstance();
            // try inject args to runner
            BeanUtils.setValueIfAny(_runnerInstance, "mappedArgs", _argsMap);
            BeanUtils.setValueIfAny(_runnerInstance, "remainArgs", _argsRemain);
        } catch (Exception x) {
            System.err.println("Uncaught exception: ");
            x.printStackTrace();
            System.exit(2);
        }

        // SHUTDOWN HOOK
        // final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {

            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public AbstractLauncher handle(final Delegates.Handler callback){
        _handler = callback;
        return this;
    }

    public final void run() {
        if (!_initialized) {
            runInternal();
        }
        _initialized = true;
    }

    public final String[] getArgs() {
        return _args;
    }

    public final Map<String, Object> getArgsMap() {
        return _argsMap;
    }

    public final Object getArg(final String name){
        return _argsMap.get(name);
    }

    public final Object getArgString(final String name){
        return ConversionUtils.toString(_argsMap.get(name));
    }

    public final boolean getArgBoolean(final String name){
        return ConversionUtils.toBoolean(_argsMap.get(name));
    }

    public final int getArgInteger(final String name){
        return ConversionUtils.toInteger(_argsMap.get(name));
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    public abstract void ready();
    public abstract void shutdown();

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void runInternal() {
        try {
            // run lyj runner
            _runnerClass.getMethod("run").invoke(_runnerInstance);

            if (Lyj.isDebugMode()) {
                LoggingRepository.getInstance().setLevel(Level.FINE);
            }

            // ready method
            this.ready();
            if(null!=_handler){
                try {
                    Delegates.invoke(_handler);
                }catch(Throwable ignored){}
            }
        } catch (Exception x) {
            System.err.println("Uncaught exception: ");
            x.printStackTrace();
            System.exit(2);
        }
    }

    private ClassLoader init(final String[] args) throws Exception {
        // logger
        this.initLoggerPath();

        // read args
        this.parseArgs(args);

        // creates directories
        this.initDirs();

        final File home = getLyjHome();
        final ClassLoader loader = createClassLoader(home);


        return loader;
    }

    private void initLoggerPath(){
        // init internal logger
        LoggingRepository.getInstance().setFilePath(IConstants.PATH_LOG.concat("/lyj.log"));
    }

    private void initDirs() throws Exception {
        FileUtils.mkdirs(PathUtils.getAbsolutePath(IConstants.PATH_LOG));
        FileUtils.mkdirs(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES));
        //FileUtils.mkdirs(PathUtils.getAbsolutePath(IConstants.PATH_LIBRARIES));
        //FileUtils.mkdirs(PathUtils.getAbsolutePath(IConstants.PATH_PACKAGES));
    }

    private void parseArgs(final String[] args) throws
            CmdLineParser.UnknownOptionException,
            CmdLineParser.IllegalOptionValueException {
        if (null != args && args.length > 0) {
            final CmdLineParser parser = new CmdLineParser();

            final CmdLineParser.Option workspaceOpt = parser.addStringOption('w', "workspace");
            final CmdLineParser.Option charsetOpt = parser.addStringOption('c', "charset");
            final CmdLineParser.Option testOpt = parser.addBooleanOption('t', "test"); // test mode (only for test unit)
            final CmdLineParser.Option adminOpt = parser.addBooleanOption('a', "admin"); // admin mode (only for administration mode)
            final CmdLineParser.Option proxyOpt = parser.addBooleanOption('p', "proxy"); // proxy (-p true, -p false) if true uses system proxy

            parser.parse(args);

            String workspace = (String) parser.getOptionValue(workspaceOpt);
            if (null != workspace) {
                if (workspace.contains(APPDIR)) {
                    workspace = this.replaceInPath(workspace, APPDIR, IConstants.USER_DIR);
                } else if (workspace.contains(USERHOME)) {
                    workspace = this.replaceInPath(workspace, USERHOME, IConstants.USER_HOME);
                }
                System.setProperty(IConstants.SYSPROP_HOME, workspace);
                _argsMap.put("w", workspace);
            }

            final String charset = (String) parser.getOptionValue(charsetOpt, CharEncoding.getDefault());
            if (null != charset) {
                CharEncoding.setDefault(charset);
                _argsMap.put("c", charset);
            }

            final boolean test = (Boolean) parser.getOptionValue(testOpt, false);
            _argsMap.put("t", test);

            final boolean admin = (Boolean) parser.getOptionValue(adminOpt, false);
            _argsMap.put("a", admin);

            // set default proxy for outgoing communications
            final boolean use_proxy = (Boolean) parser.getOptionValue(proxyOpt, false);
            _argsMap.put("p", use_proxy);
            System.setProperty("java.net.useSystemProxies", use_proxy + ""); // java.net property
            System.setProperty(IConstants.SYSPROP_USE_PROXIES, use_proxy + ""); // smartly property

            //-- remaining --//
            _argsRemain = parser.getRemainingArgs();
            if (null != _argsRemain && _argsRemain.length > 0) {
                for (int i = 0; i < _argsRemain.length; i++) {
                    _argsMap.put("param_" + i, _argsRemain[i]);
                }
            }
        }
    }

    private String replaceInPath(final String path, final String what, final String with) {
        final String result = StringUtils.replace(path, what, with);
        return PathUtils.toUnixPath(result);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    /**
     * Create a server-wide ClassLoader from our install directory.
     * This will be used as parent ClassLoader for all application
     * ClassLoaders.
     *
     * @param home the ringo install directory
     * @return the main classloader we'll be using
     * @throws java.net.MalformedURLException
     */
    private static ClassLoader createClassLoader(final File home)
            throws IOException {
        /**
        final String classpath = System.getProperty(SYSPROP_CLASSPATH, DEFAULT_CLASSPATH);
        final String[] classes = classpath.split(",");

        // ensure classpath exists
        for (final String path : classes) {
            final String cleanPath = path.replaceAll("\\*", "");
            FileUtils.mkdirs(PathUtils.getAbsolutePath(cleanPath));
        }
        **/
        // creates and set the new class loader as context class loader
        final LyjClassLoader loader = new LyjClassLoader(home, new String[0]);
        Thread.currentThread().setContextClassLoader(loader);

        return loader;
    }

    /**
     * Get the Lyj install directory.
     *
     * @return the base install directory we're running in
     * @throws IOException                    an I/O related exception occurred
     * @throws java.net.MalformedURLException the jar URL couldn't be parsed
     */
    private static File getLyjHome()
            throws IOException {
        // check if home directory is set via system property
        String smartlyHome = System.getProperty(IConstants.SYSPROP_HOME);
        if (!StringUtils.hasText(smartlyHome)) {
            smartlyHome = System.getenv(ENVPROP_HOME);
        }

        if (!StringUtils.hasText(smartlyHome)) {

            URL launcherUrl = findUrl(LyjLauncher.class.getClassLoader());
            if (launcherUrl == null) {
                launcherUrl = findUrl(Thread.currentThread().getContextClassLoader());
            }
            if (launcherUrl == null) {
                launcherUrl = findUrl(ClassLoader.getSystemClassLoader());
            }

            // this is a  JAR URL of the form
            //    jar:<url>!/{entry}
            // we strip away the jar: prefix and the !/{entry} suffix
            // to get the original jar file URL

            String jarUrl = launcherUrl.toString();
            // decode installDir in case it is URL-encoded
            try {
                jarUrl = URLDecoder.decode(jarUrl, System.getProperty("file.encoding"));
            } catch (UnsupportedEncodingException x) {
                System.err.println("Unable to decode jar URL: " + x);
            }

            if (!jarUrl.startsWith("jar:") || !jarUrl.contains("!")) {
                smartlyHome = IConstants.USER_DIR; //System.getProperty("user.dir");
                System.err.println("Warning: lyj.home system property is not set ");
                System.err.println("         and not started from launcher.jar. Using ");
                System.err.println("         current working directory as install dir.");
            } else {
                int excl = jarUrl.indexOf("!");
                jarUrl = jarUrl.substring(4, excl);
                launcherUrl = new URL(jarUrl);
                smartlyHome = new File(launcherUrl.getPath()).getParent();
                if (smartlyHome == null) {
                    smartlyHome = IConstants.USER_DIR;
                }
            }
        }

        final File home = new File(smartlyHome).getCanonicalFile();
        // set System property (this must be called into main)
        System.setProperty(IConstants.SYSPROP_HOME, home.getPath());
        return home;
    }

    private static URL findUrl(final ClassLoader loader) {
        if (loader instanceof URLClassLoader) {
            return ((URLClassLoader) loader).findResource(LAUCHER_CLASS);
        }
        return null;
    }

}
