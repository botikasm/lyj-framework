package org.ly.commons.network.socket.basic.message;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.io.memorycache.MemoryCache;
import org.lyj.commons.io.memorycache.MemoryCacheItem;
import org.lyj.commons.util.StringUtils;

import java.util.Set;

public class SocketMessagePublicKeyCache
        extends MemoryCache<SocketMessagePublicKeyCacheItem> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int DEFAULT_DURATION = 2 * 60 * 1000; // 2 minutes cache

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _uid;
    private final int _duration;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessagePublicKeyCache(final String uid) {
        this(uid, DEFAULT_DURATION);
    }

    public SocketMessagePublicKeyCache(final String uid,
                                       final int duration) {
        _uid = uid;
        _duration = duration;
    }

    @Override
    public String toString() {
        final JSONArray response = new JSONArray();
        final Set<String> keys = super.keys();
        for (final String key : keys) {
            final JSONObject item = new JSONObject();
            item.put(key, super.get(key).item().publicKey());
            response.put(item);
        }
        return response.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    @Override
    public synchronized MemoryCacheItem<SocketMessagePublicKeyCacheItem> get(final String key) {

        if (!super.containsKey(key)) {
            final SocketMessagePublicKeyCacheItem item = new SocketMessagePublicKeyCacheItem();
            item.clientId(key);
            super.put(key, new MemoryCacheItem<SocketMessagePublicKeyCacheItem>()
                    .duration(_duration)
                    .item(item));
        }
        return super.wakeUp(key);
    }

    public synchronized String getKey(final String owner_id) {
        final MemoryCacheItem<SocketMessagePublicKeyCacheItem> item = this.get(owner_id);
        final String public_key = item.item().publicKey();

        //System.out.println(_uid + " getKey: " + owner_id + " " + public_key);

        return StringUtils.hasText(public_key) ? public_key : "";
    }

    public synchronized void setKey(final String owner_id,
                                    final String public_key) {
        if (StringUtils.hasText(public_key)) {

            final MemoryCacheItem<SocketMessagePublicKeyCacheItem> item = this.get(owner_id);
            if (null == item.item()) {
                item.item(new SocketMessagePublicKeyCacheItem());
            }
            item.item().clientId(owner_id);
            item.item().publicKey(public_key);

            // System.out.println(_uid + " setKey: " + owner_id + " " + public_key);
        }
    }
}
