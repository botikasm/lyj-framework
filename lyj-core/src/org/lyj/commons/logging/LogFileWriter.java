package org.lyj.commons.logging;

import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LogFileWriter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Object> _locks;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private LogFileWriter() {
        _locks = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void write(final long max_size,
                      final boolean create_archive,
                      final String file_name,
                      final LogItem item) throws IOException {
        synchronized (getLock(file_name)) {
            // ensure directory exists
            FileUtils.mkdirs(file_name);

            // check id and archive files
            final int id = item.getId();
            if (id == 1) {
                // should archive?
                if (create_archive) {
                    this.archive(file_name);
                } else {
                    FileUtils.delete(file_name);
                }
            } else {
                // rotate if file size exceed the limit
                final long file_size = FileUtils.getSize(file_name);
                if (file_size >= max_size) {
                   this.rotate(file_name);
                }
            }

            // get text to write
            final String text = item.toString();

            // append to fle
            this.append(file_name, text);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Object getLock(final String filename) {
        final String key = MD5.encode(filename);
        if (!_locks.containsKey(key)) {
            _locks.put(key, new Object()); // creates lock
        }
        return _locks.get(key);
    }

    private void append(final String fileName,
                        final String text) {
        FileUtils.append(new File(fileName), text);
    }

    private void archive(final String file_name) throws IOException {
        if (FileUtils.exists(file_name)) {
            final String root = PathUtils.getParent(file_name);
            final String dir = StringUtils.replace(PathUtils.getDateTimePath(6), "/", "_");
            final String path_archive = PathUtils.concat(root, dir);
            FileUtils.mkdirs(path_archive);

            final List<File> files = new LinkedList<>();
            FileUtils.list(files, new File(root), "*.log", "", 0, false);

            if (!files.isEmpty()) {
                for (final File file : files) {
                    FileUtils.copy(file, new File(PathUtils.concat(path_archive, file.getName())));
                    FileUtils.delete(file.getAbsolutePath());
                }
            }
        }
    }

    private void rotate(final String file_name) throws IOException {
        if (FileUtils.exists(file_name)) {
            final String root = PathUtils.getParent(file_name);
            final String name = PathUtils.getFilename(file_name, false);
            final String ext = PathUtils.getFilenameExtension(file_name, true);

            final List<File> files = new LinkedList<>();
            FileUtils.list(files, new File(root), name + "_*.log", "", 0, false);

            // get last file name
            int last = 0;
            for (final File file : files) {
                final String[] tokens = StringUtils.split(PathUtils.getFilename(file.getName(), false), "_");
                final int pos = ConversionUtils.toInteger(CollectionUtils.get(tokens, 1), 0);
                if (pos > last) {
                    last = pos;
                }
            }

            // rotate file saving last with a progressive name
            final String new_name = PathUtils.concat(root, name.concat("_").concat((last+1) + "").concat(ext));
            FileUtils.copy(new File(file_name), new File(new_name));

            // remove current for new content
            FileUtils.delete(file_name);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static LogFileWriter __instance;

    public static synchronized LogFileWriter instance() {
        if (null == __instance) {
            __instance = new LogFileWriter();
        }
        return __instance;
    }

}
