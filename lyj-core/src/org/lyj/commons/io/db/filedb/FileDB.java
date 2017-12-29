package org.lyj.commons.io.db.filedb;

import org.lyj.commons.async.Locker;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.util.*;

public final class FileDB
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = PathUtils.getAbsolutePath("./filedb");
    private static final String COLLECTIONS_PATH = "./collections";
    private static final String COLL_EXT = IFileDBConstants.COLL_EXT;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final String _collections_path;
    private final Map<String, FileDBCollection> _collections;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileDB(final String name) {
        this(ROOT, name);
    }

    public FileDB(final String root,
                  final String name) {
        _root = PathUtils.concat(root, name);
        _collections_path = PathUtils.concat(_root, COLLECTIONS_PATH);
        _collections = Collections.synchronizedMap(new HashMap<>());

        LoggingRepository.getInstance().setLogFileName(FileDB.class, "filedb.log");

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String dbPath() {
        return _root;
    }

    public String collectionsPath() {
        return _collections_path;
    }

    public String[] collectionNames() {
        synchronized (_collections) {
            final Set<String> keys = _collections.keySet();
            return keys.toArray(new String[keys.size()]);
        }
    }

    public FileDBCollection[] collections(){
        synchronized (_collections) {
            final Collection<FileDBCollection> values = _collections.values();
            return values.toArray(new FileDBCollection[values.size()]);
        }
    }

    public FileDBCollection collection(final String name) {
        synchronized (_collections) {
            if (!_collections.containsKey(name)) {
                _collections.put(name, new FileDBCollection(this, this.collectionPath(name)));
            }
            return _collections.get(name);
        }
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    void removeCollection(final String name) {
        synchronized (_collections) {
            _collections.remove(name);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // ensure root exists
        FileUtils.tryMkdirs(_root);

        // initialize collections
        this.loadCollections();
    }

    private void loadCollections() {
        Locker.instance().lock(_collections_path);
        try {
            final List<File> files = new ArrayList<>();
            FileUtils.list(files, new File(_collections_path), "*".concat(COLL_EXT), "", 1, false);
            for (final File file : files) {
                final String name = PathUtils.getFilename(file.getName(), false);
                _collections.put(name, new FileDBCollection(this, file.getAbsolutePath()));
            }
        } catch (Throwable t) {
            super.error("loadCollections", t);
        } finally {
            Locker.instance().unlock(_collections_path);
        }
    }

    private String collectionPath(final String name) {
        return PathUtils.concat(_collections_path, name.concat(COLL_EXT));
    }

}
