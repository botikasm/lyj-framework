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

package org.ly.commons.network.socket.server;

import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.network.socket.messages.AbstractMessage;
import org.ly.commons.network.socket.messages.MessageResponse;
import org.ly.commons.network.socket.server.handlers.ISocketFilter;
import org.ly.commons.network.socket.server.handlers.ISocketHandler;
import org.ly.commons.network.socket.server.handlers.SocketRequest;
import org.ly.commons.network.socket.server.handlers.SocketResponse;
import org.ly.commons.network.socket.server.handlers.pool.SocketFilterPoolIterator;
import org.ly.commons.network.socket.server.handlers.pool.SocketHandlerPool;

import java.io.*;
import java.net.Socket;


public class ServerWorker extends Thread {

    private final Server _server;
    private final Socket _client;
    private final SocketHandlerPool _pool;

    public ServerWorker(final Server server, final Socket client) {
        _server = server;
        _client = client;
        _pool = _server.getHandlers();
    }

    @Override
    public void run() {
        try {
            // out and in
            final ObjectOutputStream out = new ObjectOutputStream(_client.getOutputStream());
            final ObjectInputStream in = new ObjectInputStream(_client.getInputStream());
            // read
            final Object input = in.readObject();
            if (null != input) {
                final SocketRequest request = new SocketRequest(new SocketRequestServer(_server), input);
                final SocketResponse response = new SocketResponse();

                //-- handle request and write response --//
                final MessageResponse output = this.handle(request, response);

                // write
                if (!output.isNull()) {
                    out.writeObject(output);
                    out.flush();
                }
            }
            out.close();
            in.close();
        } catch (EOFException ignored) {
        } catch (StreamCorruptedException ignored) {
        } catch (Throwable t) {
            this.onError(null, t);
        } finally {
            try {
                _client.close();
            } catch (Throwable ignored) {
            }
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void onError(final String message, final Throwable t) {
        try {
            _server.onError(message, t);
        } catch (Throwable ignored) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    private MessageResponse handle(final SocketRequest request, final SocketResponse response) {
        // response
        final MessageResponse output = new MessageResponse();

        // filters
        final SocketFilterPoolIterator iterator = _pool.getFiltersIterator();
        while (iterator.hasNext()) {
            final ISocketFilter handler = iterator.next();
            if (handler.handle(request, response)) {
                break;
            }
        }

        if (response.canHandle() && _pool.hasHandler(request.getType())) {
            final ISocketHandler handler = _pool.getHandler(request.getType());
            if (null != handler) {
                handler.handle(request, response);
            }
        }

        if(request.read() instanceof AbstractMessage){
            output.setUserToken(((AbstractMessage)request.read()).getUserToken());
        }
        output.setData(response.read());

        return output;
    }

    private Object read(final InputStream is) {
        Object result = null;
        try {
            final ObjectInputStream in = new ObjectInputStream(is);
            result = in.readObject();
        } catch (Throwable ignored) {
            // unable to read stream
        }
        return result;
    }
}
