package org.ly.commons.network.socket.server.helpers;


import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.network.socket.server.handlers.ISocketFilter;
import org.ly.commons.network.socket.server.handlers.SocketRequest;
import org.ly.commons.network.socket.server.handlers.SocketResponse;

public class FooFilter implements ISocketFilter {

    private Logger _logger;

    public FooFilter() {
        _logger = LoggingUtils.getLogger(this);
    }

    @Override
    public boolean handle(final SocketRequest request, final SocketResponse response) {
        // do nothing
        _logger.info("Foo handler is ignored");
        return false;
    }

}
