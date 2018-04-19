package org.ly.licensemanager.app.server.listeners.api.routing;

import org.ly.licensemanager.app.templates.Templates;
import org.lyj.commons.Delegates;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerContext;

/**
 * Common API superclass
 */
public abstract class AbstractApi
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------


    private static final String EMPTY_ITEM = ""; // "{\"_id\":0}"
    private static final String EMPTY_LIST = ""; // "[]"


    final static String PARAM_FILTER = "filter";
    final static String PARAM_SKIP = "skip";
    final static String PARAM_LIMIT = "limit";
    final static String PARAM_SORT = "sort";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractApi() {

    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void auth(final String token, final Delegates.SingleResultCallback<Boolean> callback) {
        ApiHelper.auth(token, callback);
    }

    protected String getParamToken(final HttpServerContext context) {
        return ApiHelper.getParamToken(context);
    }

    protected int getParamSkip(final HttpServerContext context) {
        return ConversionUtils.toInteger(this.getParam(context, PARAM_SKIP));
    }

    protected int getParamLimit(final HttpServerContext context) {
        return ConversionUtils.toInteger(this.getParam(context, PARAM_LIMIT));
    }

    protected String getParam(final HttpServerContext context,
                              final String paramName) {
        return this.getParam(context, paramName, "");
    }

    protected String getParam(final HttpServerContext context,
                              final String paramName,
                              final String defaultValue) {
        return ApiHelper.getParam(context, paramName, defaultValue);
    }

    protected void writeError(final HttpServerContext context,
                              final Throwable t) {
        this.writeError(context, t, "");
    }

    protected void writeError(final HttpServerContext context,
                              final Throwable t,
                              final String methodName) {
        context.writeJsonError(t, methodName);
    }

    protected void writeError(final HttpServerContext context,
                              final String error) {
        context.writeJsonError(error);
    }

    protected void writeInternalError(final HttpServerContext context, final Throwable t) {
        this.writeInternalError(context, t, "");
    }

    protected void writeInternalError(final HttpServerContext context, final Throwable t, final String methodName) {
        context.writeInternalServerError(t);
        if (StringUtils.hasText(methodName)) {
            super.error(methodName, t);
        }
    }

    protected void writeErroMissingParams(final HttpServerContext context, final String... names) {
        context.writeErroMissingParams(names);
    }

    protected void writeHTMLError(final HttpServerContext context) {
        final String lang = context.getLang();
        final String html = Templates.instance().getTemplateHTML(lang, Templates.TPL_ERROR,
                MapBuilder.createSS().put(Templates.ERROR, "").toMap());
        this.writeHTML(context, html);
    }

    protected void writeHTML(final HttpServerContext context, final String html) {
        context.writeHtml(html);
    }

    protected void writeJSON(final HttpServerContext context, final Object object) {
        context.writeJson(ApiHelper.wrapResponse(object));
    }

    protected void writeJSON(final HttpServerContext context, final String json) {
        context.writeJson(ApiHelper.wrapResponse(json));
    }

    protected void writeRawValue(final HttpServerContext context, final Object value) {
        context.writeRawValue(value);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


}
