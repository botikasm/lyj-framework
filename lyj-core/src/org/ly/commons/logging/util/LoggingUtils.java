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
 * LoggingUtils.java
 *
 */
package org.ly.commons.logging.util;

import org.ly.commons.logging.Level;
import org.ly.commons.logging.LogItemRepositoryLogger;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.LoggingRepository;

/**
 * @author
 */
public abstract class LoggingUtils {

    private static final String LOGGER_NAME = "BASE LOGGER";

    public static Level getLevel() {
        return LoggingRepository.getInstance().getLevel();
    }

    public static void setLevel(final Level level) {
        LoggingRepository.getInstance().setLevel(level);
    }

    public static Logger getLogger(Class cls) {
        return getLogger(cls.getName());
    }

    public static Logger getLogger(Object instance) {
        return getLogger(instance.getClass().getName());
    }

    public static Logger getLogger() {
        final LogItemRepositoryLogger result = new LogItemRepositoryLogger(LOGGER_NAME, "");
        return result;
    }

    public static Logger getLogger(String name) {
        final LogItemRepositoryLogger result = new LogItemRepositoryLogger(name, "");
        return result;
    }
}
