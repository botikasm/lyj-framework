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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Main mutex dispenser.
 * Mutex are objects you can synchronize using "synchronize" keyword.
 * <code>synchronize(mymutex){... code ...}</code>
 * <p/>
 * To retrieve a mutex object call get().
 */
public class MutexPool {

    private static final long TIMEOUT = 30 * 1000;

    private final Map<Object, Mutex> _pool;
    private MutexGarbageCollector _gc;

    public MutexPool() {
        _pool = Collections.synchronizedMap(new HashMap<Object, Mutex>());
        _gc = new MutexGarbageCollector(this);
        _gc.start();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } catch (Throwable ignored) {
        } finally {
            super.finalize();
        }
    }

    public void close() {
        synchronized (_pool) {
            _pool.clear();
            if (null != _gc) {
                _gc.interrupt();
                _gc = null;
            }
        }
    }

    public Mutex get(final Object key) {
        synchronized (_pool) {
            if (null != _gc) {
                if (!_pool.containsKey(key)) {
                    _pool.put(key, new Mutex(TIMEOUT));
                }
                return _pool.get(key).wakeUp();
            } else {
                // never null
                return new Mutex(TIMEOUT);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


    private static class MutexGarbageCollector extends Thread {

        private final MutexPool _mutexPool;

        public MutexGarbageCollector(final MutexPool pool) {
            _mutexPool = pool;
            super.setPriority(Thread.NORM_PRIORITY);
            super.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (!super.isInterrupted()) {
                    Thread.sleep(TIMEOUT);
                    this.garbage();
                }
            } catch (InterruptedException ignored) {
            }
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private void garbage() {
            synchronized (_mutexPool._pool) {
                final Iterator<Object> i = _mutexPool._pool.keySet().iterator();
                while (i.hasNext()) {
                    final Object key = i.next();
                    final Mutex mutex = _mutexPool._pool.get(key);
                    if (mutex.isExpired()) {
                        i.remove();
                        // System.out.println("removed: " + mutex);
                    }
                }
            }
        }

    }
}
