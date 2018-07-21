package org.ly.ose.server.application.importer;

import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.importer.impl.ProgramImporter;
import org.lyj.commons.io.packets.PacketMonitorTask;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.util.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PackageImporter
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                     c o n s t
    // ------------------------------------------------------------------------

    public static final String LOGGER_NAME = "importer.log";

    private static final String ROOT = PathUtils.combine(IConstants.ROOT_FILES, "install");

    // ------------------------------------------------------------------------
    //                     f i e l d s
    // ------------------------------------------------------------------------

    private final PacketMonitorTask _task;
    private final Map<String, Class<? extends AbstractImporter>> _importers;

    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private PackageImporter() {
        // creates task to monitor packets
        _task = new PacketMonitorTask(15); // 15 seconds task
        // importers
        _importers = new HashMap<>();

        // logger
        LoggingRepository.getInstance().setLogFileName(this.getClass(), LOGGER_NAME);

        this.init();

        super.info("constructor", "Importer initialized");
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        if (null != _task) {
            _task.open();
        }
    }

    public void close() {
        if (null != _task) {
            _task.close();
        }
    }

    public void force() {
        if (null != _task) {
            _task.force();
        }
    }


    public String root() {
        return ROOT;
    }

    public void put(final File zip_file) throws IOException {
        final String file_name = PathUtils.getFilename(zip_file.getAbsolutePath(), true);
        final String path = PathUtils.concat(ROOT, file_name);
        FileUtils.copy(zip_file, new File(path));
        FileUtils.delete(zip_file);
    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // init folder and task
        FileUtils.tryMkdirs(ROOT);
        try {
            _task.root(ROOT);
            _task.callback(this::handleOnPackageFiles);
        } catch (Throwable ignored) {
        }

        // register importers
        _importers.put(ProgramImporter.TYPE, ProgramImporter.class);
    }

    /**
     * Received pack
     */
    private void handleOnPackageFiles(final File package_zip,
                                      final List<File> files) {
        try {
            final AbstractImporter importer = this.getImporter(package_zip, files);
            if (null != importer && importer.isReady()) {
                super.info("handleOnPackageFiles", FormatUtils.format("START IMPORTING '%s'.",
                        package_zip.getName()));

                // import data
                importer.startImport();

                super.info("handleOnPackageFiles", FormatUtils.format("END IMPORTING '%s'.",
                        package_zip.getName()));
            }
        } catch (Throwable t) {
            super.error("handleOnPackageFiles", t);
        }
    }

    private AbstractImporter getImporter(final File package_zip,
                                         final List<File> files) {
        try {
            for (final File file : files) {
                if (AbstractImporter.isConfig(file)) {
                    final ImportConfig config = AbstractImporter.config(file);
                    final String type = config.type();
                    if (StringUtils.hasText(type)) {
                        return this.getImporter(type, package_zip, files);
                    }
                    break;
                }
            }
        } catch (Throwable ignored) {
            // ignored
        }
        return null;
    }

    private AbstractImporter getImporter(final String type,
                                         final File package_zip,
                                         final List<File> files) {
        try {
            final Class<? extends AbstractImporter> importer_class = _importers.get(type);
            if (null != importer_class) {
                return (AbstractImporter) ClassLoaderUtils.newInstance(importer_class,
                        new Object[]{files});
            }
        } catch (Throwable t) {
            super.error("getImporter", t);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                     S T A T I C
    // ------------------------------------------------------------------------

    private static PackageImporter __instance;

    public static synchronized PackageImporter instance() {
        if (null == __instance) {
            __instance = new PackageImporter();
        }
        return __instance;
    }


}
