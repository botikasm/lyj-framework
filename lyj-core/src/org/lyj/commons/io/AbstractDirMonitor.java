package org.lyj.commons.io;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.async.future.Timed;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.ZipUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    private static final int DEFAULT_LOOP_INTERVAL = 15000;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root_monitor;
    private final Set<String> _working_files;

    private FileObserver _watchdog;
    private Timed _timed_task;
    private Delegates.CallbackThrowable<File> _callback; // invoked for each single file (zip are deflated)

    // properties
    private long _task_interval = DEFAULT_LOOP_INTERVAL;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractDirMonitor(final String path) {
        this(path, 0);
    }

    public AbstractDirMonitor(final String path,
                              final long task_interval) {
        _root_monitor = PathUtils.getAbsolutePath(path);
        _working_files = new HashSet<>();
        _task_interval = task_interval;
    }


    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public boolean useTask() {
        return _task_interval > 0;
    }

    public long taskInterval() {
        return _task_interval;
    }

    public AbstractDirMonitor taskInterval(final long value) {
        _task_interval = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open(final Delegates.CallbackThrowable<File> callback) {
        _callback = callback;

        // creates paths
        FileUtils.tryMkdirs(_root_monitor);

        if (this.useTask()) {
            // start task
            this.initTask();
        } else {
            // start watchdog
            this.initWatchdog();
        }
    }

    public void close() {
        if (null != _watchdog) {
            _watchdog.stopWatching();
            _watchdog = null;
        }
        if (null != _timed_task) {
            _timed_task.stop(true);
            _timed_task = null;
        }
    }

    public String path(final String relative) {
        return PathUtils.concat(_root_monitor, relative);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initWatchdog() {
        try {
            this.close();

            _watchdog = new FileObserver(_root_monitor, (event, path) -> {
                try {
                    if (event == FileObserver.EVENT_CREATE
                            || event == FileObserver.EVENT_MODIFY) {
                        // new file to install?
                        final File file = new File(path);
                        if (!this.isMonitorRoot(file)) {
                            this.handleAsync(file, true, null);
                        }
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

    private void initTask() {
        try {
            this.close();

            _timed_task = new MonitorTask(this,
                    new File(_root_monitor),
                    _task_interval);

        } catch (Throwable t) {
            super.error("initTask", t);
        }
    }

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

    private boolean isMonitorRoot(final File file) {
        return file.isDirectory() && _root_monitor.equalsIgnoreCase(file.getAbsolutePath());
    }

    private boolean containsFile(final File file) {
        synchronized (_working_files) {
            return _working_files.contains(file.getAbsolutePath());
        }
    }

    private void pushFile(final File file) {
        synchronized (_working_files) {
            _working_files.add(file.getAbsolutePath());
        }
    }

    private void popFile(final File file) {
        synchronized (_working_files) {
            _working_files.remove(file.getAbsolutePath());
        }
    }


    private void handleAsync(final File file,
                             final boolean remove,
                             final Delegates.Callback<Exception> error_handler) {
        if (!containsFile(file)) {
            this.pushFile(file);
            Async.invoke((args) -> {
                this.tryHandle(file, null, (error) -> {
                    try {
                        // log error if any
                        if (null != error) {
                            error("handle", error);
                        }

                        // should remove file?
                        if (remove) {
                            AbstractDirMonitor.deleteFileAsync(file);
                        }

                        this.popFile(file);
                    } finally {
                        Delegates.invoke(error_handler, error);
                    }
                });
            });
        } else {
            Delegates.invoke(error_handler, null);
        }
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

    // ------------------------------------------------------------------------
    //                      EMBEDDED
    // ------------------------------------------------------------------------

    private static class MonitorTask
            extends Timed {

        private AbstractDirMonitor _owner;
        private final File _root;
        private boolean _busy;

        public MonitorTask(final AbstractDirMonitor owner,
                           final File root,
                           final long loop_interval) {
            super(TimeUnit.MILLISECONDS, 0, loop_interval, 0, 0);
            super.setDaemon(true);

            _owner = owner;
            _root = root;

            // start
            super.start(this::handle);
        }

        private void handle(final TaskInterruptor interruptor) {
            if (null != _owner && !_busy) {
                // lock task
                _busy = true;
                try {
                    final List<Exception> errors = this.checkRoot();
                    if (!errors.isEmpty()) {
                        // errors checking files
                        _owner.error("MonitorTask#handle",
                                FormatUtils.format("Found '%s' errors. First error is '%s'", errors.size(), errors.get(0)));
                    }
                } finally {
                    _busy = false;
                }
            }
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private List<Exception> checkRoot() {
            final List<Exception> errors = new LinkedList<>();
            final List<File> files = new LinkedList<>();
            FileUtils.list(files, _root, "*.*",
                    "*.DS_Store", 0, true);

            for (final File file : files) {
                try {
                    this.checkFile(file);
                } catch (Exception e) {
                    errors.add(e);
                }
            }

            return errors;
        }

        private void checkFile(final File file) throws Exception {
            final Task<File> task = new Task<>((t) -> {
                _owner.handleAsync(file, true, (error) -> {
                    if (null != error) {
                        t.fail(error);
                    } else {
                        t.success(file);
                    }
                });
            });
            task.get();
        }

    }

}
