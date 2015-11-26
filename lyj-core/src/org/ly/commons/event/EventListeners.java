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
package org.ly.commons.event;


import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Thread safe Map of listener's list.
 *
 * @author
 */
public class EventListeners
        implements Serializable {

    private final List<IEventListener> _listeners;

    public EventListeners() {
        _listeners = Collections.synchronizedList(new LinkedList<IEventListener>());
    }

    @Override
    protected void finalize() throws Throwable {
        if (null != _listeners) {
            this.clear();
        }
        super.finalize();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName());
        result.append("[");
        result.append("Items: ").append(this.size());
        result.append("]");

        return result.toString();
    }

    public int size() {
        synchronized (_listeners) {
            return _listeners.size();
        }
    }

    public boolean isEmpty() {
        synchronized (_listeners) {
            return _listeners.isEmpty();
        }
    }

    public boolean contains(final IEventListener listener) {
        synchronized (_listeners) {
            return _listeners.contains(listener);
        }
    }

    public void add(IEventListener listener) {
        synchronized (_listeners) {
            if (!_listeners.contains(listener)) {
                _listeners.add(listener);
            }
        }
    }

    public void clear() {
        synchronized (_listeners) {
            _listeners.clear();
        }
    }

    public void remove(final IEventListener listener) {
        synchronized (_listeners) {
            _listeners.remove(listener);
        }
    }

    public void remove(final int index) {
        synchronized (_listeners) {
            _listeners.remove(index);
        }
    }

    public IEventListener[] toArray() {
        synchronized (_listeners) {
            return _listeners.toArray(new IEventListener[_listeners.size()]);
        }
    }

    public int count(final String key) {
        synchronized (_listeners) {
            return _listeners.size();
        }
    }

    public String getListenerSnapshot() {
        synchronized (_listeners) {
            return this.getSnapshot();
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private String getSnapshot() {
        int size = _listeners.size();

        StringBuilder result = new StringBuilder();
        result.append("Total listeners: ").append(size);
        try {
            for (final IEventListener listener : _listeners) {
                result.append("\n ");
                result.append("\t").append(listener.toString());
            }
        } catch (Throwable t) {
            Logger logger = this.getLogger();
            logger.log(Level.WARNING, "Error getting listeners snapshot.", t);

            result.append("ERROR [").append(t.toString()).append("]");
        }
        return result.toString();
    }
}
