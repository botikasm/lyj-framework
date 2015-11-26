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

package org.ly.commons.network.socket.server.handlers.impl;

import org.ly.commons.network.socket.messages.multipart.MultipartInfo;
import org.ly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.ly.commons.network.socket.server.handlers.AbstractSocketHandler;
import org.ly.commons.network.socket.server.handlers.SocketRequest;
import org.ly.commons.network.socket.server.handlers.SocketResponse;
import org.ly.commons.network.socket.messages.tools.MultipartMessageUtils;

/**
 *
 */
public class HandlerMultipartMessage extends AbstractSocketHandler {

    public static final String TYPE = MultipartMessagePart.class.getName();

    @Override
    public void handle(final SocketRequest request, final SocketResponse response) {
        // add part to server pool
        if (request.isTypeOf(MultipartMessagePart.class)) {
            final MultipartMessagePart part = (MultipartMessagePart) request.read();
            // multipart messages are used to upload or download
            if (part.getInfo().getType() == MultipartInfo.MultipartInfoType.File) {
                if (part.getInfo().getDirection() == MultipartInfo.MultipartInfoDirection.Upload) {
                    // UPLOAD
                    MultipartMessageUtils.saveOnDisk(part);
                    request.getServer().addMultipartMessagePart(part);
                } else {
                    // DOWNLOAD
                    MultipartMessageUtils.setPartBytes(part); // read chunk bytes
                    // send back data with bytes
                    response.write(part);
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------


}
