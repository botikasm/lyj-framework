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

import org.ly.commons.Delegates;
import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.network.socket.messages.multipart.Multipart;
import org.ly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.ly.commons.network.socket.messages.multipart.MultipartPool;
import org.ly.commons.network.socket.server.handlers.ISocketFilter;
import org.ly.commons.network.socket.server.handlers.ISocketHandler;
import org.ly.commons.network.socket.server.handlers.impl.HandlerMultipartMessage;
import org.ly.commons.network.socket.server.handlers.pool.SocketHandlerPool;
import org.ly.commons.network.socket.messages.tools.MultipartMessageUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Very simple socket server example. That responds to a single object with
 * another object. Could be used as the basis for something more complex, but
 * this illustrates the basics of TCP/IP communication.
 * 
 * A Client will call a Server with a message. The Server will respond with a message.
 * In this simplest implementation the messages can be any serializable object.
 * 
 * To setup a server:
 *  1) Create your handler, a class that implements the simple SimpleSocketHandler 
 *     interface.
 *  2) Call the static Server.startServer() method with a port and an
 *     instance of your Handler defined above.
 *     
 * To call the server from a client:
 *  1) Call the static Client.send() method specifying the server host, port, and
 *     message. This returns your response.
 */
public class Server extends Thread {

    public static int DEFAULT_PORT = 14444;
    public static final int DEFAULT_MULTIPART_TIMEOUT = 60 * 2 * 1000; // two minute timeout

    // --------------------------------------------------------------------
    //               e v e n t s
    // --------------------------------------------------------------------

    public static interface OnStart {
        void handle(final Server sender);
    }

    private static final Class EVENT_ON_START = OnStart.class;
    private static final Class EVENT_ON_PART = Multipart.OnPartListener.class;
    private static final Class EVENT_ON_FULL = Multipart.OnFullListener.class;
    private static final Class EVENT_ON_TIME_OUT = Multipart.OnTimeOutListener.class;
    private static final Class EVENT_ON_ERROR = Delegates.ExceptionCallback.class;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final MultipartPool _multipartPool;
    private final Delegates.Handlers _eventHandlers;
    private final int _port;
    private SocketHandlerPool _handlers;
    private ServerSocket _socket;
    private boolean _running;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Server(final int port) throws IOException {
        this(port, DEFAULT_MULTIPART_TIMEOUT, null);
    }

    public Server(final int port,
                  final int uploadTimeout) throws IOException {
        this(port, uploadTimeout, null);
    }

