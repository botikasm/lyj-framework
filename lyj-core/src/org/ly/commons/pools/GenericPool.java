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

package org.ly.commons.pools;

import org.ly.commons.Delegates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Blocking Pool of objects.<br/>
 * Call method "lock" to get an item from pool.<br/>
 * Call method "release" to return item to pool.<br/>
 * When pool is empty, a thread will loop until next item is released.
 */
public class GenericPool<T> {


    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Object _syncObj;

    private final List<T> _pool;


    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public GenericPool(final T... items) {
        this(0, null); // unlimited pool
        if (null != items && items.length > 0) {
            this.init(items);
        }
    }

    public GenericPool(final int capacity,
                       final Delegates.Function<T> callback) {
        _syncObj = new Object();
        _pool = Collections.synchronizedList(new ArrayList<T>(capacity));

        this.init(capacity, callback);
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public int size() {
        synchronized (_pool) {
            return _pool.size();
        }
    }

    public T lock() {
        synchronized (_syncObj) {
            while (this.size() == 0) {
                try {
                    Thread.sleep(100);
                } catch (Throwable ignored) {
                    return null;
                }
            }
            return this.firstItem();
        }
    }

    public void release(final T item) {
        this.putItem(item);
    }

    public Object[] clear() {
        synchronized (_syncObj) {
            final Object[] result = new Object[_pool.size()];
            int i = 0;
            for (final Object item : _pool) {
                if (null != item) {
                    result[i] = item;
                }
                i++;
            }
            _pool.clear();
            return result;
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void init(final T... items) {
        synchronized (_pool) {
            _pool.addAll(Arrays.asList(items));
        }
    }

    private void init(final int capacity, final Delegates.Function<T> callback) {
        if (null != callback) {
            synchronized (_pool) {
                for (int i = 0; i < capacity; i++) {
                    final T item = callback.handle(i);
                    if (null != item) {
                        _pool.add(item);
                    }
                }
            }
        }
    }

    private void putItem(final T item) {
        synchronized (_pool) {
            _pool.add(item);
        }
    }

    public T firstItem() {
        synchronized (_pool) {
            return _pool.remove(0);
        }
    }
}
