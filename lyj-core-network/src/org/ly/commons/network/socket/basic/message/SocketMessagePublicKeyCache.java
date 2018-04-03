package org.ly.commons.network.socket.basic.message;

import org.lyj.commons.io.memorycache.MemoryCache;
import org.lyj.commons.io.memorycache.MemoryCacheItem;

public class SocketMessagePublicKeyCache
        extends MemoryCache<SocketMessagePublicKeyCacheItem> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int DEFAULT_DURATION = 2 * 60 * 1000; // 2 minutes cache

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final int _duration;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessagePublicKeyCache() {
        this(DEFAULT_DURATION);
    }

    public SocketMessagePublicKeyCache(final int duration) {
        _duration = duration;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    @Override
    public MemoryCacheItem<SocketMessagePublicKeyCacheItem> get(final String key) {
        if (!super.containsKey(key)) {
            final SocketMessagePublicKeyCacheItem item = new SocketMessagePublicKeyCacheItem();
            item.clientId(key);
            super.put(key, new MemoryCacheItem<SocketMessagePublicKeyCacheItem>()
                    .duration(_duration)
                    .item(item));
        }
        return super.wakeUp(key);
    }
}
