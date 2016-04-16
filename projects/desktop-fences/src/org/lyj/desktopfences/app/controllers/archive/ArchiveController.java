package org.lyj.desktopfences.app.controllers.archive;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.FixedBlockingPool;
import org.lyj.commons.io.db.jdbm.JDB;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopfences.app.IConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class ArchiveController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ARCHIVE_PATH = IConstants.ARCHIVE_PATH;
    private static final String ARCHIVE_FILES = PathUtils.combine(ARCHIVE_PATH, "files");
    private static final String ARCHIVE_DATA = PathUtils.combine(ARCHIVE_PATH, "data");

    private static final String COLLECTION_FILES = "files";
    private static final String ID = IConstants.ID;


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JDB _db;

    private final Indexes _categories;
    private final Indexes _tags;
    private final Indexes _directories;

    private int _count_archived;
    private int _count_processed;
    private boolean _closed;
    private FixedBlockingPool _threads;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ArchiveController() {
        _threads = FixedBlockingPool.create()
                .capacity(20).corePoolSize(5).maximumPoolSize(10)
                .keepAliveTime(30, TimeUnit.MINUTES);
        _count_archived = 0;
        _count_processed = 0;
        _db = JDB.create(ARCHIVE_DATA).open("db");
        _categories = new Indexes();
        _tags = new Indexes();
        _directories = new Indexes();
        _closed = false;

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String flushActivities() {
        final String response = FormatUtils.format("Archived %s files within %s threads.", _count_archived, this.countCompletedTasks());
        return response;
    }

    public void close() {
        try {
            final int still_active = this.countActiveTasks();
            _closed = true;
            _threads.stop();
            while (!_threads.isTerminated()) {
            }
            super.logger().info(FormatUtils.format("ArchiveController closed (%s threads was alive while closing).", still_active));
        } finally {
            _db.close(); // close to avoid data corruption
        }
    }

    public void put(final ArchiveFile file,
                    final boolean move,
                    final Delegates.SingleResultCallback<PreparedArchive> callback) {
        if (_closed) {
            return;
        }
        super.logger().debug("Preparing: " + file.pathOrigin());
        // check if move file or directory
        this.prepare(file, (err, prepared) -> {
            if (!_closed) {
                if (null == err) {
                    try {
                        // add indexes to database
                        final List<ArchiveFile> files = prepared.resources();
                        for (final ArchiveFile item : files) {
                            if (_closed) {
                                break;
                            }
                            _count_processed++;
                            final boolean exists_id = this.dbFileExists(item);
                            final boolean exists_file = FileUtils.exists(item.pathArchive());
                            if (!_closed && (!exists_id || !exists_file)) {

                                _threads.start(() -> {

                                    if (!_closed) {
                                        // update dtabase
                                        if (this.dbFileUpsert(item)) {
                                            //-- move archive --//
                                            this.copy(item, move);
                                        }

                                        _count_archived++;
                                    }

                                });
                            } else {
                                super.logger().debug(FormatUtils.format("File already archived '%s'", item.pathOrigin()));
                            }
                        }
                        Delegates.invoke(callback, null, prepared);
                    } catch (Throwable t) {
                        Delegates.invoke(callback, t, null);
                        super.error("put", t);
                    }
                } else {
                    Delegates.invoke(callback, err, null);
                }
            }
        });
    }

    public void restore(final ArchiveFile file,
                        final boolean deleteArchive) throws IOException {
        if (!_closed) {
            FileUtils.mkdirs(file.pathOrigin());
            FileUtils.copy(new File(file.pathArchive()), new File(file.pathOrigin()));
            if (deleteArchive) {
                this.delete(file);
            }
        }
    }

    public void delete(final ArchiveFile file) throws IOException {
        if (!_closed) {
            FileUtils.delete(file.pathArchive());
            this.dbFileRemove(file.id());
        }
    }

    public void reloadIndexes() {
        this.rebuildIndexesAsync();
    }

    public int countActiveTasks() {
        if (null != _threads) {
            return _threads.getActiveCount();
        }
        return 0;
    }

    public long countCompletedTasks() {
        if (null != _threads) {
            return _threads.getCompletedCount();
        }
        return 0;
    }

    public long countArchived() {
        return _count_archived;
    }

    public long countProcessed() {
        return _count_processed;
    }

    public long countInArchive() {
        try {
            return this.dbCount();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    public Indexes categories() {
        return _categories;
    }

    public Indexes tags() {
        return _tags;
    }

    public Indexes directories() {
        return _directories;
    }

    // ------------------------------------------------------------------------
    //                      i n i t
    // ------------------------------------------------------------------------

    private void init() {
        // check all archive and regenerates tags and other system metadata
        this.rebuildIndexesAsync();
    }

    private void prepare(final ArchiveFile file_archive,
                         final Delegates.SingleResultCallback<PreparedArchive> callback) {
        try {
            final PreparedArchive files = new PreparedArchive(file_archive);
            Delegates.invoke(callback, null, files);
        } catch (Throwable t) {
            Delegates.invoke(callback, t, null);
            super.error("prepare", t);
        }
    }

    private void copy(final ArchiveFile file,
                      final boolean move) {
        if (!_closed) {
            try {
                final String path_source = file.pathOrigin();
                final String path_target = file.pathArchive();
                if (file.isDirectory()) {
                    //-- error cannot copy a directory --//
                    super.logger().error(FormatUtils.format("Cannot copy a directory, only files. Directory to copy: %s", file.pathOrigin()));
                } else {
                    FileUtils.mkdirs(file.pathArchive());
                    if (move) {
                        // remove original
                        Files.move(Paths.get(path_source), Paths.get(path_target), StandardCopyOption.REPLACE_EXISTING);
                        //Files.copy(Paths.get(path_source), Paths.get(path_target), StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        Files.copy(Paths.get(path_source), Paths.get(path_target), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (Throwable t) {
                super.error("copy", t);
            }
        }
    }

    private void dbFileRemove(final String id) {
        synchronized (_db) {
            _db.collection(COLLECTION_FILES).removeOne(MapBuilder.createSO().put(ID, id).toMap());
        }
    }

    private boolean dbFileUpsert(final ArchiveFile file) {
        synchronized (_db) {
            try {
                if (!file.isDirectory()) {
                    if (!_db.closed() && !_closed) {
                        _db.collection(COLLECTION_FILES).upsert(file.map());
                        addIndex(file);
                        return true;
                    } else {
                        super.warning("dbFileUpsert", "File not indexed because application was shutting down: " + file.name());
                    }
                } else {
                    super.warning("dbFileUpsert", "Trying to archive entire directory: " + file.name());
                }
            } catch (Throwable t) {
                super.error("dbFileUpsert", "Error '" + t.toString() + "'. File not indexed:" + file.name());
            }
            return false;
        }
    }

    private boolean dbFileExists(final ArchiveFile file) {
        synchronized (_db) {
            try {
                if (!file.isDirectory()) {
                    if (!_db.closed() && !_closed) {
                        return _db.collection(COLLECTION_FILES).exists(file.id());
                    } else {
                        return false;
                    }
                    // save tags
                } else {
                    super.warning("dbFileExists", "Trying check id for entire directory: " + file.name());
                }
            } catch (Throwable t) {
                super.error("dbFileExists", "Error '" + t.toString() + "'. File not indexed:" + file.name());
            }
            return false;
        }
    }

    private long dbCount() {
        synchronized (_db) {
            if (!_db.closed() && !_closed) {
                try {
                    return _db.collection(COLLECTION_FILES).count();
                } catch (Throwable t) {
                    super.error("dbCount", t);
                }
            }
            return 0;
        }
    }


    private void rebuildIndexesAsync() {
        Async.invoke((args) -> {
            this.rebuildIndexes();
        });
    }

    private void rebuildIndexes() {
        synchronized (_db) {
            if (!_closed) {
                final long start_ms = System.currentTimeMillis();
                // clear indexes
                this.clearIndexes();

                final Collection<Map<String, Object>> list = _db.collection(COLLECTION_FILES).find();
                int count = 0;
                for (final Object raw_item : list) {
                    if (_closed) {
                        break;
                    }

                    if (raw_item instanceof Map) {
                        count++;
                        final ArchiveFile file = ArchiveFile.create((Map) raw_item);
                        addIndex(file);
                    } else {
                        super.warning("rebuildIndexes", FormatUtils.format("Found Malformed Index at: %s", count));
                    }
                }
                final long end_ms = System.currentTimeMillis();
                // finish load indexes
                super.logger().info(FormatUtils.format("Reloaded indexes in ms: %s", end_ms - start_ms));
            }
        }
    }

    private void clearIndexes() {
        synchronized (this) {
            _categories.clear();
            _tags.clear();
            _directories.clear();
        }
    }

    private void addIndex(final ArchiveFile file) {
        synchronized (this) {
            final Set<String> tags = file.tag();
            for (final String item : tags) {
                _tags.add(item, file.id());
            }
            final Set<String> categories = file.category();
            for (final String item : categories) {
                _categories.add(item, file.id());
            }
            _directories.add(file.directory(), file.id());
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ArchiveController __instance;

    public static ArchiveController instance() {
        if (null == __instance) {
            __instance = new ArchiveController();
        }
        return __instance;
    }

    // ------------------------------------------------------------------------
    //                      e m b e d d e d
    // ------------------------------------------------------------------------

    private static class PreparedArchive
            extends AbstractLogEmitter {

        private ArchiveFile _file;
        private List<ArchiveFile> _resources;

        public PreparedArchive(final ArchiveFile file_archive) {
            _file = file_archive;
            _resources = new LinkedList<>();
            this.init();
        }

        public ArchiveFile file() {
            return _file;
        }

        public List<ArchiveFile> resources() {
            return _resources;
        }

        private void init() {
            final String root = PathUtils.combine(ARCHIVE_FILES, PathUtils.getDateTimePath(3));
            final String path_archive = PathUtils.combine(root, _file.name());
            _file.pathArchive(path_archive);
            if (_file.isDirectory()) {
                // list all files in directory
                final List<File> list = new ArrayList<>();
                FileUtils.listFiles(list, new File(_file.pathOrigin()));
                for (final File item : list) {
                    if (ArchiveFile.isValid(item)) {
                        try {
                            final ArchiveFile archive = ArchiveFile.create(item, false);
                            final String target = PathUtils.concat(root, archive.pathLogic());
                            archive.pathArchive(target);
                            _resources.add(archive);
                        } catch (Throwable t) {
                            super.error("init", t);
                        }
                    }
                }
            } else {
                _resources.add(_file);
            }
        }
    }

}
