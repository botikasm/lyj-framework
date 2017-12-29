package org.lyj.commons.io.db.filedb.exporter;

import org.lyj.commons.async.Locker;
import org.lyj.commons.io.db.filedb.FileDB;
import org.lyj.commons.io.db.filedb.FileDBCollection;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.json.JsonItem;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractFileDBExporter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonItem _properties;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractFileDBExporter() {
        _properties = new JsonItem();
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    /**
     * Return handled file extension for current exporter
     */
    protected abstract String extension();

    protected abstract void export(final FileDBCollection collection, final String target) throws Exception;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public JsonItem properties() {
        return _properties;
    }

    public String[] export(final FileDB db) throws Exception {
        final Collection<String> paths = new ArrayList<>();
        final FileDBCollection[] collections = db.collections();
        for (final FileDBCollection collection : collections) {
            paths.add(this.export(collection));
        }
        return paths.toArray(new String[paths.size()]);
    }

    public String export(final FileDBCollection collection) throws Exception {
        final String ext = this.extension();
        final String root = PathUtils.concat(collection.db().dbPath(), "/exports");
        final String path = PathUtils.concat(root, collection.name().concat(ext.contains(".") ? ext : ".".concat(ext)));
        FileUtils.mkdirs(path);
        Locker.instance().lock(path);
        try {
            this.export(collection, path);
            return path;
        } finally {
            Locker.instance().unlock(path);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected Writer getWriter(final String file_name) throws IOException {
        return new BufferedWriter(new FileWriter(new File(file_name)));
    }

}
