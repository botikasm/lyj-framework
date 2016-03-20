package org.lyj.desktopfences.app.controllers;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DesktopController
        extends AbstractLogEmitter {

    private static final String DESKTOP_PATH = PathUtils.getDesktopDirectory();
    private static final String ARCHIVE_PATH = PathUtils.getAbsolutePath("archive");

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DesktopController() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void catalogue() {
        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, new File(DESKTOP_PATH));
        this.catalogue(files);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void catalogue(final List<File> files) {
        for (final File file : files) {
            try {
                this.catalogue(file);
            } catch (Throwable t) {
                super.error("catalogue", t);
            }
        }
    }

    private void catalogue(final File file){

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
