package org.ly.server.application.importer;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Base importer class
 */
public abstract class AbstractImporter
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String CONFIG_FILE_NAME = ImportConfig.CONFIG_FILE_NAME;
    private static final String LOGGER_NAME = PackageImporter.LOGGER_NAME;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Collection<File> _files;

    private ImportConfig _config;
    private String _root;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractImporter(final Collection<File> files) {
        // logger
        LoggingRepository.getInstance().setLogFileName(this.getClass(), LOGGER_NAME);

        _files = new ArrayList<>();

        this.init(files);

    }

    public abstract String type();

    public abstract void startImport();

    public abstract boolean isValidFile(final File file);

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public boolean isReady() {
        try {
            if (!_files.isEmpty() && null != _config) {
                return this.type().equalsIgnoreCase(_config.type());
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public ImportConfig config() {
        return _config;
    }


    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected File[] files() {
        return _files.toArray(new File[0]);
    }

    protected boolean hasFiles() {
        return !_files.isEmpty();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final Collection<File> files) {
        try {
            for (final File file : files) {
                if (isConfig(file)) {
                    _config = config(file);
                    _root = PathUtils.getParent(file.getAbsolutePath());
                } else if (this.isValidFile(file)) {
                    _files.add(file);
                }
            }
        } catch (Throwable t) {
            super.error("init", t);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static boolean isConfig(final File file) {
        return file.getName().equalsIgnoreCase(CONFIG_FILE_NAME);
    }

    public static ImportConfig config(final File file) throws Exception {
        final String root = file.getParent();
        final String file_name = PathUtils.combine(root, CONFIG_FILE_NAME);
        if (FileUtils.exists(file_name)) {
            final String content = FileUtils.readFileToString(new File(file_name));
            return new ImportConfig(content);
        }
        return new ImportConfig(null);
    }

}
