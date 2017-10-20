package org.ly.appsupervisor.app.loop.installer.controllers;

import org.ly.appsupervisor.app.IConstants;
import org.lyj.Lyj;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FileController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = PathUtils.getAbsolutePath(IConstants.PATH_INSTALL);

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _root;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileController() {
        this(ROOT);
    }

    public FileController(final String root) {
        this.root(root);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public FileController root(final String value) {
        _root = value;
        try {
            FileUtils.mkdirs(value);
        } catch (Throwable ignored) {
        }
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Collection<File> check() {
        final List<File> files = new LinkedList<>();
        try {
            // get files
            FileUtils.listFiles(files, new File(_root));

            // check file integrity
            for (final File file : files) {
                if (!this.isReady(file)) {
                    throw new Exception("Invalid File");
                }
            }

        } catch (Throwable ignored) {
        }
        return files;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    private boolean isReady(final File file) {
        try {
            return FileUtils.getSize(file) > 0;
        } catch (Throwable ignored) {

        }
        return false;
    }

}
