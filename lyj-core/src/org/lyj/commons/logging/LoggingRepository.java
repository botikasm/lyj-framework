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

/*
 *
 */
package org.lyj.commons.logging;

import org.lyj.IConstants;
import org.lyj.commons.event.Event;
import org.lyj.commons.event.EventEmitter;
import org.lyj.commons.util.PathUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an utility class for logging storage.<br/>
 * Properties:<br/>
 * <ul>
 * <li>FileEnabled: (Boolean - default=true) Enable/Disable writing on file (./logs/logging.log)</li><br/>
 * <li>MaxSize: (Integer - default=50Mb) Maximum file size.</li><br/>
 * <li>ArchiveExisting: (Boolean - default=true) Backup and archive existing logs.</li><br/>
 * </ul>
 *
 * @author angelo.geminiani
 */
public final class LoggingRepository
        extends EventEmitter {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------

    public static final String ON_LOG = "on_log";

    private static final String DEFAULT = "default";

    // ------------------------------------------------------------------------
    //                      Variables
    // ------------------------------------------------------------------------
    private Level _level = Level.INFO;
    private final Map<String, String> _custom_paths;
    private String _root;
    private boolean _file_enabled;
    private boolean _console_enabled;
    private boolean _enable_events;
    private long _max_size;
    private boolean _archive_existing;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public LoggingRepository() {
        _custom_paths = Collections.synchronizedMap(new HashMap<String, String>());
        _file_enabled = true;
        _console_enabled = true;
        _enable_events = false;
        _max_size = 1024 * 1000 * 50; // 50Mb
        _archive_existing = true;

        this.setFilePath(IConstants.PATH_LOG + "/logging.log");
    }

    @Override
    public String toString() {
        synchronized (_custom_paths) {
            return _custom_paths.toString();
        }
    }

    // ------------------------------------------------------------------------
    //                      public
    // ------------------------------------------------------------------------

    public Map<String, String> pathMap() {
        synchronized (_custom_paths) {
            final Map<String, String> response = new HashMap<>(_custom_paths);
            return response;
        }
    }

    /**
     * Set default file path
     *
     * @param path
     */
    public void setFilePath(final String path) {
        _root = PathUtils.getParent(path);
        this.setLogFileName(DEFAULT, path);
    }

    /**
     * Set a custom log file.
     *
     * @param aclass   Class of instance that logs in a separate file.
     * @param fileName File Name. i.e. "./memory.log"
     */
    public void setLogFileName(final Class aclass, final String fileName) {
        final String key = this.getKey(aclass);
        this.setLogFileName(key, fileName);
    }

    /**
     * Set a custom log file.
     *
     * @param key      Name of logger. Usually is name of calling class.
     * @param fileName File Name. i.e. "./memory.log"
     */
    public void setLogFileName(final String key, final String fileName) {
        final String name = PathUtils.getFilename(fileName, true);
        final String absoluteFileName = PathUtils.join(_root, name);
        this.setAbsoluteLogFileName(key, absoluteFileName);
    }

    public void setAbsoluteLogFileName(final String key, final String absoluteFileName) {
        synchronized (_custom_paths) {
            _custom_paths.put(key, absoluteFileName);
        }
    }

    public String getAbsoluteLogFileName(final String name) {
        final String path = this.getCleanLogFileName(name);
        return PathUtils.getAbsolutePath(path);
    }

    public void setLevel(final Level level) {
        _level = level;
    }

    public Level getLevel() {
        return _level;
    }

    public void setEnableEvents(final boolean value) {
        _enable_events = value;
    }

    public boolean isEnableEvents() {
        return _enable_events;
    }

    public boolean isFileEnabled() {
        return _file_enabled;
    }

    public void setFileEnabled(boolean fileEnabled) {
        this._file_enabled = fileEnabled;
    }

    public boolean isConsoleEnabled() {
        return _console_enabled;
    }

    public void setConsoleEnabled(boolean consoleEnabled) {
        this._console_enabled = consoleEnabled;
    }

    public long getMaxSize() {
        return _max_size;
    }

    public void setMaxSize(final long value) {
        _max_size = value;
    }

    public boolean isArchiveExisting() {
        return _archive_existing;
    }

    public void setArchiveExisting(final boolean value) {
        _archive_existing = value;
    }

    public boolean isLoggable(final Level level) {
        return _level.getNumValue() <= level.getNumValue();
    }

    public void log(final Logger logger, final Throwable t) {
        this.log(logger, Level.SEVERE, t, null);
    }

    public void log(final Logger logger, final String message) {
        this.log(logger, Level.INFO, null, message);
    }

    public void log(final Logger logger, final Level level, final String message) {
        this.log(logger, level, null, message);
    }

    public void log(final Logger logger, final Level level, final Throwable t) {
        this.log(logger, level, t, null);
    }

    public void log(final Logger logger, final Level level,
                    final Throwable t, final String message) {
        if (this.isLoggable(level)) {
            final String logger_name = logger.getName();
            final LogItem item = new LogItem(logger_name, level, t, message);

            //this.incCounter(logger_name);

            // this.invokeListeners(item);
            if (_enable_events) {
                this.emitLogEvent(logger, item);
            }

            this.writeFile(logger, item);
            this.writeConsole(item);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private String getKey(final Class aclass) {
        if (null != aclass) {
            final Logger logger = new LogItemRepositoryLogger(aclass.getName());
            return this.getKey(logger);
        }
        return DEFAULT;
    }

    private String getKey(final Logger logger) {
        if (null != logger) {
            return logger.getName();
        }
        return DEFAULT;
    }

    private String getRelativeLogFileName(final Class aclass) {
        final String key = this.getKey(aclass);
        return this.getCleanLogFileName(key);
    }

    private String getCleanLogFileName(final String name) {
        if (_custom_paths.containsKey(name)) {
            final String path = _custom_paths.get(name);
            return path; //path.startsWith(".")?path.substring(1):path;
        }
        return _custom_paths.get(DEFAULT);
    }

    private synchronized void writeFile(final Logger sender, final LogItem item) {
        if (_file_enabled) {
            final String file_name = this.getAbsoluteLogFileName(sender.getName());
            try {
                LogFileWriter.instance().write(_max_size, _archive_existing, file_name, item);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void writeConsole(final LogItem item) {
        if (_console_enabled) {
            System.out.println(item);
        }
    }

    private void emitLogEvent(final Logger logger, final LogItem logItem) {
        final Event event = new Event(logger, ON_LOG, logItem);
        super.emit(event);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static LoggingRepository __instance;

    public static synchronized LoggingRepository getInstance() {
        if (null == __instance) {
            __instance = new LoggingRepository();
        }
        return __instance;
    }
}
