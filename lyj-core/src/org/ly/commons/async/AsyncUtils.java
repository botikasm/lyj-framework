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

package org.ly.commons.async;

import org.ly.commons.Delegates;

/**
 *
 */
public class AsyncUtils {

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    /**
     * Creates array of Async actions
     *
     * @param length
     * @param callback
     * @return
     */
    public static Thread[] createArray(final int length, final Delegates.CreateRunnableCallback callback) {
        final Thread[] result = new Thread[length];
        for (int i = 0; i < length; i++) {
            final Runnable action = null != callback ? callback.handle(i, length) : getEmptyAction();
            result[i] = new Thread(action);
            result[i].setDaemon(true);
        }
        return result;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static Runnable getEmptyAction() {
        return new Runnable() {
            @Override
            public void run() {
                // nothing to to, just a stub
            }
        };
    }

}
