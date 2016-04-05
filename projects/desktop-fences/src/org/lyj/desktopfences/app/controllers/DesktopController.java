package org.lyj.desktopfences.app.controllers;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.desktopfences.app.DesktopFences;
import org.lyj.desktopfences.app.controllers.archive.ArchiveController;
import org.lyj.desktopfences.app.controllers.archive.ArchiveFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DesktopController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _root;
    private boolean _closed;
    private boolean _working;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DesktopController() {
        _root = DesktopFences.instance().settings().teleport();
        _closed = false;
        _working = false;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {
        _closed = true;
        _working = false;
        ArchiveController.instance().close();
        super.logger().info("DesktopController closed.");
    }

    public boolean isWorking() {
        return _working;
    }

    public void scan(final boolean move) {
        this.scan(_root, move);
    }

    public void scan(final String path,
                     final boolean move) {
        if (!_closed && !_working) {
            try {
                _working = true;
                final List<File> files = new ArrayList<>();
                this.mkdirs(path);
                FileUtils.list(files, new File(path), "*.*", "", 0, true);
                this.catalogue(files, move);
            } finally {
                _working = false;
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void mkdirs(final String path) {
        try {
            FileUtils.mkdirs(path);
        } catch (IOException e) {
            super.error("mkdirs", e);
        }
    }

    private void catalogue(final List<File> files, final boolean move) {
        for (final File file : files) {
            if (_closed) {
                break;
            }
            try {
                this.catalogue(file, move);
            } catch (Throwable t) {
                super.error("catalogue", FormatUtils.format("Error archiving '%s': %s", file.getPath(), t.toString()));
            }
        }
    }

    private void catalogue(final File file,
                           final boolean move) throws FileNotFoundException {
        if (ArchiveFile.isValid(file)) {
            final ArchiveFile archive = ArchiveFile.create(file);
            ArchiveController.instance().put(archive, move, null);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static DesktopController __instance;

    public static DesktopController instance() {
        if (null == __instance) {
            __instance = new DesktopController();
        }
        return __instance;
    }


}
