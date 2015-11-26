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

package org.ly.commons.logging;

/**
 * @author angelo.geminiani
 */
public enum Level {

    ALL(0),
    FINEST(1),
    FINER(2),
    FINE(3),
    CONFIG(4),
    INFO(5),
    WARNING(6),
    SEVERE(7),
    OFF(100);

    private final Integer _numValue;

    Level(final int intValue) {
        _numValue = intValue;
    }

    public int getNumValue() {
        return _numValue;
    }

    public static Level getLevel(String level) {
        if (level != null) {
            level = level.toUpperCase();
            if (level.equals("DEBUG")) {
                return Level.FINE;
            }
            if (level.equals("ERROR")) {
                return Level.SEVERE;
            }
            if (level.equals("FINE")) {
                return Level.FINE;
            }
            if (level.equals("FINER")) {
                return Level.FINER;
            }
            if (level.equals("FINEST")) {
                return Level.FINEST;
            }
            if (level.equals("INFO")) {
                return Level.INFO;
            }
            if (level.equals("CONFIG")) {
                return Level.CONFIG;
            }
            if (level.equals("WARNING")) {
                return Level.WARNING;
            }
            if (level.equals("OFF")) {
                return Level.OFF;
            }
        }
        return Level.ALL;
    }
}
