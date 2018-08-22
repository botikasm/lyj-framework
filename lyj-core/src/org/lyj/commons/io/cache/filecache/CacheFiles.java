package org.lyj.commons.io.cache.filecache;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 *
 */
public class CacheFiles
        extends AbstractCacheFiles {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final int DEFAULT_DETAIL = 3; // detail 0=yyyy/MM/dd, 1=yyyy/, 2=yyyy/MM, 3=yyyy/MM/dd, 4=yyyy/MM/dd/hh, 5=yyyy/MM/dd/hh/mm, 6=yyyy/MM/dd/hh/mm/ss/

    public static final long ONE_MINUTE = DateUtils.ONE_MINUTE_MS;
    public static final long ONE_HOUR = DateUtils.ONE_HOUR_MS;
    public static final long ONE_DAY = DateUtils.ONE_DAY_MS;
    public static final long ONE_YEAR = ONE_DAY * 365;
    public static final long FOREVER = ONE_YEAR * 1000;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _encoding;
    private int _path_detail = DEFAULT_DETAIL;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CacheFiles(final String root,
                      final long duration,
                      final String encoding) {
        super(root, duration, (long) (duration * 0.5), Mode.Memory);
        _encoding = encoding;
    }

    public CacheFiles(final String root) {
        this(root, ONE_MINUTE, CharEncoding.UTF_8);
    }

    public CacheFiles(final String root,
                      final long duration) {
        this(root, duration, CharEncoding.UTF_8);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String encoding() {
        return _encoding;
    }

    public CacheFiles encoding(final String value) {
        _encoding = value;
        return this;
    }

    public int pathDetail() {
        return _path_detail;
    }

    public CacheFiles pathDetail(final int value) {
        _path_detail = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean has(final String key) {
        return super.registry().has(key);
    }

    public void put(final String key,
                    final String content) {
        this.put(key, content, super.duration());
    }

    public void put(final String key,
                    final String content,
                    final long duration) {
        try {
            if (!this.has(key)) {
                final String target = this.path(key.concat(".txt"));

                this.put(key, target, content.getBytes(_encoding), duration);
            } else {
                this.update(key, duration);
            }
        } catch (Throwable t) {
            super.logger().error("put#String", t);
        }
    }

    public void put(final String key,
                    final File file) {
        this.put(key, file, super.duration());
    }

    public void put(final String key,
                    final File file,
                    final long duration) {
        try {
            final byte[] content = ByteUtils.getBytes(file);
            final String target = this.path(key.concat(PathUtils.getFilenameExtension(file.getName(), true)));
            this.putOrUpdate(key, target, content, duration);
        } catch (Throwable t) {
            super.logger().error("put#Bytes", t);
        }
    }

    public void put(final String key,
                    final byte[] content) {
        this.put(key, content, super.duration());
    }

    public void put(final String key,
                    final byte[] content,
                    final long duration) {
        final String target = this.path(key.concat(".dat"));
        this.putOrUpdate(key, target, content, duration);
    }

    public void put(final String key,
                    final InputStream content) {
        this.put(key, content, super.duration());
    }

    public void put(final String key,
                    final InputStream content,
                    final long duration) {
        try {
            if (!this.has(key)) {
                final String target = this.path(key.concat(".dat"));

                this.put(key, target, content, duration);
            } else {
                this.update(key, duration);
            }
        } catch (Throwable t) {
            super.logger().error("put#Bytes", t);
        }
    }

    public void update(final String key,
                       final long duration_ms) {
        if (this.registry().has(key) && duration_ms > 0) {
            this.registry().get(key).duration(duration_ms);
        }
    }

    public boolean remove(final String key) {
        try {
            if (this.registry().has(key)) {
                return this.registry().removeItem(key);
            }
        } catch (Throwable t) {
            super.logger().error("remove", t);
        }
        return false;
    }

    public String getString(final String key) {
        final byte[] bytes = this.getBytes(key);
        try {
            return bytes.length > 0 ? new String(bytes, 0, bytes.length, _encoding) : null;
        } catch (Throwable ignored) {

        }
        return null;
    }

    public File getFile(final String key) {
        try {
            if (super.registry().has(key)) {
                final String target = super.registry().get(key).path();
                final File file = new File(target);
                if (file.exists()) {
                    return file;
                }
            }
        } catch (Throwable ignored) {

        }
        return null;
    }

    public byte[] getBytes(final String key) {
        return this.getBytes(key, false);
    }

    public byte[] getBytes(final String key,
                           final boolean remove_item) {
        byte[] response = new byte[0];
        try {
            if (super.registry().has(key)) {
                final String target = super.registry().get(key).path();
                final File file = new File(target);
                if (file.exists()) {
                    response = ByteUtils.getBytes(file);
                }
                if (remove_item) {
                    super.registryRemoveItem(key);
                }
            }
        } catch (Throwable ignored) {

        }
        return response;
    }

    public byte[] getBytes(final String key, final int skip, final int len) {
        try {
            if (super.registry().has(key)) {
                final String target = super.registry().get(key).path();
                final File file = new File(target);
                if (file.exists()) {
                    return ByteUtils.getBytes(file, skip, len);
                }
            }
        } catch (Throwable ignored) {

        }
        return new byte[0];
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String path(final String rel_path) {
        final String time_path = PathUtils.concat(super.root(), PathUtils.getDateTimePath(_path_detail)); // yyyy/MM/dd
        if (StringUtils.hasText(rel_path)) {
            return PathUtils.concat(time_path, rel_path);
        } else {
            return time_path;
        }
    }

    private void putOrUpdate(final String key,
                             final String target,
                             final byte[] content,
                             final long duration) {
        try {
            if (!this.has(key)) {
                this.put(key, target, content, duration);
            } else {
                this.update(key, duration);
            }
        } catch (Throwable t) {
            super.logger().error("putOrUpdate#Bytes", t);
        }
    }

    private void put(final String key,
                     final String target,
                     final byte[] content,
                     final long duration) throws Exception {
        FileUtils.tryMkdirs(target);
        try (final FileOutputStream fos = new FileOutputStream(target)) {
            fos.write(content);
            fos.flush();
        }

        final long dynamic_duration = duration > 0 ? duration : content.length * 5;
        super.registry().addItem(key, target, dynamic_duration);
        super.registry().save();
    }

    private void put(final String key,
                     final String target,
                     final InputStream content,
                     final long duration) throws Exception {
        FileUtils.tryMkdirs(target);
        final Counter count = new Counter(0);
        try (final FileOutputStream fos = new FileOutputStream(target)) {
            ByteUtils.read(content, 1024, (bytes) -> {
                try {
                    fos.write(bytes);
                    count.inc(bytes.length);
                } catch (Throwable ignored) {
                }
            });
            fos.flush();
        }
        final long dynamic_duration = duration > 0 ? duration : count.value() * 5;
        super.registry().addItem(key, target, dynamic_duration);
        super.registry().save();
    }

}
