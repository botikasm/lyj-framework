package org.lyj.desktopfences.app.controllers;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopfences.app.controllers.archive.ArchiveController;
import org.lyj.desktopfences.app.controllers.archive.ArchiveFile;

import java.io.File;
import java.io.FileNotFoundException;
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

    private static final String DESKTOP_PATH = PathUtils.getDesktopDirectory();

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _closed;
    private boolean _working;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DesktopController() {
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

    public void catalogueDesktop(final boolean move) {
        if (!_closed && !_working) {
            try {
                _working = true;
                final List<File> files = new ArrayList<>();
                FileUtils.list(files, new File(DESKTOP_PATH), "*.*", "", 0, true);
                this.catalogue(files, move);
            } finally {
                _working = false;
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

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
