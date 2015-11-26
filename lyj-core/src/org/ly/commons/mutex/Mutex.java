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

package org.ly.commons.mutex;


/**
 *
 */
public class Mutex {

    private final long _timeout;
    private long _lastAccessTime;

    public Mutex(final long timeout) {
        _timeout = timeout;
        _lastAccessTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("id: ").append(this.hashCode());
        result.append(", ");
        result.append("timeout: ").append(_timeout);
        result.append(", ");
        result.append("last_access: ").append(_lastAccessTime);

        return result.toString();
    }

    Mutex wakeUp() {
        _lastAccessTime = System.currentTimeMillis();
        return this;
    }

    boolean isExpired() {
        final long now = System.currentTimeMillis();
        return (now - _lastAccessTime) > _timeout;
    }
}
