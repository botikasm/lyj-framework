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
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * @author angelo.geminiani
 */
public final class LogItem
        implements Serializable {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _loggerName;
    private int _id;
    private Date _date;
    private Level _level;
    private Throwable _exception;
    private String _message;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LogItem() {
        this(null, Level.INFO, null, null);
    }

    public LogItem(final Level level, final String message) {
        this(null, level, null, message);
    }

    public LogItem(final String message) {
        this(null, Level.INFO, null, message);
    }

    public LogItem(final String loggername,
                   final Level level,
                   final Throwable exception,
                   final String message) {
        _loggerName = loggername;
        _id = nextId(_loggerName);
        _level = level;
        _exception = exception;
        _message = message;
        _date = DateUtils.now();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("[").append(_id).append("] ");
        result.append(_level).append(": ");
        result.append(" (").append(FormatUtils.formatDate(_date, Locale.getDefault(), true)).append(") ");
        if (StringUtils.hasText(_message)) {
            result.append(_message).append(" ");
        }
        if (null != _exception) {
            result.append(IConstants.LINE_SEPARATOR);
            if (StringUtils.hasText(_loggerName)) {
                result.append("\t LOGGER: ");
                result.append(_loggerName);
                result.append(IConstants.LINE_SEPARATOR);
            }
            result.append("\t ERROR: {");
            result.append(_exception.toString());
            result.append("}");
            result.append(IConstants.LINE_SEPARATOR);
            result.append("\t CAUSE: {");
            result.append(ExceptionUtils.getRealMessage(_exception));
            result.append("}");
        }
        return result.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int getId() {
        return _id;
    }

    public Level getLevel() {
        return _level;
    }

    public void setLevel(Level level) {
        this._level = level;
    }

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        this._date = date;
    }

    public Throwable getException() {
        return _exception;
    }

    public void setException(Throwable exception) {
        this._exception = exception;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        this._message = message;
    }

    public boolean enabled() {
        return Level.OFF != _level;
    }

    public Throwable getCause() {
        return ExceptionUtils.getRealCause(_exception);
    }

    public String getCauseMessage() {
        return ExceptionUtils.getRealMessage(_exception);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static int _COUNT = 0;

    private static int nextId(final String logger) {
        return ++_COUNT;
    }

}
