package org.ly.commons.network.socket.basic.message.impl;

public class SocketMessageHandShake
        extends SocketMessage {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessageHandShake(final String uid) {
        super(uid);
        super.type(MessageType.Handshake);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public SocketMessage signature(final byte[] value) {
        super.signature(value);
        super.body(value);
        return this;
    }
}
