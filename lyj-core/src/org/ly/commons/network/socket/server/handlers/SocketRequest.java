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

package org.ly.commons.network.socket.server.handlers;

import org.ly.IConstants;
import org.ly.commons.network.socket.server.SocketRequestServer;

/**
 * Socket request Message
 */
public class SocketRequest {

    private final SocketRequestServer _server;
    private final Object _data;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public SocketRequest(final SocketRequestServer server, final Object data) {
        _data = data;
        _server = server;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public SocketRequestServer getServer() {
        return _server;
    }

    public boolean isTypeOf(final Class aclass) {
        if (null != aclass && null != _data) {
            return aclass.equals(this.getTypeClass());
        }
        return false;
    }

    public Class getTypeClass() {
        if (null != _data) {
            return _data.getClass();
        }
        return null;
    }

    public String getType() {
        if (null != _data) {
            return _data.getClass().getName();
        }
        return IConstants.NULL;
    }

    public Object read() {
        return _data;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


}
