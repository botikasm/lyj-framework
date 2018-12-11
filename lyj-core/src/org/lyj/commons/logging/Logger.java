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

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;

/**
 * @author angelo.geminiani
 */
public class Logger {


    private String _name;
    private String _short_name;
    private Level _level = Level.INFO;
    private boolean _use_console;


    public Logger(final String name) {
        this.setName(name);
        this.setUseConsole(true);
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        this._name = name;
        if (StringUtils.hasText(name) && StringUtils.contains(name, ".")) {
            _short_name = CollectionUtils.getLast(name, ".");
        } else {
            _short_name = _name;
        }
    }

    public String getShortName() {
        return _short_name;
    }

    public boolean getUseConsole() {
        return _use_console;
    }

    public void setUseConsole(boolean value) {
        _use_console = value;
    }

    public Level getLevel() {
        return _level;
    }

    public void setLevel(Level level) {
        this._level = level;
    }

    public boolean isLoggable(final Level level) {
        return _level.getNumValue() <= level.getNumValue();
    }

    public boolean isDebugEnabled() {
        return this.isLoggable(Level.FINE);
    }

    public void log(final Level level, final String msg) {
        this.notify(level, msg, null);
    }

    public void log(final Level level, final String msg,
                    final Object param1) {
        this.notify(level, FormatUtils.format(msg, param1), null);
    }

    public void log(final Level level, final String msg,
                    final Object[] params) {
        this.notify(level, FormatUtils.format(msg, params), null);
    }

    public void log(final Level level, final String msg,
                    final Throwable thrown) {
        this.notify(level, msg, thrown);
    }

    public void info(String msg) {
        this.notify(Level.INFO, msg, null);
    }

    public void info(final String msg, final Object... args) {
        this.notify(Level.INFO, FormatUtils.format(msg, args), null);
    }

    public void error(final String msg) {
        this.severe(msg);
    }

    public void error(final String msg, final Throwable t) {
        this.notify(Level.SEVERE, msg, t);
    }

    public void severe(final String msg) {
        this.notify(Level.SEVERE, msg, null);
    }

    public void severe(final String msg, final Object... args) {
        this.notify(Level.SEVERE, FormatUtils.format(msg, args), null);
    }

    public void warning(String msg) {
        this.notify(Level.WARNING, msg, null);
    }

    public void warning(final String msg, final Object... args) {
        this.notify(Level.WARNING, FormatUtils.format(msg, args), null);
    }

    public void debug(String msg) {
        this.fine(msg);
    }

    public void fine(String msg) {
        this.notify(Level.FINE, msg, null);
    }

    public void fine(final String msg, final Object... args) {
        this.notify(Level.FINE, FormatUtils.format(msg, args), null);
    }

    public void finer(String msg) {
        this.notify(Level.FINER, msg, null);
    }

    public void finer(final String msg, final Object... args) {
        this.notify(Level.FINER, FormatUtils.format(msg, args), null);
    }

    public void finest(String msg) {
        this.notify(Level.FINEST, msg, null);
    }

    public void finest(final String msg, final Object... args) {
        this.notify(Level.FINEST, FormatUtils.format(msg, args), null);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------
    protected Object notify(final Level level,
                            final String subject,
                            final Throwable error) {
        LoggingRepository.getInstance().log(this, level, error, subject);
        return this;
    }

}
