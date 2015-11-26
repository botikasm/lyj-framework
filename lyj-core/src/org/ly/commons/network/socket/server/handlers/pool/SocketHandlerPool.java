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
import org.ly.commons.network.socket.server.handlers.ISocketHandler;
import org.ly.commons.util.CollectionUtils;

import java.util.*;

/**
 * Pool of Socket handlers
 */
public class SocketHandlerPool {

    private final List<Class<? extends ISocketFilter>> _filters;
    private final Map<String, Class<? extends ISocketHandler>> _handlers;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r s
    // --------------------------------------------------------------------

    public SocketHandlerPool() {
        _filters = Collections.synchronizedList(new LinkedList<Class<? extends ISocketFilter>>());
        _handlers = Collections.synchronizedMap(new HashMap<String, Class<? extends ISocketHandler>>());
    }

    public SocketHandlerPool(final Class<? extends ISocketFilter>[] handlers) {
        this();
        CollectionUtils.addAllNoDuplicates(_filters, handlers);
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void clear() {
        synchronized (_filters) {
            _filters.clear();
        }
        synchronized (_handlers) {
            _handlers.clear();
        }
    }

    // --------------------------------------------------------------------
    //               FILTERS
    // --------------------------------------------------------------------

    public int sizeFilters() {
        return _filters.size();
    }


    public boolean hasFilters() {
        return !_filters.isEmpty();
    }


    public boolean hasFiler(final Class<ISocketFilter> o) {
        return _filters.contains(o);
    }

    public SocketFilterPoolIterator getFiltersIterator() {
        return new SocketFilterPoolIterator(this.getFilters());
    }

    public boolean addFilter(final Class<? extends ISocketFilter> o) {
        if (null != o) {
            synchronized (_filters) {
                return _filters.add(o);
            }
        }
        return false;
    }

    public void addFilters(final Class<? extends ISocketFilter>[] array) {
        if (null != array) {
            synchronized (_filters) {
                CollectionUtils.addAllNoDuplicates(_filters, array);
            }
        }
    }

    public boolean removeFilter(final Class<? extends ISocketFilter> o) {
        if (null != o) {
            synchronized (_filters) {
                return _filters.remove(o);
            }
        }
        return false;
    }

    // --------------------------------------------------------------------
    //               HANDLERS
    // --------------------------------------------------------------------

    public int sizeHandlers() {
        return _handlers.size();
    }

    public boolean hasHandlers() {
        return !_handlers.isEmpty();
    }

    public boolean hasHandler(final String key) {
        return _handlers.containsKey(key);
    }

    public void addHandler(final String key, final Class<? extends ISocketHandler> o) {
        if (null != o) {
            synchronized (_handlers) {
                _handlers.put(key, o);
            }
        }
    }

    public Class<? extends ISocketHandler> removeHandler(final String key) {
        if (null != key) {
            synchronized (_handlers) {
                return _handlers.remove(key);
            }
        }
        return null;
    }

    public ISocketHandler getHandler(final String key) {
        synchronized (_handlers) {
            return this.getHandlerInstance(_handlers.get(key));
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Class<? extends ISocketFilter>[] getFilters() {
        synchronized (_filters) {
            return _filters.toArray(new Class[_filters.size()]);
        }
    }

    private ISocketHandler getHandlerInstance(Class<? extends ISocketHandler> aclass) {
        try {
            if (null != aclass) {
                return aclass.newInstance();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
