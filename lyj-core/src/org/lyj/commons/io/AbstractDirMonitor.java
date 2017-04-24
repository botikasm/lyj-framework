package org.lyj.commons.io;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.ZipUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Zip and Directories raise an event for each file.
 */
public class AbstractDirMonitor
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int RETRY_IDLE = 1000;
    private static final int RETRY_COUNT = 20;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root_monitor;

    private FileObserver _watchdog;
    private Delegates.CallbackThrowable<File> _callback; // invoked for each single file (zip are deflated)

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractDirMonitor(final String path) {
        _root_monitor = PathUtils.getAbsolutePath(path);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open(final Delegates.CallbackThrowable<File> callback) {
        _callback = callback;

        // creates paths
        FileUtils.tryMkdirs(_root_monitor);

        // start task
        this.initWatchdog();
    }

    public void close() {
        if (null != _watchdog) {
            _watchdog.stopWatching();
        }
    }

    public String path(final String relative) {
        return PathUtils.concat(_root_monitor, relative);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void delete(final String path) {
        try {
            FileUtils.delete(path);
        } catch (Throwable t) {
            super.error("delete", t);
        }
    }

    private boolean isZip(final File file) {
        final String ext = PathUtils.getFilenameExtensionNotNull(file.getAbsolutePath(), false);
        return "zip".equalsIgnoreCase(ext);
    }

    private void initWatchdog() {
        try {
            this.close();

            _watchdog = new FileObserver(_root_monitor, (event, path) -> {
                try {
                    if (event == FileObserver.EVENT_CREATE) {
                        // new file to install?
                        final File file = new File(path);
                        this.handle(file, true);
                    }
                } catch (Throwable t) {
                    // problem with this file
                    super.error("initWatchdog#event", t);
                }
            });

            _watchdog.startWatching();

            super.info("initWatchdog",
                    this.getClass().getSimpleName() + " is monitoring folder: " + _root_monitor);
        } catch (Throwable t) {
            super.error("initWatchdog", t);
        }
    }

    private void handle(final File file,
                        final boolean remove) {
        this.tryHandle(file, null, (error) -> {
            // log error if any
            if (null != error) {
                error("handle", error);
            }

            // should remove file?
            if (remove) {
                AbstractDirMonitor.deleteFileAsync(file);
            }
        });
    }

    private void tryHandle(final File file,
                           final Counter counter,
                           final Delegates.Callback<Exception> error_handler) {
        final Counter count = null != counter ? counter : new Counter();
        try {
            this.handleTypes(file);
            // exit without error
            Delegates.invoke(error_handler, null);
        } catch (Exception ex) {
            count.inc();
            if (count.value() > RETRY_COUNT) {
                // exit with error
                Delegates.invoke(error_handler, ex);
            } else {
                // try again
                Async.delay((args) -> {
                    tryHandle(file, counter, error_handler);
                }, RETRY_IDLE);
            }
        }
    }

    private void handleTypes(final File file) throws Exception {
        if (file.isDirectory()) {
            this.handleDir(file);
        } else if (this.isZip(file)) {
            this.handleZip(file);
        } else {
            this.handleFile(file);
        }
    }

    private void handleZip(final File archive) throws Exception {
        final String name = PathUtils.getFilename(archive.getName(), false);
        final String tmp_path = PathUtils.getTemporaryDirectory(name);
        try {
            // unzip
            ZipUtils.unzip(archive.getAbsolutePath(), tmp_path);

            // install
            this.handleDir(new File(tmp_path));
        } catch (Exception t) {
            super.error("installZip",
                    FormatUtils.format("Error installing '%s': '%s'", archive.getName(), t));
            throw t;
        } finally {
            // remove temp
            this.delete(tmp_path);
        }
    }

    private void handleDir(final File dir) throws Exception {
        final List<File> files = new ArrayList<>();
        FileUtils.list(files, dir, "*.*", null, -1, true);
        for (final File file : files) {
            this.handleTypes(file);
        }
    }

    private void handleFile(final File file) throws Exception {
        if (null != _callback) {
            _callback.handle(file);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static void deleteFileAsync(final File file) {
        Async.delay((args) -> {
            AbstractDirMonitor.deleteFile(file);
        }, 1000);
    }

    private static void deleteFile(final File file) {
        try {
            FileUtils.delete(file.getPath());
        } catch (Throwable t) {
            file.delete();
        }
    }
}
