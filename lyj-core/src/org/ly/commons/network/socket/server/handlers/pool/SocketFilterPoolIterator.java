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

package org.ly.commons.network.socket.server.handlers.pool;

import org.ly.commons.network.socket.server.handlers.ISocketFilter;

/**
 *
 */
public class SocketFilterPoolIterator {

    private final Class<? extends ISocketFilter>[] _items;

    private int _index;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public SocketFilterPoolIterator(final Class<? extends ISocketFilter>[] items) {
        _items = items;
        _index = -1;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public boolean hasNext() {
        return (_index + 1) < _items.length;
    }

    public ISocketFilter next() {
        if (this.hasNext()) {
            _index++;
            return getInstance(_items[_index]);
        }
        return null;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private ISocketFilter getInstance(Class<? extends ISocketFilter> aclass) {
        try {
            if (null != aclass) {
                return aclass.newInstance();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

}
