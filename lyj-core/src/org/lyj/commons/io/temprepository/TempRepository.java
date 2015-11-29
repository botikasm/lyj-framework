/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lyj.commons.io.temprepository;

import org.lyj.commons.io.FileObserver;
import org.lyj.commons.io.IFileObserverListener;
import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.IOException;

/**
 * Repository with expiration time for its content.
 * Expired files are removed
 */
public class TempRepository
        implements IFileObserverListener {

    private static final String REGISTRY_LOG = "_registry.log";
    private static final String REGISTRY_SETTINGS = "_registry_settings.json";
    private static final String REGISTRY_DATA = "_registry_data.json";

    private static final String LOGGER_PREFIX = "[" + TempRepository.class.getName() + "] ";

    private static final int MAX_ERRORS = 10;

    private final String _root;
    private final String _path_data;
    private final String _path_settings;
    private final String _path_log;
    private final Registry _registry;
    private FileObserver _dirObserver;
    private int _countErrors;
    private boolean _debugMode;

    public TempRepository(final String root) throws IOException {
        _root = root;
        _path_data = PathUtils.concat(_root, REGISTRY_DATA);
        _path_settings = PathUtils.concat(_root, REGISTRY_SETTINGS);
        _path_log = PathUtils.concat(_root, REGISTRY_LOG);

        LoggingRepository.getInstance().setAbsoluteLogFileName(REGISTRY_LOG, _path_log);

        //-- ensure temp dir exists --//
        FileUtils.mkdirs(root);

        //-- load file registry (creates if any) --//
        _registry = new Registry(_path_settings, _path_data);
        _registry.save();

        _countErrors = 0;
        _debugMode = false;

        this.startThreads();
    }

    @Override
    protected void finalize() throws Throwable {
        this.interrupt();
        super.finalize();
    }

    @Override
    public void onEvent(int event, final String path) {
        this.handle(event, path);
    }

    public String getRoot() {
        return _root;
    }

    public boolean isDebugMode() {
        return _debugMode;
    }

    public void setDebugMode(final boolean value) {
        _debugMode = value;
        this.debug("Debug Mode: " + value);
    }

    public void setDuration(final long ms) {
        _registry.interrupt();
        _registry.setLife(ms);
        _registry.start();
    }

    public void setCheckTime(final long ms) {
        _registry.interrupt();
        _registry.setCheck(ms);
        _registry.start();
    }

    public void interrupt() {
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
        this.stopThreads();

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
        this.startThreads();
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(REGISTRY_LOG);
    }

    private void startThreads() {
        try {
            this.startDirObserver();
            this.debug("Started Path Observer");
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        try {
            _registry.start();
            this.debug("Started Registry");
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    private void stopThreads() {
        try {
            _dirObserver.interrupt();
            _dirObserver = null;
        } catch (Throwable ignored) {
        }

        try {
            _registry.interrupt();
        } catch (Throwable ignored) {
        }
    }

    private void startDirObserver() throws IOException {
        //-- file observer initialization --//
        if (null != _dirObserver) {
            _dirObserver.interrupt();
            _dirObserver = null;
        }
        _dirObserver = new FileObserver(_root, true, false, FileObserver.ALL_EVENTS, this);
        _dirObserver.startWatching();
    }

    private void handle(final int event, final String path) {
        if (!_path_log.equalsIgnoreCase(path)) {
            String sevent = "UNDEFINED";
            try {
                if (event == FileObserver.EVENT_CREATE) {
                    sevent = "CREATE";
                    // CREATE
                    if (!_path_data.equalsIgnoreCase(path)
                            && !_path_settings.equalsIgnoreCase(path)) {
                        if (_registry.addItem(path)) {
                            _registry.save();
                            this.debug(FormatUtils.format("Action '{0}' on '{1}'", sevent, path));
                        }
                    }
                } else if (event == FileObserver.EVENT_MODIFY) {
                    sevent = "MODIFY";
                    // MODIFY
                    if (_path_settings.equalsIgnoreCase(path)) {
                        _registry.reloadSettings();
                        this.debug("Changed Settings: reload all settings from file.");
                    }
                } else if (event == FileObserver.EVENT_DELETE) {
                    sevent = "DELETE";
                    if (!_path_data.equalsIgnoreCase(path)
                            && !_path_settings.equalsIgnoreCase(path)) {
                        if (_registry.removeItem(path)) {
                            _registry.save();
                            this.debug(FormatUtils.format("Action '{0}' on '{1}'", sevent, path));
                        }
                    } else {
                        _registry.clear();
                        _registry.save();
                        this.debug("Removed DATA file: reset of registry.");
                    }
                }
            } catch (final Throwable t) {
                final String msg = FormatUtils.format("Error on '{0}' path '{1}' to temp repository: {2}",
                        sevent, path, t);
                this.handleError(msg, t);
            }
        }
    }

    public void handleError(final String message,
                            final Throwable t) {
        if (_countErrors < MAX_ERRORS) {
            _countErrors++;
            if (StringUtils.hasText(message)) {
                this.getLogger().log(Level.SEVERE, LOGGER_PREFIX.concat(message), t);
            } else {
                this.getLogger().log(Level.SEVERE,
                        LOGGER_PREFIX.concat(FormatUtils.format("{0}", t)),
                        t);
            }

        } else {
            this.interrupt();
        }
    }

    public void debug(final String message) {
        if (_debugMode) {
            this.getLogger().log(Level.INFO, LOGGER_PREFIX.concat(message));
        }
    }
}
