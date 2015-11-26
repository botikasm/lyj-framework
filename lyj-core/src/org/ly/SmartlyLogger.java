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

package org.ly;


import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;

/**
 * Logger wrapper that check for "silent" option.
 * <p/>
 * SILENT mode does not log info or warnings, but only errors.
 * Errors are always logged.
 */
public class SmartlyLogger {

    private final boolean _silent;
    private final Object _sender;

    public SmartlyLogger(final boolean silent) {
        _silent = silent;
        _sender = null;
    }

    public SmartlyLogger(final Object sender, final boolean silent) {
        _silent = silent;
        _sender = sender;
    }

    public void debug(final String message) {
        this.debug(null != _sender ? _sender : this, message);
    }

    public void info(final String message) {
        this.info(null != _sender ? _sender : this, message);
    }

    public void warning(final String message) {
        this.warning(null != _sender ? _sender : this, message);
    }

    public void warning(final Throwable error) {
        this.warning(null != _sender ? _sender : this, error);
    }

    public void severe(final String message) {
        this.severe(null != _sender ? _sender : this, message);
    }

    public void severe(final Throwable error) {
        this.severe(null != _sender ? _sender : this, error);
    }

    public void severe(final String message, final Throwable error) {
        this.severe(null != _sender ? _sender : this, message, error);
    }

    public void debug(final Object sender, final String message) {
        if (!_silent) {
            this.getLogger(sender).debug(message);
        }
    }

    public void info(final Object sender, final String message) {
        if (!_silent) {
            this.getLogger(sender).info(message);
        }
    }

    public void warning(final Object sender, final String message) {
        if (!_silent) {
            this.getLogger(sender).warning(message);
        }
    }

    public void warning(final Object sender, final Throwable error) {
        if (!_silent) {
            this.getLogger(sender).log(Level.WARNING, null, error);
        }
    }

    public void severe(final Object sender, final String message) {
        this.getLogger(sender).log(Level.SEVERE, message);
    }

    public void severe(final Object sender, final Throwable error) {
        this.getLogger(sender).log(Level.SEVERE, null, error);
    }

    public void severe(final Object sender, final String message, final Throwable error) {
        this.getLogger(sender).log(Level.SEVERE, message, error);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger(final Object instance) {
        return LoggingUtils.getLogger(instance);
    }


}
