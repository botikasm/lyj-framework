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

package org.ly.launcher;


import org.ly.IConstants;
import org.ly.Smartly;
import org.ly.commons.io.repository.FileRepository;
import org.ly.commons.io.repository.Resource;
import org.ly.commons.io.repository.deploy.FileDeployer;
import org.ly.commons.util.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SmartlyPackageLoader {

    private final String _root;
    private final Map<String, AbstractPackage> _packages;
    private List<AbstractPackage> _sortList;
    private boolean _runned;
    private AbstractPackage _modalPackage;

    public SmartlyPackageLoader() throws IOException {
        _root = PathUtils.join(Smartly.getHome(), IConstants.PATH_PACKAGES);
        _packages = Collections.synchronizedMap(new HashMap<String, AbstractPackage>());
        _runned = false;
        _modalPackage = null;

        // ensure dir exists
        FileUtils.mkdirs(_root);
    }

    public Set<String> getPackageNames() {
        return _packages.keySet();
    }

    public void register(final AbstractPackage instance) {
        this.register(instance, null);
    }

    /**
     * Programmatically registration of a package.
     * Usually packages are loaded from package folder, but when launched from IDE you may
     * find useful manual registration.
     *
     * @param instance Package Instance
     */
    public void register(final AbstractPackage instance,
                         final Boolean modal) {
        if (null != instance) {
            final String key = instance.getId();
            synchronized (_packages) {
                if (!_packages.containsKey(key)) {
                    if (isModal(instance, modal)) {
                        if (null != _modalPackage) {
                            final String msg = FormatUtils.format("Modal Package already registered. Only one modal " +
                                            "package is allowed. '{0}->{1}' will not be registered.",
                                    key, instance.getClass().getCanonicalName());
                            this.warning(msg);
                            return;
                        }
                        _modalPackage = instance;
                    }
                    _packages.put(key, instance);
                    // ensure directory exists
                    this.ensureExists(instance);
                    this.info(FormatUtils.format("REGISTERED MODULE: {0}", key));
                } else {
                    final AbstractPackage existing = _packages.get(key);
                    if (!existing.getClass().getCanonicalName().equalsIgnoreCase(instance.getClass().getCanonicalName())) {
                        final String msg = FormatUtils.format("TWO PACKAGES WITH SAME ID: " +
                                        "Package '{0}' already exists and is of type '{1}'. Package ID must be unique. " +
                                        "You are trying to register another package with same ID but of type '{2}'.",
                                key, existing.getClass().getCanonicalName(), instance.getClass().getCanonicalName());
                        this.warning(msg);
                    }
                }
            }
        }
    }



    public void load() throws Exception {
        if (_runned) {
            return;
        }
        _runned = true;

        //-- load from repository--//
        this.loadPackages();

        //-- run all packages in order --//
        _sortList = this.sort();
        this.load(_sortList);
    }

    public void ready() {
        if (!CollectionUtils.isEmpty(_sortList)) {
            for (final AbstractPackage item : _sortList) {
                try {
                    //-- Modal package is last --//
                    if (!item.equals(_modalPackage)) {
                        item.ready();
                    }
                } catch (Throwable t) {
                    this.severe(FormatUtils.format("ERROR CALLING METHOD 'ready()' FROM PACKAGE '{0}': {1}",
                            item.getId(), ExceptionUtils.getRealMessage(t)));
                }
            }
            if (null != _modalPackage) {
                this.info(FormatUtils.format(
                        "Smartly started [{0}]::{1} as MODAL.",
                        _modalPackage.getId(), _modalPackage.getClass().getName()));
                _modalPackage.ready();
            }
        }
    }

    public void unload() {
        if (!CollectionUtils.isEmpty(_sortList)) {
            for (final AbstractPackage item : _sortList) {
                try {
                    //-- Modal package is last --//
                    if (!item.equals(_modalPackage)) {
                        item.unload();
                    }
                } catch (Throwable t) {
                    this.severe(FormatUtils.format("ERROR CALLING METHOD 'unload()' FROM PACKAGE '{0}': {1}",
                            item.getId(), ExceptionUtils.getRealMessage(t)));
                }
            }
            if (null != _modalPackage) {
                this.info(FormatUtils.format(
                        "Smartly stopped [{0}]::{1} as MODAL.",
                        _modalPackage.getId(), _modalPackage.getClass().getName()));
                _modalPackage.unload();
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    private void info(final String message) {
        Smartly.getLogger().info(this, message);
    }

    private void warning(final String message) {
        Smartly.getLogger().warning(this, message);
    }

    private void severe(final String message) {
        Smartly.getLogger().severe(this, message);
    }

    private void severe(final String message, final Throwable error) {
        Smartly.getLogger().severe(this, message, error);
    }

    private void loadPackages() throws IOException {
        final FileRepository repository = new FileRepository(_root);
        final Resource[] resources = repository.getResources(true);
        for (final Resource resource : resources) {
            if (null != resource) {
                this.loadPackage(resource);
            }
        }
    }

    private void loadPackage(final Resource resource) {
        try {
            final String content = resource.getContent(Smartly.getCharset());
            final JsonWrapper json = new JsonWrapper(content);
            final String name = json.optString("name");
            final String main = json.optString("main");
            if (!StringUtils.hasText(main)) {
                final String msg = FormatUtils.format(
                        "Unable to load Package '{0}': Missing 'main' attribute.",
                        name);
                this.severe(msg);
            } else {
                this.registerPackageLauncher(name, main);
            }
        } catch (Throwable t) {
            this.severe(FormatUtils.format("Unmanaged Exception loading Packages: '{0}'", t), t);
        }
    }

    private void registerPackageLauncher(final String name, final String className) {
        try {
            final Class clazz = ClassLoaderUtils.forName(className);
            if (null == clazz) {
                throw new Exception("Class not found in current loader.");
            }
            final AbstractPackage launcher = (AbstractPackage) clazz.newInstance();
            this.register(launcher);
        } catch (Throwable t) {
            this.severe(FormatUtils.format("Exception loading Package '{0}' from class '{1}': '{2}'",
                            name, className, t),
                    t);
        }
    }

    private List<AbstractPackage> sort() {
        final Collection<AbstractPackage> packages = _packages.values();
        List<AbstractPackage> list = new ArrayList<AbstractPackage>(packages);
        Collections.sort(list);
        return list;
    }

    private void load(final List<AbstractPackage> list) {
        for (final AbstractPackage item : list) {
            try {
                item.load();
                this.info(FormatUtils.format("STARTED MODULE: {0}", item.getId()));
                // flush deployers
                FileDeployer.deployAll();
            } catch (Throwable t) {
                final String msg = FormatUtils.format("Error running Package '{0}': '{1}'", item.getId(), t);
                this.severe(msg, t);
            }
        }
    }

    private void ensureExists(final AbstractPackage pkg) {
        try {
            final String packagePath = PathUtils.join(_root, pkg.getId());
            FileUtils.mkdirs(packagePath);
            final File packageJson = new File(PathUtils.join(packagePath, "package.json"));
            if (!packageJson.exists()) {
                // read default
                this.copyDefault(packageJson, pkg);
            }
        } catch (Throwable t) {
            this.severe(null, t);
        }
    }

    private void copyDefault(final File packageJson, final AbstractPackage pkg) throws IOException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader(); //this.getClass().getClassLoader();
        final String path = PathUtils.getPackagePath(this.getClass());
        final String filePath = PathUtils.join(path, packageJson.getName());
        final InputStream is = cl.getResourceAsStream(filePath);
        if (null != is) {
            final byte[] content = ByteUtils.getBytes(is);

            //format content with class data
            final Map<String, String> data = new HashMap<String, String>();
            data.put("name", pkg.getId());
            data.put("main", pkg.getClass().getCanonicalName());
            data.put("version", pkg.getVersion());
            data.put("description", pkg.getDescription());
            data.put("maintainer_mail", pkg.getMaintainerMail());
            data.put("maintainer_url", pkg.getMaintainerUrl());
            data.put("maintainer_name", pkg.getMaintainerName());

            final String json = FormatUtils.formatTemplate(new String(content), "<", ">", data);

            FileUtils.copy(json.getBytes(), packageJson);
        } else {
            this.warning(FormatUtils.format("RESOURCE '{0}' not found. " +
                    "Ensure you included it in your package distribution " +
                    "(i.e. check IDE settings for Compiler Options.)", filePath));
        }
    }

    private static boolean isModal(final AbstractPackage instance, final Boolean modal) {
        if (null == modal) {
            return instance instanceof ISmartlyModalPackage;
        } else {
            return modal;
        }
    }
}
