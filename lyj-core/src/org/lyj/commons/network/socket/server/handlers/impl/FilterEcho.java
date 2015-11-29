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

package org.lyj.commons.network.socket.server.handlers.impl;

import org.lyj.commons.network.socket.server.handlers.ISocketFilter;
import org.lyj.commons.network.socket.server.handlers.SocketRequest;
import org.lyj.commons.network.socket.server.handlers.SocketResponse;

/**
 * Just echo back the message that you receive. This Handler could be used
 * for testing that a communication channel is up and running.
 */
public class FilterEcho implements ISocketFilter {

    public boolean handle(final SocketRequest request, final SocketResponse response) {
        response.write(request.read());
        return true;
    }
}
