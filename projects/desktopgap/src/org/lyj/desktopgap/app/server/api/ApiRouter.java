package org.lyj.desktopgap.app.server.api;

import org.lyj.desktopgap.app.IConstants;
import org.lyj.desktopgap.app.server.api.connection.ApiConnection;
import org.lyj.desktopgap.app.server.api.gui.ApiGui;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.controllers.routing.RoutingContext;
import org.lyj.ext.netty.server.web.handlers.impl.RoutingHandler;

/**
 * Internal Api router
 */
public class ApiRouter
        extends RoutingHandler {


    private static final String PATH_API = "/desktopgap_api";

    private static final String PATH_API_CONNECTION = PATH_API.concat("/connection");
    private static final String PATH_API_GUI = PATH_API.concat("/gui");

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected ApiRouter(final HttpServerConfig config) {
        super(config);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

        super.all(PATH_API.concat("/version")).handler(this::handleVersion);

        //-- connection --//
        super.all(PATH_API_CONNECTION.concat("/is_connected")).handler(ApiConnection::is_connected);

        //-- gui --//
        super.all(PATH_API_GUI.concat("/file_chooser")).handler(ApiGui::fileChooser);
    }

    private void handleVersion(final RoutingContext context) {
        context.writeJson(IConstants.APP_VERSION);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static ApiRouter create(final HttpServerConfig config) {
        return new ApiRouter(config);
    }


}
