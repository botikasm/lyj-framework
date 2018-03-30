package org.ly.commons.network.socket.basic;

import org.lyj.commons.lang.CharEncoding;

public class SocketContext {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private int _port;
    private boolean _encrypt;
    private int _timeout_ms;
    private String _charset;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    public SocketContext() {
        _charset = CharEncoding.UTF_8;
        _port = 5000;
        _timeout_ms = 5000;
        _encrypt = true;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int port() {
        return _port;
    }

    public SocketContext port(final int value) {
        _port = value;
        return this;
    }

    public boolean encrypt() {
        return _encrypt;
    }

    public SocketContext encrypt(final boolean value) {
        _encrypt = value;
        return this;
    }

    public int timeout() {
        return _timeout_ms;
    }

    public SocketContext timeout(final int value) {
        _timeout_ms = value;
        return this;
    }

    public String charset() {
        return _charset;
    }

    public SocketContext charset(final String value) {
        _charset = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
