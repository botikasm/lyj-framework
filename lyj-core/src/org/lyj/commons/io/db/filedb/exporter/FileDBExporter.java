package org.lyj.commons.io.db.filedb.exporter;

import org.lyj.commons.io.db.filedb.exporter.impl.FileDBExporterCSV;
import org.lyj.commons.io.db.filedb.exporter.impl.FileDBExporterJSON;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileDBExporter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Class<? extends AbstractFileDBExporter>> _exporters;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private FileDBExporter() {
        _exporters = Collections.synchronizedMap(new HashMap<>());

        this.init();
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void register(final String extension,
                         final Class<? extends AbstractFileDBExporter> exporter_class) {
        synchronized (_exporters) {
            final String key = this.getKey(extension);
            _exporters.put(key, exporter_class);
        }
    }

    public AbstractFileDBExporter exporter(final String extension) throws Exception {
        return this.getExporter(extension);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        this.registerExporters();
    }

    private void registerExporters() {
        this.register(FileDBExporterJSON.EXTENSION, FileDBExporterJSON.class);
        this.register(FileDBExporterCSV.EXTENSION, FileDBExporterCSV.class);
    }

    private String getKey(final String extension) {
        return StringUtils.replace(extension, ".", "").toLowerCase();
    }

    private AbstractFileDBExporter getExporter(final String extension) throws Exception {
        synchronized (_exporters) {
            try {
                final String key = this.getKey(extension);
                if (_exporters.containsKey(key)) {
                    final Class<? extends AbstractFileDBExporter> aclass = _exporters.get(key);
                    return (AbstractFileDBExporter) ClassLoaderUtils.newInstance(aclass);
                }
            } catch (Throwable ignored) {

            }
            throw new Exception("Extension not supported: " + extension);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static FileDBExporter __instance;

    public static synchronized FileDBExporter instance() {
        if (null == __instance) {
            __instance = new FileDBExporter();
        }
        return __instance;
    }

}
