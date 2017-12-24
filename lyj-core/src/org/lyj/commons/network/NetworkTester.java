package org.lyj.commons.network;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Date;

/**
 * Generic Network Controller that monitor network status
 */
public final class NetworkTester
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String URL = "https://httpbin.org/ip";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    private boolean _connected;
    private long _last_offline;
    private long _last_online;
    private long _count_offline_time;
    private long _count_online_time;
    private long _count_offline;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private NetworkTester() {
        _connected = false;
        _count_offline = 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("is_online=").append(this._connected);
        sb.append(", ");
        sb.append("was_offline=").append(this.wasOffLine());
        if (this.wasOffLine()) {
            sb.append(", ");
            sb.append("last_offline=").append(new Date(_last_offline));
            sb.append(", ");
            sb.append("count_offline=").append(_count_offline);
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isConnected() {
        if (!_connected) {
            this.refresh();
        }
        return _connected;
    }

    public boolean wasOffLine() {
        return this.offLineMilliseconds() > 0;
    }

    public long countOffLine() {
        return _count_offline;
    }

    public long onLineMilliseconds() {
        return Math.max(0, _count_online_time);
    }

    public long offLineMilliseconds() {
        return Math.max(0, _count_offline_time);
    }

    public synchronized void refresh() {
        try {
            final String content = URLUtils.getUrlContent(URL);
            this.setConnected(StringUtils.hasText(content));
        } catch (Throwable t) {
            this.setConnected(false);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void setConnected(final boolean value) {
        final boolean changed = !(value && _connected);
        _connected = value;
        if (value) {
            _last_online = DateUtils.timestamp();
            _count_online_time = Math.max(0, _last_offline - _last_online);
        } else {
            _last_offline = DateUtils.timestamp();
            _count_offline_time = Math.max(0, _last_online - _last_offline);
            _count_offline++;
        }
        if (changed) {
            super.info("changed network status", this.toString());
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static NetworkTester __instance;

    public static synchronized NetworkTester instance() {
        if (null == __instance) {
            __instance = new NetworkTester();
        }
        return __instance;
    }
}
