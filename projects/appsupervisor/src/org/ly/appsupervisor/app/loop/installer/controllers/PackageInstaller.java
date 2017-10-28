package org.ly.appsupervisor.app.loop.installer.controllers;

import org.json.JSONObject;
import org.ly.appsupervisor.app.loop.launcher.controllers.ActionController;
import org.lyj.commons.cryptograph.GUID;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PackageInstaller
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String INSTALL_JSON = "install.json";

    private static final String FLD_TARGET = "target";
    private static final String FLD_EXTENSIONS = "extensions";
    private static final String FLD_ACTION_BEFORE = "action-before";
    private static final String FLD_ACTION_AFTER = "action-after";


    private static final String TEMP_PATH = PathUtils.getAbsolutePath("./tmp");

    private static final String[] INVALID_PATHS = new String[]{
            "MACOSX",
            ".DS_Store"
    };

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public PackageInstaller() {


    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean install(final File file) {
        try {
            if (this.isZip(file)) {
                return this.tryInstall(file);
            }
        } catch (Throwable t) {
            super.error("install", t);
        }
        return false;
    }

    public boolean install(final List<File> files) {
        try {
            return this.tryInstall(files);
        } catch (Throwable t) {
            super.error("install", t);
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean isZip(final File file) {
        try {
            return PathUtils.getFilenameExtension(file.getName(), true).equalsIgnoreCase(".zip");
        } catch (Throwable ignored) {
        }
        return false;
    }

    private boolean tryInstall(final File file) {
        try {
            final String temp_path = PathUtils.concat(TEMP_PATH, GUID.create());
            try {
                FileUtils.mkdirs(temp_path);
                ZipUtils.unzip(file.getAbsolutePath(), temp_path);

                final List<File> files = new LinkedList<>();
                FileUtils.listFiles(files, new File(temp_path), "*.*", "", -1);

                return this.tryInstall(files);
            } finally {
                FileUtils.delete(temp_path);
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private boolean tryInstall(final List<File> files) throws Exception {
        if (!files.isEmpty()) {
            final JSONObject info = this.getInstallInfo(files);
            if (null != info) {
                final String target_path = info.optString(FLD_TARGET);
                final String action_before = info.optString(FLD_ACTION_BEFORE);
                final String action_after = info.optString(FLD_ACTION_AFTER);
                if (StringUtils.hasText(target_path) && files.size() > 1) {
                    // ACTION BEFORE
                    this.doAction(action_before);
                    // copy files
                    for (final File installable_file : files) {
                        if (this.isInstallable(installable_file)) {
                            this.copy(installable_file, target_path);
                        }
                    }
                    // ACTION AFTER
                    this.doAction(action_after);

                    return true;
                }
            } else {
                super.warning("tryInstall", "MISSING 'install.json' configuration file.");
            }
        }
        return false;
    }

    private boolean isInstallInfoFile(final File file) {
        return file.getName().equalsIgnoreCase(INSTALL_JSON);
    }

    private JSONObject getInstallInfo(final List<File> files) throws IOException {
        for (final File file : files) {
            if (this.isInstallInfoFile(file)) {
                final String text = FileUtils.readFileToString(file.getAbsoluteFile());
                return new JSONObject(text);
            }
        }
        return null;
    }

    private boolean isInstallable(final File file) {
        return !file.isDirectory()
                && StringUtils.hasText(file.getName())
                && !isInstallInfoFile(file)
                && !containsInvalidPath(file);
    }

    private boolean containsInvalidPath(final File file) {
        for (final String invalid : INVALID_PATHS) {
            if (file.getAbsolutePath().contains(invalid)) {
                return true;
            }
        }
        return false;
    }

    private void copy(final File file,
                      final String target_path) throws IOException {
        final String root = PathUtils.getAbsolutePath(target_path);
        final String file_name = PathUtils.concat(root, PathUtils.getFilename(file.getAbsolutePath(), true));
        FileUtils.mkdirs(root);
        FileUtils.copy(file, new File(file_name));
    }

    private void doAction(final String action) throws Exception {
        if (StringUtils.hasText(action)) {
            ActionController.instance().run(action);
        }
    }
}
