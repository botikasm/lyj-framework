package org.ly.commons.network.socket.basic.message;

public class SocketMessageHandShake
        extends SocketMessage {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessageHandShake() {
        super.type(MessageType.Handshake);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public SocketMessage signature(String value) {
        super.signature(value);
        super.body(value.getBytes());
        return this;
    }
}
