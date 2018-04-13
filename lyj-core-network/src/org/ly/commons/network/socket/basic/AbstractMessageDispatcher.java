package org.ly.commons.network.socket.basic;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.message.cipher.AbstractMessageCipher;

public abstract class AbstractMessageDispatcher
        extends SocketLogger {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int DEFAULT_CHUNK_SIZE = SocketSettings.CHUNK_SIZE; // buffer size

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final AbstractMessageCipher _cipher;

    private int _chunk_size;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractMessageDispatcher(final AbstractMessageCipher cipher) {
        _cipher = cipher;
        _chunk_size = DEFAULT_CHUNK_SIZE;

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String name() {
        return _cipher.name();
    }

    public byte[] signature() {
        return _cipher.publicKey().getBytes();
    }

    public AbstractMessageCipher cipher() {
        return _cipher;
    }

    public int chunkSize() {
        return _chunk_size;
    }

    public AbstractMessageDispatcher chunkSize(final int value) {
        _chunk_size = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }


}
