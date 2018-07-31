package org.ly.ose.server.application.endpoints.api.routing;


import org.ly.ose.server.application.endpoints.api.routing.impl.ApiProgram;
import org.ly.ose.server.application.endpoints.api.routing.impl.ApiUtil;
import org.ly.ose.server.deploy.config.ConfigHelper;
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
    private static final String PATH_API_PROGRAM = PATH_API.concat("/program");

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    //private final ApiAccount _api_account;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected RouterSys(final HttpServerConfig config) {
        super(config);

        // _api_account = new ApiAccount();


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
        super.post(PATH_API_UTIL.concat("/md5")).handler(ApiUtil::md5);
        super.get(PATH_API_UTIL.concat("/log/:app_token/:name")).handler(ApiUtil::log);

        //-- program --//
        super.post(PATH_API_PROGRAM.concat("/invoke")).handler(ApiProgram::invoke);
        // https://localhost:4000/api/program/invoke/iuhdiu87w23ruh897dfyc2w3r/it/session_12234/system.utils/count/null
        super.get(PATH_API_PROGRAM.concat("/invoke/:app_token/:lang/:client_id/:namespace/:function/:params")).handler(ApiProgram::invoke);
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
