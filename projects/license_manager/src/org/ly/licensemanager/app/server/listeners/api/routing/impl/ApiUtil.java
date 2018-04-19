package org.ly.licensemanager.app.server.listeners.api.routing.impl;


import org.ly.licensemanager.IConstants;
import org.ly.licensemanager.app.server.listeners.api.routing.ApiHelper;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerContext;

public class ApiUtil {

    public static void ping(final HttpServerContext context) {
        context.writeJson("true");
    }

    public static void version(final HttpServerContext context) {
        context.writeJson("{\"app\":\"" + IConstants.APP_VERSION + "\"}");
    }

    public static void md5(final HttpServerContext context) {
        final String token = ApiHelper.getParamToken(context);

        ApiHelper.auth(token, (err, valid) -> {
            if (null == err) {
                final String value = context.getParam("value");
                if (StringUtils.hasText(value)) {
                    try {
                        final String md5 = MD5.encode(value);

                        ApiHelper.writeJSON(context, md5);
                    } catch (Throwable t) {
                        ApiHelper.writeError(context, t, "login");
                    }

                } else {
                    ApiHelper.writeErroMissingParams(context, "value");
                }
            } else {
                ApiHelper.writeError(context, err);
            }
        });
    }

    /**
     * https://localhost:4000/api/util/redirect?paramXXX=param&url=gianangelogeminiani.me/ecco-cosa-devi-sapere-prima-di-lanciarti-in-una-startup/?param1=jddhd
     *
     * @param context
     */
    public static void redirect(final HttpServerContext context) {
        final String raw_url = context.getParam("url");
        if (StringUtils.hasText(raw_url)) {
            final String url = PathUtils.hasProtocol(raw_url) || raw_url.startsWith("http") ? raw_url : "http://" + raw_url;
            context.writeRedirect(url);

        }
    }
}
