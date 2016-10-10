
package org.lyj.commons.io.filecache;

import org.lyj.commons.io.filecache.registry.Registry;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.IOException;

/**
 * Repository with expiration time for its content.
 * Expired files are removed
 */
public abstract class AbstractCacheFiles
        extends AbstractLogEmitter {

    private static final String REGISTRY_LOG = "_registry.log";
    private static final String REGISTRY_SETTINGS = "_registry_settings.json";
    private static final String REGISTRY_DATA = "_registry_data.json";

    private static final long ONE_MINUTE = DateUtils.ONE_MINUTE_MS;

    private final String _root;
    private final String _path_data;
    private final String _path_settings;
    private final String _path_log;
    private final Registry _registry;

    private long _duration;
    private long _check_interval;
    private boolean _debugMode;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractCacheFiles(final String root) {
        this(root, ONE_MINUTE, ONE_MINUTE);
    }

    public AbstractCacheFiles(final String root,
                              final long duration,
                              final long check_interval) {

        _root = PathUtils.getAbsolutePath(root);
        _path_data = PathUtils.concat(_root, REGISTRY_DATA);
        _path_settings = PathUtils.concat(_root, REGISTRY_SETTINGS);
        _path_log = PathUtils.concat(_root, REGISTRY_LOG);

        LoggingRepository.getInstance().setAbsoluteLogFileName(this.getClass().getName(), _path_log);

        _debugMode = false;

        _duration = duration;
        _check_interval = check_interval;

        //-- ensure temp dir exists --//
        FileUtils.tryMkdirs(_root);

        //-- load file registry (creates if any) --//
        _registry = new Registry(_path_settings, _path_data);
        _registry.setCheck(_check_interval);
        _registry.trySave();

        this.open();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public boolean debugMode() {
        return _debugMode;
    }

    public AbstractCacheFiles debugMode(final boolean value) {
        _debugMode = value;
        return this;
    }

    public AbstractCacheFiles duration(final long value) {
        _duration = value;
        return this;
    }

    public long duration() {
        return _duration;
    }

    public long checkIntervall() {
        return _check_interval;
    }

    public AbstractCacheFiles checkIntervall(final long value) {
        _check_interval = value;

        _registry.interrupt();
        _registry.setCheck(_check_interval);
        _registry.start();

        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        this.startThreads();
    }

    public void close() {
        this.stopThreads();
        try {
            _registry.save();
        } catch (Throwable ignored) {
        }
    }

    /**
     * Created only for test purpose.
     */
    public void join() throws InterruptedException {
        _registry.join();
    }

    public void clear() {
        //-- stop threads--//
        this.close();

        //-- clear root --//
        try {
            FileUtils.delete(_root);
            FileUtils.mkdirs(_root);
        } catch (Throwable ignored) {
        }

        //-- reset registry--//
        try {
            _registry.clear();
            _registry.save();
        } catch (Throwable ignored) {
        }

        //-- start --//
        this.open();
    }


    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected String pathData() {
        return _path_data;
    }

    protected String pathSettings() {
        return _path_settings;
    }

    protected String pathLog() {
        return _path_log;
    }

    protected void debug(final String message) {
        if (_debugMode) {
            this.logger().log(Level.INFO, message);
        }
    }

    protected Registry registry() {
        return _registry;
    }

    protected void registryAddItem(final String path, final long duration) throws IOException {
        if (_registry.addItem(path, duration)) {
            _registry.save();
        }
    }

    protected void registryRemoveItem(final String path) throws IOException {
        if (_registry.removeItem(path)) {
            _registry.save();
        }
    }

    protected void registryReloadSettings() {
        _registry.reloadSettings();
    }

    protected void registryClear() {
        _registry.clear();
    }

    protected void registrySave() throws IOException {
        _registry.save();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    private void startThreads() {
        try {
            _registry.start();
            this.debug("Started Registry");
        } catch (Throwable t) {
            this.logger().log(Level.SEVERE, null, t);
        }
    }

    private void stopThreads() {
        try {
            _registry.interrupt();
        } catch (Throwable ignored) {
        }
    }


}