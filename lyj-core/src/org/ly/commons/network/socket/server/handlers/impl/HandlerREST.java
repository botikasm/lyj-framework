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

import org.ly.commons.lang.CharEncoding;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.network.socket.messages.rest.RESTMessage;
import org.ly.commons.network.socket.server.handlers.AbstractSocketHandler;
import org.ly.commons.network.socket.server.handlers.SocketRequest;
import org.ly.commons.network.socket.server.handlers.SocketResponse;
import org.ly.commons.remoting.rest.RESTRegistry;
import org.ly.commons.remoting.rest.wrapper.MethodWrapper;

/**
 * REST Command Handler
 */
public class HandlerREST extends AbstractSocketHandler {

    public static final String TYPE = RESTMessage.class.getName();

    // --------------------------------------------------------------------
    //               o v e r r i d e
    // --------------------------------------------------------------------

    @Override
    public void handle(final SocketRequest request, final SocketResponse response) {
        // manage request
        if (request.isTypeOf(RESTMessage.class)) {
            final RESTMessage message = (RESTMessage) request.read();
            final Object invoke_response = invoke(message);
            if (null != invoke_response) {
                response.write(invoke_response);
            }
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static Logger getLogger() {
        return LoggingUtils.getLogger(HandlerREST.class);
    }

    private static Object invoke(final RESTMessage message) {
        if (null != message) {
            try {
                final MethodWrapper mw = RESTRegistry.getMethod(message.getMethod(), message.getPath());
                if (null != mw) {
                    final byte[] bytes = mw.execute(message.getPath(), message.getDataAsJSON());
                    return null != bytes ? new String(bytes, CharEncoding.UTF_8) : "";
                } else {
                    // method not found
                    getLogger().error("Method not found: " + message.toString());
                }
            } catch (Throwable t) {
                return t;
            }
        }
        return null;
    }

}
