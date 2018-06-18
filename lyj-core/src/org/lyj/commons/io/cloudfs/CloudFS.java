package org.lyj.commons.io.cloudfs;

import org.json.JSONObject;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.io.cloudfs.configuration.CloudFSConfig;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage FS access, file storage and disk quota for each company account
 */
public class CloudFS
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "./cloud_fs";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final CloudFSConfig _settings;
    private final List<CloudDisk> _disks;
    private final CloudFSIndex _db;

    private boolean _open;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CloudFS(final CloudFSConfig settings) {
        _disks = new ArrayList<>();
        _db = new CloudFSIndex(PathUtils.getAbsolutePath(ROOT));
        _settings = settings;
        _open = false;

        this.initialize(_settings);
    }

    @Override
    public String toString() {
        final JSONObject sb = new JSONObject();
        try {
            sb.put("disks", _disks.size());
            for (final CloudDisk disk : _disks) {
                final String uid = disk.uid();
                final JSONObject item = new JSONObject();
                item.put("root", disk.root());
                item.put("max_size", disk.maxSize());
                item.put("used_size", disk.usedSize());
                item.put("free_size", disk.freeSize());
                sb.put(uid, item);
            }
        } catch (Throwable ignored) {

        }


        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public synchronized CloudFS open() {
        if (!_open) {
            _open = true;

        }
        return this;
    }

    public synchronized CloudFS close() {
        if (_open) {
            _open = false;
        }
        return this;
    }

    public boolean canStore(final File file) {
        synchronized (_disks) {
            try {
                if (!_disks.isEmpty()) {
                    for (final CloudDisk disk : _disks) {
                        if (disk.canStore(file)) {
                            return true;
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
            return false;
        }
    }

    public String move(final File file) throws IOException {
        synchronized (_disks) {
            if (!_disks.isEmpty()) {
                for (final CloudDisk disk : _disks) {
                    if (disk.canStore(file)) {
                        return disk.move(file);
                    }
                }
            }
            return "";
        }
    }

    public String copy(final File file) throws IOException {
        synchronized (_disks) {
            if (!_disks.isEmpty()) {
                for (final CloudDisk disk : _disks) {
                    if (disk.canStore(file)) {
                        return disk.copy(file);
                    }
                }
            }
            return "";
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initialize(final CloudFSConfig settings) {
        if (null != settings) {
            // clear disks and init internal array
            _disks.clear();

            final CloudFSConfig.Disk[] disks = settings.disks();
            for (final CloudFSConfig.Disk disk : disks) {
                // validate folders
                final String path = PathUtils.isAbsolute(disk.name())
                        ? disk.name()
                        : PathUtils.getAbsolutePath(PathUtils.merge(ROOT, disk.name()));
                FileUtils.tryMkdirs(path);

                _disks.add(new CloudDisk(_db, path, disk.sizeMb() * 1024L * 1024L));
            }
        } else {
            super.error("initialize", "MISSING CONFIGURATION!");
        }
    }


    // ------------------------------------------------------------------------
    //                      F A C T O R Y
    // ------------------------------------------------------------------------

    private static CloudFSConfig getDefaultSettings() {
        final String text = ClassLoaderUtils.getResourceAsString(null,
                CloudFS.class,
                "configuration/configuration.json");
        return StringUtils.isJSON(text) ? new CloudFSConfig(text) : null;
    }

    public static CloudFS open(final CloudFSConfig settings) {
        return new CloudFS(settings).open();
    }

    public static CloudFS openTest() {
        return new CloudFS(getDefaultSettings()).open();
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    private static class CloudDisk {

        private final CloudFSIndex _index;
        private final String _uid;
        private final String _root;
        private final long _max_size_bytes;

        private CloudFSIndex.Entity _entity;
        private long _used_size_bytes;

        public CloudDisk(final CloudFSIndex index,
                         final String root,
                         final long max_size_bytes) {
            _index = index;
            _root = root;
            _max_size_bytes = max_size_bytes;
            _uid = MD5.encode(_root);

            _entity = _index.get(_uid);
            if (null == _entity) {
                _entity = new CloudFSIndex.Entity();
                _entity.key(_uid);
                _entity.root(_root);
                _entity.maxSize(_max_size_bytes);
                _entity.usedSize(0);

                this.upsert();
            }

            _used_size_bytes = _entity.usedSize();
        }

        public String uid() {
            return _uid;
        }

        public String root() {
            return _root;
        }

        public long maxSize() {
            return _max_size_bytes;
        }

        public long usedSize() {
            return _used_size_bytes;
        }

        public long freeSize() {
            return _max_size_bytes - _used_size_bytes;
        }

        public boolean canStore(final String file_name) {
            try {
                return this.canStore(new File(file_name));
            } catch (Throwable ignored) {
            }
            return false;
        }

        public boolean canStore(final File file) {
            try {
                return this.canStore(file.length());
            } catch (Throwable ignored) {
            }
            return false;
        }

        public boolean canStore(final long size) {
            return (_max_size_bytes - (_used_size_bytes + size)) > 0;
        }

        public String copy(final File file) throws IOException {
            return this.store(file, false);
        }

        public String move(final File file) throws IOException {
            return this.store(file, true);
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private String store(final File file,
                             final boolean move) throws IOException {
            if (this.canStore(file)) {
                final String date_path = PathUtils.getDateTimePath(5);
                final String parent_path = PathUtils.concat(_root, date_path);
                final String file_ext = PathUtils.getFilenameExtension(file.getAbsolutePath(), true);
                final String file_name = PathUtils.getFilename(file.getAbsolutePath(), false);

                // get response file name
                final String response = PathUtils.concat(parent_path,
                        file_name.concat("_" + RandomUtils.randomNumeric(6)).concat(file_ext));

                FileUtils.tryMkdirs(response);
                final long file_size = file.length();
                if (file_size > 0) {
                    copy(file, new File(response), move);

                    _used_size_bytes += file_size; // UPDATE SIZE USAGE

                    if (null != _entity) {
                        _entity.usedSize(_used_size_bytes);
                        this.upsert();
                    }

                    return response;
                }

            }
            return "";
        }

        private void upsert() {
            try {
                _index.upsert(_entity);
            } catch (Throwable ignored) {

            }
        }

        private static void copy(final File in, final File out, final boolean move) throws IOException {
            FileUtils.copy(in, out);
            if (move) {
                FileUtils.delete(in);
            }
        }

    }

}