    public Server(final int port,
                  final int uploadTimeout,
                  final Class<? extends ISocketFilter>[] handlers) throws IOException {
        super("Smartly-SocketServer");
        _running = false;
        _port = port;
        _handlers = new SocketHandlerPool(handlers);
        _socket = new ServerSocket(_port);
        _multipartPool = new MultipartPool(uploadTimeout);
        _eventHandlers = new Delegates.Handlers();

        this.init();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _multipartPool.clear();
            _handlers.clear();
            _eventHandlers.clear();
        } catch (Throwable ignore) {
        }
        super.finalize();
    }


    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

    public void onStart(final OnStart handler) {
        _eventHandlers.add(handler);
    }

    public void onError(final Delegates.ExceptionCallback listener) {
        _eventHandlers.add(listener);
    }

    public void onMultipartPart(final Multipart.OnPartListener listener) {
        _eventHandlers.add(listener);
    }

    public void onMultipartFull(final Multipart.OnFullListener listener) {
        _eventHandlers.add(listener);
    }

    public void onMultipartTimeOut(final Multipart.OnTimeOutListener listener) {
        _eventHandlers.add(listener);
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void run() {
        this.startServer();
    }

    public void stopServer() {
        this.getLogger().info("Server: Stopping server...");
        try {
            if (_socket != null) {
                _socket.close();
            }
            if (null != _handlers) {
                _handlers.clear();
            }
        } catch (Throwable t) {
            this.onError(null, t);
        }
    }

    public boolean isServerRunning() {
        return _running;
    }

    public SocketHandlerPool getHandlers() {
        return _handlers;
    }

    public Server addFilter(final Class<? extends ISocketFilter> handler) {
        _handlers.addFilter(handler);
        return this;
    }

    public Server removeFilter(final Class<? extends ISocketFilter> handler) {
        _handlers.removeFilter(handler);
        return this;
    }

    public Server addHandler(final Class type, final Class<? extends ISocketHandler> handler) {
        this.addHandler(type.getName(), handler);
        return this;
    }

    public Server addHandler(final String type, final Class<? extends ISocketHandler> handler) {
        _handlers.addHandler(type, handler);
        return this;
    }

    public void addMultipartMessagePart(final MultipartMessagePart part) {
        _multipartPool.add(part);
    }


    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() {

        //-- register default handlers --//
        this.addHandler(HandlerMultipartMessage.TYPE,
                HandlerMultipartMessage.class);

        //-- init multipart pool --//
        _multipartPool.onPart(new Multipart.OnPartListener() {
            @Override
            public void handle(final Multipart sender, final MultipartMessagePart part) {
                onMultipartPart(sender, part);
            }
        });
        _multipartPool.onFull(new Multipart.OnFullListener() {
            @Override
            public void handle(Multipart sender) {
                onMultipartFull(sender);
            }
        });
        _multipartPool.onTimeOut(new Multipart.OnTimeOutListener() {
            @Override
            public void handle(Multipart sender) {
                onMultipartTimeout(sender);
            }
        });
    }

    private void startServer() {
        this.getLogger().info("Starting server on port [" + _port + "] with handlers [" + _handlers.toString() + "]");
        try {
            _running = true;
            this.onStart();
            while (true) {
                // accept client
                final Socket client = _socket.accept();
                // handle request in thread
                final ServerWorker st = new ServerWorker(this, client);
                st.start();
            }
        } catch (Throwable t) {
            if (_socket != null && _socket.isClosed()) {
                //Ignore if closed by stopServer() call
            } else {
                this.onError(null, t);
            }
        } finally {
            _socket = null;
            _running = false;
        }
        this.getLogger().info("Stopped");
    }

    void onStart() {
        _eventHandlers.trigger(EVENT_ON_START, this);
    }

    void onError(final String message, final Throwable error) {
        if (_eventHandlers.contains(EVENT_ON_ERROR)) {
            _eventHandlers.trigger(EVENT_ON_ERROR, message, error);
        } else {
            this.getLogger().log(Level.SEVERE, message, error);
        }
    }

    private void onMultipartPart(final Multipart multipart, final MultipartMessagePart part) {
        try {
            if (_eventHandlers.contains(EVENT_ON_PART)) {
                _eventHandlers.triggerAsync(EVENT_ON_PART, multipart, part);
            } else {
                // no external handlers.
                // handle internally
            }
        } catch (Throwable ignored) {
        }
    }

    private void onMultipartFull(final Multipart multipart) {
        try {
            if (_eventHandlers.contains(EVENT_ON_FULL)) {
                _eventHandlers.triggerAsync(EVENT_ON_FULL, multipart);
            } else {
                // no external handlers.
                // handle internally

            }
        } catch (Throwable ignored) {

        }
    }

    private void onMultipartTimeout(final Multipart multipart) {
        // timeout
        try {
            if (_eventHandlers.contains(EVENT_ON_TIME_OUT)) {
                _eventHandlers.triggerAsync(EVENT_ON_TIME_OUT, multipart);
            } else {
                // no external handlers.
                // handle internally
                try {
                    MultipartMessageUtils.remove(multipart);
                } catch (Throwable t) {
                    this.onError(null, t);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------


    public static Server startServer(final Class<ISocketFilter>[] handlers) throws Exception {
        return startServer(Server.DEFAULT_PORT, DEFAULT_MULTIPART_TIMEOUT, handlers);
    }

    public static Server startServer(final int port,
                                     final Class<ISocketFilter>[] handlers) throws Exception {
        return startServer(port, DEFAULT_MULTIPART_TIMEOUT, handlers);
    }

    public static synchronized Server startServer(final int port,
                                                  final int uploadTimeout,
                                                  final Class<ISocketFilter>[] handlers) throws Exception {
        final Server server = new Server(port, uploadTimeout, handlers);
        server.start();
        while (!server.isServerRunning()) {
            Thread.sleep(100);
        }
        return server;
    }

}
