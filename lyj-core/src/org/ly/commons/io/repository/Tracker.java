
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

package org.ly.commons.io.repository;

import java.io.IOException;

/**
 * A utility class that allows Resource consumers to track changes
 * on resources.
 */
public class Tracker {

    Trackable source;
    long lastModified;

    public Tracker(Trackable source) throws IOException {
        this.source = source;
        markClean();
    }

    public boolean hasChanged() throws IOException {
        return lastModified != source.lastModified();
    }

    public void markClean() throws IOException {
        lastModified = source.lastModified();
    }

    public Trackable getSource() {
        return source;
    }
}
