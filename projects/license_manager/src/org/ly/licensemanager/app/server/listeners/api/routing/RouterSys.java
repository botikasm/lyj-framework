package org.ly.licensemanager.app.server.listeners.api.routing;


import org.ly.licensemanager.app.server.listeners.api.routing.impl.ApiLicense;
import org.ly.licensemanager.app.server.listeners.api.routing.impl.ApiUtil;
import org.ly.licensemanager.deploy.config.ConfigHelper;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.handlers.impl.RoutingHandler;

import java.util.Map;

/**
 * Internal Api router
 */
public class RouterSys
        extends RoutingHandler {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH_API = "/api";

    private static final String PATH_API_UTIL = PATH_API.concat("/util");

    private static final String PATH_API_LICENSE = PATH_API.concat("/license");


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final ApiLicense _api_license;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected RouterSys(final HttpServerConfig config) {
        super(config);

        _api_license = new ApiLicense();


        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

        super.all(PATH_API.concat("/version")).handler(ApiUtil::version);

        //-- util --//
        super.all(PATH_API_UTIL.concat("/version")).handler(ApiUtil::version);
        super.all(PATH_API_UTIL.concat("/ping")).handler(ApiUtil::ping);
        super.get(PATH_API_UTIL.concat("/redirect")).handler(ApiUtil::redirect);
        super.post(PATH_API_UTIL.concat("/md5")).handler(ApiUtil::md5);

        //-- license --//
        super.get(PATH_API_LICENSE.concat("/register/:app_token")).handler(_api_license::register);

    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static RouterSys create(final HttpServerConfig config) {
        return new RouterSys(config);
    }

    public static String urlApi() {
        return PathUtils.concat(ConfigHelper.instance().apiHost(), PATH_API);
    }

    public static String urlApi(final String path) {
        return PathUtils.concat(urlApi(), path);
    }

    public static String urlApi(final String path, final Map<String, ?> query_params) {
        return PathUtils.concat(urlApi(), path) +
                (null != query_params && !query_params.isEmpty()
                        ? "?" + StringUtils.toQueryString(query_params)
                        : "");
    }

}
