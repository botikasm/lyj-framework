package org.lyj.commons.io.packets;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.future.Timed;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled task.
 * Singleton task is a 30 seconds task
 */
public class PacketMonitorTask
        extends Timed {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final TimeUnit UNIT = TimeUnit.SECONDS;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final PacketController _controller;
    private boolean _working;
    private boolean _stop_on_error;
    private String _path_root;  // absolute path to monitor
    private Delegates.CallbackEntry<File, List<File>> _callback;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public PacketMonitorTask(final int interval) {
        super(UNIT, 0, interval, 0, 0);
        _controller = new PacketController();
        _stop_on_error = false;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public boolean isBusy() {
        return _working;
    }

    public String root() {
        return _path_root;
    }

    public PacketMonitorTask root(final String value) throws IOException {
        _path_root = PathUtils.isAbsolute(value) ? value : PathUtils.getAbsolutePath(value);
        if (StringUtils.hasText(_path_root)) {
            FileUtils.mkdirs(_path_root);
        }
        return this;
    }

    public PacketMonitorTask callback(final Delegates.CallbackEntry<File, List<File>> value) {
        _callback = value;
        return this;
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        super.start(this::handle);
    }

    public void close() {
        super.stop(true);
    }

    public String path(final String file_name) {
        return PathUtils.concat(_path_root, file_name);
    }

    public boolean isValidPacket(final File file) {
        return _controller.isValidPacket(file);
    }

    /**
     * Force execution
     */
    public void force(){
        this.handle(null);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handle(final TaskInterruptor interruptor) {
        if (!_working) {
            _working = true;
            try {
                if (StringUtils.hasText(_path_root)
                        && FileUtils.isDir(_path_root)) {
                    // get consistent files
                    final List<File> files = this.getFiles();
                    for (final File packet : files) {
                        if (null != packet) {
                            // process valid packet
                            _controller.process(packet, (packet_files) -> {
                                // returns list of unzipped files
                                Delegates.invoke(_callback,
                                        packet,         // packet file
                                        packet_files    // packet content
                                );
                            });
                        }
                    }
                }
            } catch (Throwable t) {
                super.error("handle", t);
                if (_stop_on_error && null!=interruptor) {
                    interruptor.stop();
                }
            } finally {
                _working = false;
            }
        }
    }

    private List<File> getFiles() {
        final List<File> response = new LinkedList<>();
        try {
            final List<File> files = new LinkedList<>();
            FileUtils.listFiles(files, new File(_path_root), "*.*", "", -1);
            for (final File file : files) {
                if (this.isReady(file)) {
                    response.add(file);
                }
            }
        } catch (Exception error) {
            super.error("getFiles", error);
            throw error;
        }
        return response;
    }

    private boolean isReady(final File file) {
        try {
            return FileUtils.getSize(file) > 0;
        } catch (Throwable ignored) {

        }
        return false;
    }
    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static PacketMonitorTask __instance;

    public static synchronized PacketMonitorTask instance() {
        if (null == __instance) {
            __instance = new PacketMonitorTask(30); // 30 seconds task
        }
        return __instance;
    }

}
