package org.ly.commons.network.socket.server.helpers;


import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.network.socket.server.handlers.ISocketFilter;
import org.ly.commons.network.socket.server.handlers.SocketRequest;
import org.ly.commons.network.socket.server.handlers.SocketResponse;

import java.util.Date;

public class SampleFilter implements ISocketFilter {

    private Logger _logger;

    public SampleFilter() {
        _logger = LoggingUtils.getLogger(this);
    }

    @Override
    public boolean handle(final SocketRequest request, final SocketResponse response) {
        _logger.info("SERVER: " + " (" + (new Date()).toString() + "): " + request.read());
        try {
            Thread.sleep(1000);
        } catch (Throwable ignored) {
        }
        final String txt = " (" + (new Date()).toString() + "): " + request.read().toString();
        response.write(txt);
        return true;
    }

}
