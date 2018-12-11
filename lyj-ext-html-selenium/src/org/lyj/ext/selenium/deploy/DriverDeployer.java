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

package org.lyj.ext.selenium.deploy;


import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.commons.util.*;
import org.lyj.ext.selenium.loggers.MainLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DriverDeployer
        extends FileDeployer {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH_BIN = "./bin";
    private static final String OS = SystemUtils.getOSSymbol();

    private static final Map<String, String> DRIVER_PROPS = MapBuilder.createSS()
            .put("gecko", "webdriver.gecko.driver")
            .put("chrome", "webdriver.chrome.driver")
            .toMap();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final static MainLogger _logger = new MainLogger();

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DriverDeployer(final String targetFolder, final boolean silent) {
        super("", targetFolder,
                silent, false, false, false);

        super.setOverwrite(true);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public byte[] compile(byte[] data, final String filename) {
        return data;
    }

    @Override
    public byte[] compress(byte[] data, final String filename) {
        return null;
    }

    @Override
    public void deploy() {
        super.deploy();
    }

    @Override
    public void finish() {
        _logger.info("DriverDeployer.finish", "\t----------------");
        try {
            moveDrivers(this.getTargetFolder());
        } catch (Throwable t) {
            _logger.error("finish", t);
        }

        // remove folder
        FileUtils.tryDelete(this.getTargetFolder());
        _logger.info("DriverDeployer.finish", "\t----------------");
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static boolean isArchive(final String file_name) {
        final String ext = PathUtils.getFilenameExtension(file_name, true);
        return CollectionUtils.contains(new String[]{".zip", ".gz"}, ext);
    }

    private static void moveDrivers(final String start_folder) throws Exception {
        final String target_folder = PathUtils.combine(PathUtils.getParent(start_folder), PATH_BIN);
        FileUtils.tryMkdirs(target_folder);

        final List<File> files = new ArrayList<>();
        FileUtils.list(files, new File(start_folder), "*.*", "", -1, true);
        if (!files.isEmpty()) {
            for (final File file : files) {
                if(file.isFile()){
                    move(file, target_folder);
                }
            }
        }

        FileUtils.tryDelete(start_folder);
    }

    private static void move(final File file, final String target_folder) {
        try {
            if (file.getAbsolutePath().contains(OS)) {
                if (isArchive(file.getName())) {
                    // unzip to target
                    final String path = target_folder;
                    ZipUtils.unzip(file.getAbsolutePath(), path);
                    assignDriver(file.getAbsolutePath(), path);
                } else {
                    // copy to target
                    final String path = PathUtils.concat(target_folder, file.getName());
                    FileUtils.copy(file, new File(path));
                    assignDriver(file.getAbsolutePath(), target_folder);
                }
            }
        } catch (Throwable t) {
            _logger.error("move#" + file, t);
        }
    }

    private static String getDriverType(final String file_name) {
        final Set<String> keys = DRIVER_PROPS.keySet();
        for (final String key : keys) {
            if (file_name.toLowerCase().contains(key)) {
                return key;
            }
        }
        return "";
    }

    private static File getDriver(final String dir, final String type) {
        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, new File(dir));
        for (final File file : files) {
            if (file.getName().toLowerCase().contains(type)) {
                return file;
            }
        }
        return null;
    }

    private static void assignDriver(final String source_file,
                                     final String path) {
        final String key = getDriverType(source_file);
        if (StringUtils.hasText(key)) {
            final File file = getDriver(path, key);
            if (null != file) {
                final String prop = DRIVER_PROPS.get(key);
                final String driver_path = file.getAbsolutePath();
                System.setProperty(prop, driver_path);
                _logger.info("assignDriver", FormatUtils.format("\t\t\t%s = %s", prop, driver_path));
            }
        }
    }

}
