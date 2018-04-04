package org.ly.commons.network.socket.basic.message;

import org.lyj.commons.util.json.JsonItem;

public class SocketMessagePublicKeyCacheItem extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_CLIENT_ID = "client_id";
    private static final String FLD_PUBLIC_KEY = "public_key";
    private static final String FLD_PUBLIC_KEY_SIGNATURE = "public_key_signature";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessagePublicKeyCacheItem() {
        super();
    }

    public SocketMessagePublicKeyCacheItem(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String clientId() {
        return super.getString(FLD_CLIENT_ID);
    }

    public SocketMessagePublicKeyCacheItem clientId(final String value) {
        super.put(FLD_CLIENT_ID, value);
        return this;
    }

    public String publicKey() {
        return super.getString(FLD_PUBLIC_KEY);
    }

    public SocketMessagePublicKeyCacheItem publicKey(final String value) {
        super.put(FLD_PUBLIC_KEY, value);
        return this;
    }

    public SocketMessagePublicKeyCacheItem publicKeySignature(final String value) {
        super.put(FLD_PUBLIC_KEY_SIGNATURE, value);
        return this;
    }

    public String publicKeySignature() {
        return super.getString(FLD_PUBLIC_KEY_SIGNATURE);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
