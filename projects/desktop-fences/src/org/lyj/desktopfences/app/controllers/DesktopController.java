package org.lyj.desktopfences.app.controllers;

import org.lyj.commons.io.FileObserver;
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

    private FileObserver _observer;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DesktopController() {
        _root = DesktopFences.instance().settings().teleport(); // directory to monitor
        _closed = false;
        _working = false;

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {
        _closed = true;
        _working = false;
        ArchiveController.instance().close();
        if (null != _observer) {
            _observer.stopWatching();
            _observer = null;
        }
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

    private void init() {
        try {
            _observer = new FileObserver(_root, true, false, (ievent, path) -> {
                if (FileObserver.EVENT_CREATE == ievent) {
                    // created new file
                    this.handleNewFile(path);
                }
            });
            _observer.startWatching();
        } catch (Throwable t) {
            super.error("init", t);
        }
    }

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
                this.catalogue(file, move, true);  // existing files
            } catch (Throwable t) {
                super.error("catalogue", FormatUtils.format("Error archiving '%s': %s", file.getPath(), t.toString()));
            }
        }
    }

    private void catalogue(final File file,
                           final boolean move,
                           final boolean checkCRC) throws FileNotFoundException {
        if (ArchiveFile.isValid(file)) {
            final ArchiveFile archive = ArchiveFile.create(file, checkCRC);
            ArchiveController.instance().put(archive, move, null);
        }
    }

    private void handleNewFile(final String path) {
        try {
            final File file = new File(path);
            if (file.exists()) {
                this.catalogue(file, true, true);
            }
        } catch (Throwable t) {

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
