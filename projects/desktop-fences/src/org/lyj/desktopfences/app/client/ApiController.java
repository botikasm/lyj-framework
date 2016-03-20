package org.lyj.desktopfences.app.client;

import org.lyj.desktopfences.app.client.api.ApiUtils;
import org.lyj.ext.netty.server.web.controllers.routing.IRouter;

/**
 * Internal Api router
 */
public class ApiController {


    private static final String PATH_API = "/fences";
    private static final String PATH_API_UTILS = PATH_API.concat("/utils");

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ApiController(final IRouter router) {
        this.init(router);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final IRouter router) {

        //-- connection --//
        router.all(PATH_API_UTILS.concat("/version")).handler(ApiUtils::version);



    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static ApiController install(final IRouter router) {
        return new ApiController(router);
    }


}
