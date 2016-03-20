package org.lyj.desktopfences.app.controllers;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
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

    private static final String DESKTOP_PATH = PathUtils.getDesktopDirectory();

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DesktopController() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void catalogue() {
        this.catalogue(false);
    }

    public void catalogue(final boolean move) {
        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, new File(DESKTOP_PATH));
        this.catalogue(files, move);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void catalogue(final List<File> files, final boolean move) {
        for (final File file : files) {
            try {
                this.catalogue(file, move);
            } catch (Throwable t) {
                super.error("catalogue", t);
            }
        }
    }

    private void catalogue(final File file, final boolean move) throws FileNotFoundException {
        if(ArchiveFile.isValid(file)){
            final ArchiveFile archive = ArchiveFile.create(file);
            ArchiveController.instance().put(archive, move);
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
