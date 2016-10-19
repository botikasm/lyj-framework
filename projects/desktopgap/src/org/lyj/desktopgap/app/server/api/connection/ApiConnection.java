package org.lyj.desktopgap.app.server.api.connection;

import org.lyj.desktopgap.app.controllers.DataController;
import org.lyj.ext.netty.server.web.HttpServerContext;

/**
 * Connection
 */
public class ApiConnection {

    public static void is_connected(final HttpServerContext context) {
        final boolean connected = DataController.instance().getConnected();
        context.writeJson(connected);
    }

}
