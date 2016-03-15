package org.lyj.desktopgap.app.server.api.connection;

import org.lyj.desktopgap.app.controllers.DataController;
import org.lyj.ext.netty.server.web.controllers.routing.RoutingContext;

/**
 * Connection
 */
public class ApiConnection {

    public static void is_connected(final RoutingContext context) {
        final boolean connected = DataController.instance().getConnected();
        context.writeJson(connected);
    }

}
