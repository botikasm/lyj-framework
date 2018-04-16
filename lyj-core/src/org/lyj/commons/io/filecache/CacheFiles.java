package org.lyj.commons.io.filecache;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.*;

import java.io.File;
import java.io.FileOutputStream;

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
        super(root, duration, (long) (duration * 0.5));
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
                    final byte[] content) {
        this.put(key, content, super.duration());
    }

    public void put(final String key,
                    final byte[] content,
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

    public void update(final String key, final long duration_ms) {
        if (this.registry().has(key)) {
            this.registry().get(key).duration(duration_ms);
        }
    }

    public String getString(final String key) {
        final byte[] bytes = this.getBytes(key);
        try {
            return bytes.length > 0 ? new String(bytes, 0, bytes.length, _encoding) : null;
        } catch (Throwable ignored) {

        }
        return null;
    }

    public byte[] getBytes(final String key) {
        try {
            if (super.registry().has(key)) {
                final String target = super.registry().get(key).path();
                final File file = new File(target);
                if (file.exists()) {
                    return ByteUtils.getBytes(file);
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

    private void put(final String key,
                     final String target,
                     final byte[] content,
                     final long duration) throws Exception {
        FileUtils.tryMkdirs(target);
        try (final FileOutputStream fos = new FileOutputStream(target)) {
            fos.write(content);
            fos.flush();
        }

        super.registry().addItem(key, target, duration);
        super.registry().save();
    }

}
