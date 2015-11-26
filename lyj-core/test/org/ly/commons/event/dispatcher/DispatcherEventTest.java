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


import org.junit.Test;

/**
 * Created by angelogeminiani on 21/08/14.
 */

public class DispatcherEventTest {


    @Test
    public void testEvent() {

        // register a callback for event handler
        EventDispatcher.getInstance().register(new EventDispatcher.IEventHandler() {
            @Override
            public void onEvent(Object sender, String eventName, Object... args) {
                System.out.println("Handled Event: '" + eventName + "'; invoked from: '" +
                        sender.getClass().getSimpleName() + "'; with " + args.length + " args.");
            }
        });

        // invoke test event
        EventDispatcher.getInstance().dispatch(this, "onTest", "arg1", 1234, "arg3");
    }


}
