package org.ly.commons.network.socket.basic;

import org.json.JSONObject;
import org.lyj.commons.lang.CharEncoding;

public class SocketContext {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final int DEFAULT_TIMEOUT = 10 * 1000;
    public static final int DEFAULT_PORT = 5000;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _uid;
    private int _port;
    private int _timeout_ms;
    private String _charset;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    public SocketContext(final String uid) {
        _uid = uid;
        _charset = CharEncoding.UTF_8;
        _port = DEFAULT_PORT;
        _timeout_ms = DEFAULT_TIMEOUT;
    }

    @Override
    public String toString() {
        final JSONObject response = new JSONObject();

        response.put("uid", _uid);
        response.put("charset", _charset);
        response.put("port", _port);
        response.put("timeout", _timeout_ms);

        return response.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uid() {
        return _uid;
    }

    public int port() {
        return _port;
    }

    public SocketContext port(final int value) {
        _port = value;
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
