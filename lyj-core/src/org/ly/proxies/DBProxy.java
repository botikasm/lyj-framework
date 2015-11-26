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

package org.ly.proxies;

/**
 *
 */
public class DBProxy {

    private IDBProxy _proxy;

    private DBProxy() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static DBProxy __instance;

    private static DBProxy getInstance() {
        if (null == __instance) {
            __instance = new DBProxy();
        }
        return __instance;
    }

    public static <T> IDBProxy<T> get() {
        return getInstance()._proxy;
    }

    public static void register(final IDBProxy proxy) {
        getInstance()._proxy = proxy;
    }

}
