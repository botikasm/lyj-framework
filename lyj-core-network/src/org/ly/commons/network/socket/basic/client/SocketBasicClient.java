package org.ly.commons.network.socket.basic.client;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.RandomUtils;

import java.io.File;
import java.util.Map;

public class SocketBasicClient
        extends SocketLogger {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final SocketBasicClientDispatcher _dispatcher;
    private final String _uid;

    private String _host;
    private int _port;
    private int _timeout_ms;
    private String _charset;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketBasicClient() {
        _uid = RandomUtils.randomUUID();
        _port = SocketContext.DEFAULT_PORT;
        _timeout_ms = SocketContext.DEFAULT_TIMEOUT;
        _charset = CharEncoding.UTF_8;

        _dispatcher = new SocketBasicClientDispatcher();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String uid() {
        return this._uid;
    }

    public String host() {
        return _host;
    }

    public SocketBasicClient host(final String value) {
        _host = value;
        return this;
    }

    public int port() {
        return _port;
    }

    public SocketBasicClient port(final int value) {
        _port = value;
        return this;
    }

    public int timeout() {
        return _timeout_ms;
    }

    public SocketBasicClient timeout(final int value) {
        _timeout_ms = value;
        return this;
    }

    public String charset() {
        return _charset;
    }

    public SocketBasicClient charset(final String value) {
        _charset = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Client sent an handshake request to a target.
     * Use handshake to activate encryption.
     */
    public void handShake() throws Exception {
        _dispatcher.handShake(this.context());
    }

    public SocketMessage send(final String message) throws Exception {
        return _dispatcher.send(message, null, this.context(_timeout_ms));
    }

    public SocketMessage send(final String message,
                              final Map<String, Object> headers) throws Exception {
        return _dispatcher.send(message, headers, this.context(_timeout_ms));
    }

    public SocketMessage send(final String message,
                              final int timeout_ms) throws Exception {
        return _dispatcher.send(message, null, this.context(timeout_ms));
    }

    public SocketMessage send(final String message,
                              final Map<String, Object> headers,
                              final int timeout_ms) throws Exception {
        return _dispatcher.send(message, headers, this.context(timeout_ms));
    }

    public SocketMessage send(final File file) throws Exception {
        return _dispatcher.send(file, null, this.context(_timeout_ms));
    }

    public SocketMessage send(final File file,
                              final Map<String, Object> headers) throws Exception {
        return _dispatcher.send(file, headers, this.context(_timeout_ms));
    }

    public SocketMessage send(final File file,
                              final int timeout_ms) throws Exception {
        return _dispatcher.send(file, null, this.context(timeout_ms));
    }

    public SocketMessage send(final File file,
                              final Map<String, Object> headers,
                              final int timeout_ms) throws Exception {
        return _dispatcher.send(file, headers, this.context(timeout_ms));
    }

    public SocketMessage send(final SocketMessage message) throws Exception {
        return _dispatcher.send(message, this.context(_timeout_ms));
    }

    public SocketMessage send(final SocketMessage message,
                              final int timeout_ms) throws Exception {
        return _dispatcher.send(message, this.context(timeout_ms));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private SocketContext context() {
        return new SocketContext(_uid)
                .host(this.host())
                .port(this.port())
                .timeout(this.timeout())
                .charset(this.charset());
    }

    private SocketContext context(final int timeout) {
        return new SocketContext(_uid)
                .host(this.host())
                .port(this.port())
                .timeout(timeout)
                .charset(this.charset());
    }


}
