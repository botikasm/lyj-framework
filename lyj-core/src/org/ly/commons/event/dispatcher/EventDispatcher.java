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

package org.ly.commons.event.dispatcher;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by angelogeminiani on 21/08/14.
 */
public final class EventDispatcher {

    // ------------------------------------------------------------------------
    //                      i n t e r f a c e s
    // ------------------------------------------------------------------------

    public interface IEventHandler {
        void onEvent(final Object sender, final String eventName, final Object... args);
    }

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final List<IEventHandler> _listeners;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EventDispatcher() {
        _listeners = Collections.synchronizedList(new LinkedList<IEventHandler>());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Register a callback for event dispatching
     *
     * @param handler Callback implementation
     */
    public void register(final IEventHandler handler) {
        synchronized (_listeners) {
            if (!_listeners.contains(handler)) {
                _listeners.add(handler);
            }
        }
    }

    /**
     * Dispatch an Event.
     *
     * @param sender    The invoker
     * @param eventName Name of Event to handle
     * @param args      Variant number of arguments to pass to callback
     */
    public void dispatch(final Object sender, final String eventName, final Object... args) {
        synchronized (_listeners) {
            for (IEventHandler handler : _listeners) {
                handler.onEvent(sender, eventName, args);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static EventDispatcher __instance;

    /**
     * Return singleton instance lazy initialized.
     *
     * @return EventDispatcher singleton
     */
    public static EventDispatcher getInstance() {
        if (null == __instance) {
            __instance = new EventDispatcher();
        }
        return __instance;
    }
}
