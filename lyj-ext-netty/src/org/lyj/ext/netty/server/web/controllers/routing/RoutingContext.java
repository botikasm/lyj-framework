package org.lyj.ext.netty.server.web.controllers.routing;

import org.json.JSONObject;
import org.lyj.commons.util.*;
import org.lyj.ext.netty.server.web.*;

import java.util.Map;

/**
 * The routing context
 */
public class RoutingContext
        implements IHttpConstants {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerConfig _config;
    private final HttpServerRequest _request;
    private final HttpServerResponse _response;
    private final String _uri;
    private final HttpParams _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RoutingContext(final HttpServerConfig config,
                          final HttpServerRequest request,
                          final HttpServerResponse response) {
        _config = config;
        _request = request;
        _response = response;
        _uri = _request.uri();
        _params = new HttpParams(_request.params());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uri() {
        return _uri;
    }

    public String method() {
        return _request.method();
    }

    public RoutingContext handled(final boolean value) {
        _response.handled(value);
        return this;
    }

    public boolean handled() {
        return _response.handled();
    }

    public HttpParams params() {
        return _params;
    }

    public Map<String, String> headers() {
        return _response.headers();
    }

    public String getLang() {
        final String langCode = this.headers().get(ACCEPT_LANGUAGE);
        return null != langCode ? LocaleUtils.getLanguage(langCode) : LocaleUtils.getCurrent().getLanguage();
    }

    // ------------------------------------------------------------------------
    //                      p a r a m s   h e l p e r s
    // ------------------------------------------------------------------------

    public String getParam(final String paramName) {
        return this.getParam(paramName, "");
    }

    public String getParam(final String paramName, final String defVal) {
        String result = defVal;
        try {
            result = this.params().getString(paramName);
        } catch (Throwable ignored) {
            // ignored error
        }
        return StringUtils.hasText(result) ? result : defVal;
    }

    public boolean getParamBoolean(final String paramName) {
        return this.getParamBoolean(paramName, false);
    }

    public boolean getParamBoolean(final String paramName, final boolean defVal) {
        return ConversionUtils.toBoolean(this.getParam(paramName), defVal);
    }

    public int getParamInteger(final String paramName) {
        return this.getParamInteger(paramName, 0);
    }

    public int getParamInteger(final String paramName, final int defVal) {
        return ConversionUtils.toInteger(this.getParam(paramName), defVal);
    }

    public long getParamLong(final String paramName) {
        return this.getParamLong(paramName, 0);
    }

    public long getParamLong(final String paramName, final long defVal) {
        return ConversionUtils.toLong(this.getParam(paramName), defVal);
    }

    public double getParamDouble(final String paramName) {
        return this.getParamDouble(paramName, 0);
    }

    public double getParamDouble(final String paramName, final double defVal) {
        return ConversionUtils.toDouble(this.getParam(paramName), defVal);
    }

    // ------------------------------------------------------------------------
    //                      r e s p o n s e   h e a d e r s
    // ------------------------------------------------------------------------

    public void addHeader(final String name, final String value) {
        _response.headers().put(name, value);
    }

    public void removeHeader(final String name) {
        _response.headers().remove(name);
    }

    public boolean hasHeader(final String name) {
        return _response.headers().containsKey(name);
    }

    public String getHeader(final String name) {
        return _response.headers().get(name);
    }

    public void removeHeaderAccessControlAllowOrigin() {
        this.removeHeader(ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    public void addHeaderAccessControlAllowOriginAll() {
        this.addHeaderAccessControlAllowOrigin("*");
    }

    public void addHeaderAccessControlAllowOrigin(final String value) {
        this.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, value);
        this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS");
        this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "access-control-allow-origin, content-type, " +
                "Access-Control-Request-Method, Cache-Control, User-Agent, Origin, Connection, Accept-Encoding, content-length");
    }

    // ------------------------------------------------------------------------
    //                      w r i t e    r e s p o n s e   c o n t e n t
    // ------------------------------------------------------------------------

    public void write(final String content) {
        this.write(content, "");
    }

    public void writeInternalServerError(final Throwable t) {
        _response.writeErrorINTERNAL_SERVER_ERROR(t);
    }

    public void writeInternalServerError() {
        _response.writeErrorINTERNAL_SERVER_ERROR();
    }

    public void writeJsonError(final String error) {
        this.writeJson(validateJsonError(error));
    }

    public void writeJsonError(final Throwable error) {
        this.writeJson(validateJsonError(error));
    }

    public void writeErroMissingParams(final String... names) {
        this.writeJson(validateJsonError(new Exception("Bad Request, missing some parameters: " + CollectionUtils.toCommaDelimitedString(names))));
    }

    public void write(final String content,
                      final String content_type) {
        this.addCORSHeaders();

        _response.headers().put(CONTENT_TYPE,
                StringUtils.hasText(content_type) ? content_type : MimeTypeUtils.MIME_PLAINTEXT);
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    public void writeJson(final Object content) {
        this.addCORSHeaders();

        final String json = validateJson(content);
        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.MIME_JSON);
        _response.headers().put(CONTENT_LENGTH, json.length() + "");
        _response.write(json);
        _response.flush();
    }

    public void writeHtml(final String content) {
        this.addCORSHeaders();

        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.MIME_HTML);
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    public void writeXml(final String content) {
        this.addCORSHeaders();

        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.MIME_XML);
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    public void writeRedirect(final String newUri) {
        _response.writeRedirect(newUri);
    }


    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    void addParams(final Map<String, String> params) {
        if (null != params && params.size() > 0) {
            for (final Map.Entry<String, String> entry : params.entrySet()) {
                _params.put(entry.getKey(), entry.getValue());
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String validateJson(final Object text) {
        if (StringUtils.isJSON(text)) {
            return text.toString();
        } else {
            final JSONObject json = new JSONObject();
            json.putOpt("response", null != text ? text.toString() : "");
            return json.toString();
        }
    }

    private static String validateJsonError(final Throwable t) {
        return validateJsonError(t.toString());
    }

    private static String validateJsonError(final String text) {
        if (StringUtils.isJSON(text)) {
            return text;
        } else {
            final JSONObject json = new JSONObject();
            json.putOpt("error", text);
            return json.toString();
        }
    }

    private void addCORSHeaders() {
        final String allowed_origins = _config.corsAllowOrigin();
        if (StringUtils.hasText(allowed_origins)) {
            this.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowed_origins);
            if (allowed_origins.equals("*")) {
                if (StringUtils.hasText(_config.corsAllowMethods())) {
                    this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, _config.corsAllowMethods());
                } else {
                    final String method =  _request.headerValue(ACCESS_CONTROL_REQUEST_METHOD);
                    if(null!=method){
                        this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, _request.headerValue(ACCESS_CONTROL_REQUEST_METHOD));
                    } else {
                        this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, DELETE, PUT");
                    }
                }
                if (StringUtils.hasText(_config.corsAllowHeaders())) {
                    this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, _config.corsAllowHeaders());
                } else {
                    final String headers = _request.headerValue(ACCESS_CONTROL_REQUEST_HEADERS);
                    if(null!=headers){
                        this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, _request.headerValue(ACCESS_CONTROL_REQUEST_HEADERS));
                    } else {
                        this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, StringUtils.toString(_request.headerNames()));
                    }
                }
            } else {
                this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, _config.corsAllowMethods());
                this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, _config.corsAllowHeaders());
            }
        }
    }

}
