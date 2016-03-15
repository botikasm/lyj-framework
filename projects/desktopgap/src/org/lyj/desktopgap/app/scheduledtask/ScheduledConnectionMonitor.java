package org.lyj.desktopgap.app.scheduledtask;

import org.lyj.commons.async.future.Timed;
import org.lyj.commons.event.Event;
import org.lyj.commons.event.bus.MessageBus;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.network.NetworkUtils;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.desktopgap.app.bus.IEvents;

import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

/**
 * Runtime job scheduled
 */
public class ScheduledConnectionMonitor
        extends Timed implements IEvents {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final int RUN_EVERY_SECONDS = 10;

    private boolean _initialized;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ScheduledConnectionMonitor() {

        super(TimeUnit.SECONDS, 0, RUN_EVERY_SECONDS, 0, 0);
        super.start((t) -> {
            try {
                this.run();
            } catch (Throwable err) {
                super.error("run", FormatUtils.format("Error running connection monitor: %s",
                        ExceptionUtils.getMessage(err)));
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void run() throws Exception {
        if (!_initialized) {
            _initialized = true;
            LoggingRepository.getInstance().setLogFileName(this.getClass(), "connection_monitor.log");
        }

        // check network connection
        final boolean connected = this.isConnected();
        MessageBus.getInstance().emit(Event.create(this)
                .setName(ENAME_CONNECTION).setTag(ETAG_CONNECTION)
                .setData(connected));
    }

    private boolean isConnected() {
        try {
            final URL url = new URL("http://www.google.com");
            final Proxy proxy = NetworkUtils.getProxy();
            final URLConnection conn = url.openConnection(proxy);
            conn.connect();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ScheduledConnectionMonitor __instance;

    public static ScheduledConnectionMonitor start() {
        if (null == __instance) {
            __instance = new ScheduledConnectionMonitor();
        }
        return __instance;
    }

    public static void stop() {
        if (null != __instance) {
            __instance.stop(true);
        }
    }

}
