package org.ly.licensemanager.app.server.listeners.api.routing;

import org.json.JSONObject;
import org.ly.licensemanager.app.server.controllers.TokenController;
import org.ly.licensemanager.app.templates.Templates;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.ext.netty.server.web.HttpServerContext;

import java.util.Collection;
import java.util.Map;

/**
 * Helper class
 */
public abstract class ApiHelper {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String FLD_RESPONSE = HttpServerContext.FLD_RESPONSE;

    private static final String PARAM_APP_TOKEN = "app_token";

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static void auth(final String token, final Delegates.SingleResultCallback<Boolean> callback) {
        if (StringUtils.hasText(token)) {
            //VALIDATE TOKEN LOOKING FOR APPS
            TokenController.instance().auth(token, (err, exists) -> {
                if (null != err) {
                    Delegates.invoke(callback, err, false);
                } else {
                    if (!exists) {
                        Delegates.invoke(callback, new Exception("Security issue, this request has not been authorized!"), false);
                    } else {
                        Delegates.invoke(callback, null, true);
                    }
                }
            });
        } else {
            Delegates.invoke(callback, new Exception("Missing Token"), false);
        }
    }

    public static String lang(final HttpServerContext context){
        return context.getLang();
    }

    public static String getParamToken(final HttpServerContext context) {
        return getParam(context, PARAM_APP_TOKEN);
    }

    public static String getParam(final HttpServerContext context,
                                  final String paramName) {
        return getParam(context, paramName, "");
    }

    public static String getParam(final HttpServerContext context,
                                  final String paramName,
                                  final String defaultValue) {
        String result = "";
        if (null != context) {
            result = context.params().getString(paramName);
            if (!StringUtils.hasText(result)) {
                result = context.params().getString(StringUtils.replace(paramName, "_", ""));
            }
            if (!StringUtils.hasText(result)) {
                result = defaultValue;
            }
        }
        return result;
    }

    public static JSONObject wrapResponse(final Object obj) {
        final JSONObject response = new JSONObject();
        response.put("response", "");

        if (obj instanceof Map) {
            final JSONObject json = new JSONObject((Map) obj);
            response.put(FLD_RESPONSE, json);
        } else if (StringUtils.isJSONObject(obj)) {
            response.put(FLD_RESPONSE, new JSONObject(obj.toString()));
        } else if (StringUtils.isJSONArray(obj) || obj instanceof Collection) {
            response.put(FLD_RESPONSE, JsonWrapper.toJSONArray(obj));
        } else {
            response.putOpt(FLD_RESPONSE, null != obj ? obj.toString() : "");
        }

        return response;
    }

    public static void writeError(final HttpServerContext context,
                                  final Throwable t) {
        writeError(context, t, "");
    }

    public static void writeError(final HttpServerContext context,
                                  final Throwable t,
                                  final String methodName) {
        context.writeJsonError(t, methodName);
    }

    public static void writeError(final HttpServerContext context,
                                  final String error) {
        context.writeJsonError(error);
    }

    public static void writeErroMissingParams(final HttpServerContext context, final String... names) {
        context.writeErroMissingParams(names);
    }

    public static void writeHTMLError(final HttpServerContext context) {
        final String lang = context.getLang();
        final String html = Templates.instance().getTemplateHTML(lang, Templates.TPL_ERROR,
                MapBuilder.createSS().put(Templates.ERROR, "").toMap());
        writeHTML(context, html);
    }

    public static void writeHTML(final HttpServerContext context, final String html) {
        context.writeHtml(html);
    }

    public static void writeJSON(final HttpServerContext context, final Object object) {
        context.writeJson(ApiHelper.wrapResponse(object));
    }

    public static void writeJSON(final HttpServerContext context, final String json) {
        context.writeJson(ApiHelper.wrapResponse(json));
    }

    public static void writeRawValue(final HttpServerContext context, final Object value) {
        context.writeRawValue(value);
    }

}
