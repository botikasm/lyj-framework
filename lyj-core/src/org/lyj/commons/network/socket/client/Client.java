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

package org.lyj.commons.network.socket.client;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.cryptograph.GUID;
import org.lyj.commons.io.filetokenizer.FileChunkInfo;
import org.lyj.commons.io.filetokenizer.FileTokenizer;
import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.network.socket.messages.UserToken;
import org.lyj.commons.network.socket.messages.multipart.Multipart;
import org.lyj.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.lyj.commons.network.socket.messages.multipart.MultipartPool;
import org.lyj.commons.network.socket.messages.tools.MultipartMessageUtils;
import org.lyj.commons.network.socket.server.Server;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Socket Client
 */
public class Client {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 10000;

    // file download
    private static final int DEFAULT_CHUNK_SIZE = 1 * 3000 * 1024; // 3Mb
    private static final int DEFAULT_TIMEOUT = 60 * 30 * 1000; // 30 minute timeout


    private static final Class EVENT_ON_PART = Multipart.OnPartListener.class;
    private static final Class EVENT_ON_FULL = Multipart.OnFullListener.class;
    private static final Class EVENT_ON_TIME_OUT = Multipart.OnTimeOutListener.class;
    private static final Class EVENT_ON_ERROR = Delegates.ExceptionCallback.class;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private Socket _socket;

    private Proxy _proxy;
    private SocketAddress _address;
    private int _timeOut;
    private int _chunkSize;
    private final MultipartPool _multipartPool;       // manage downloads
    private final Delegates.Handlers _eventHandlers;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Client() {
        this(Proxy.NO_PROXY);
    }

    public Client(final Proxy proxy) {
        this(proxy, DEFAULT_TIMEOUT, DEFAULT_CHUNK_SIZE);
    }

    public Client(final Proxy proxy,
                  final int timeOut,
                  final int chunkSize) {
        _timeOut = timeOut;
        _chunkSize = chunkSize;
        _proxy = proxy;
        _address = new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT);
        _multipartPool = new MultipartPool(_timeOut);
        _eventHandlers = new Delegates.Handlers();

        this.init();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _multipartPool.clear();
            _eventHandlers.clear();
        } catch (Throwable ignore) {
        }
        super.finalize();
    }

    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

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

    public int getChunkSize() {
        return _chunkSize;
    }

    public void setChunkSize(final int value) {
        _chunkSize = value;
    }

    public int getTimeOut() {
        return _timeOut;
    }

    public void setTimeOut(final int value) {
        _timeOut = value;
        if (null != _multipartPool) {
            _multipartPool.setTimeout(_timeOut);
        }
    }

    public SocketAddress getAddress() {
        return _address;
    }

    public Socket getSocket() {
        return _socket;
    }

    public void setAddress(final SocketAddress value) {
        _address = value;
    }

    public boolean isConnected() {
        try {
            return null != _socket && _socket.isConnected();
        } catch (Throwable ignored) {
        }
        return false;
    }

    public void connect() throws IOException {
        this.connect(_address);
    }

    public void connect(final String host, final int port) throws IOException {
        final SocketAddress address = new InetSocketAddress(host, port);
        this.connect(address);
    }

    public void connect(final SocketAddress address) throws IOException {
        if (null != _socket) {
            try {
                _socket.close();
                _socket = null;
                _socket = new Socket(_proxy);
            } catch (Throwable ignored) {
                _socket = null;
                _socket = new Socket(_proxy);
            }
        } else {
            _socket = new Socket(_proxy);
        }

        _address = address;
        _socket.connect(address, 3000);
    }

    public void close() {
        try {
            if (null != _socket) {
                _socket.close();
            }
        } catch (Throwable ignored) {
            _socket = null;
        }
    }

    public Object send(final Object request) throws Exception {
        return send(_socket, request);
    }

    public Thread[] sendFile(final UserToken userToken,
                             final boolean useMultipleConnections,
                             final Delegates.ProgressCallback progressCallback,
                             final Delegates.ExceptionCallback errorHandler) throws Exception {
        final String fileName = userToken.getSourceAbsolutePath();
        Thread[] result = new Thread[0];
        if (this.isConnected() && FileUtils.exists(fileName)) {
            final String uid = GUID.create();
            final String[] chunks = FileTokenizer.splitFromChunkSize(fileName, uid, _chunkSize, null);
            try {
                result = this.sendFileChunks(PathUtils.getFilename(fileName, true),
                        userToken,
                        chunks,
                        useMultipleConnections,
                        progressCallback, errorHandler);
            } finally {
                this.clearFolder(chunks);
            }
        }
        return result;
    }

    public Thread[] getFile(final UserToken userToken,
                            final Delegates.ProgressCallback progressCallback,
                            final Delegates.ExceptionCallback errorHandler) throws Exception {
        final FileChunkInfo info = new FileChunkInfo(userToken.getLength(), _chunkSize);
        return this.getFileChunks(userToken,
                info,
                progressCallback, errorHandler);
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

    private void clearFolder(final String[] chunks) throws IOException {
        // clean temp files
        final String file = CollectionUtils.get(chunks, 0);
        if (StringUtils.hasText(file)) {
            FileUtils.delete(PathUtils.getParent(file));
        }
    }

    private Thread[] getFileChunks(final UserToken userToken,
                                   final FileChunkInfo chunkInfo,
                                   final Delegates.ProgressCallback progressCallback,
                                   final Delegates.ExceptionCallback errorHandler) {
        final Client self = this;
        final int len = chunkInfo.getChunkCount();
        final String transactionId = GUID.create();
        // creates array of workers to download file chunks
        return Async.maxConcurrent(len, 3, new Delegates.CreateRunnableCallback() {
            @Override
            public Runnable handle(int index, int length) {
                return new DownloadRunnable(index,
                        transactionId,
                        self,
                        userToken,
                        chunkInfo,
                        errorHandler);
            }
        });
    }

    private Thread[] sendFileChunks(final String fileName,
                                    final UserToken userToken,
                                    final String[] chunks,
                                    final boolean useMultipleConnections,
                                    final Delegates.ProgressCallback progressCallback,
                                    final Delegates.ExceptionCallback errorHandler) {
        final Client self = this;
        final int len = chunks.length;
        final String transactionId = GUID.create();
        return Async.maxConcurrent(len, 3, new Delegates.CreateRunnableCallback() {
            @Override
            public Runnable handle(final int index, final int length) {
                return new UploadRunnable(index,
                        transactionId,
                        self,
                        fileName,
                        userToken,
                        chunks,
                        useMultipleConnections,
                        errorHandler);
                /*return new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String chunk = chunks[index];
                            final MultipartInfo info = new MultipartInfo(fileName,
                                    MultipartInfo.MultipartInfoType.File, chunk, index, len);

                            final MultipartMessagePart part = new MultipartMessagePart();
                            part.setUserToken(userToken);
                            part.setInfo(info);
                            part.setUid(transactionId);
                            part.setData(FileUtils.copyToByteArray(new File(chunk)));

                            //-- send part --//
                            if (useMultipleConnections) {
                                send(getAddress(), part);
                            } else {
                                send(part);
                            }
                        } catch (Throwable t) {
                            if (null != errorHandler) {
                                errorHandler.handle(null, t);
                            } else {
                                LoggingUtils.getLogger(Client.class).log(Level.SEVERE, null, t);
                            }
                        }
                    }
                };*/
            }
        });
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
                try {
                    saveOnDisk(multipart);
                } catch (Throwable t) {
                    this.onError(null, t);
                }
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

    private static String saveOnDisk(final Multipart multipart) throws Exception {
        try {
            final UserToken userToken = new UserToken(multipart.getUserToken());
            final String target = userToken.getTargetAbsolutePath();
            // save file on disk and remove temp
            return MultipartMessageUtils.saveOnDisk(multipart, target);
        } catch (Exception t) {
            MultipartMessageUtils.remove(multipart);
            throw t;
        }
    }

    public static String sendString(final String request) throws Exception {
        return sendString("localhost", Server.DEFAULT_PORT, request);
    }

    public static String sendString(final String server, final int port, final String request) throws Exception {
        return (String) send(server, port, (Object) request);
    }

    public static Object send(final String host, final int port, final Object request) throws Exception {
        final SocketAddress address = new InetSocketAddress(host, port);
        return send(address, request);
    }

    public static Object send(final SocketAddress address, final Object request) throws Exception {
        Object response;

        final Client cli = new Client();
        cli.connect(address);
        response = cli.send(request);
        cli.close();

        return response;
    }

    public static Object send(final Socket socket, final Object request) throws Exception {
        Object response = null;
        final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        try {
            out.writeObject(request);
            out.flush();
            try {
                response = in.readObject();
            } catch (EOFException ignored) {
                // no response
            }
        } finally {
            out.close();
            in.close();
        }
        return response;
    }

    public static boolean ping(final String server, final int port) {
        try {
            final Client cli = new Client();
            cli.connect(server, port);
            cli.close();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
