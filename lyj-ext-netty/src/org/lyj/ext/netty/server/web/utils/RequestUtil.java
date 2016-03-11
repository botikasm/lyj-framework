package org.lyj.ext.netty.server.web.utils;

import io.netty.handler.codec.http.*;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;

import java.net.URLDecoder;
import java.util.*;

/**
 * Request utility
 */
public class RequestUtil {


    public static String host(final HttpRequest request) {
        if (null != request && !request.headers().isEmpty()) {
            return request.headers().get(HttpHeaderNames.HOST, "unknown");
        }
        return "";
    }

    public static HttpVersion protocolVersion(final HttpRequest request) {
        if (null != request) {
            return request.protocolVersion();
        }
        return HttpVersion.HTTP_1_0;
    }

    public static String uri(final HttpRequest request) {
        if (null != request) {
            return request.uri();
        }
        return "";
    }

    public static Set<String> headerNames(final HttpRequest request) {
        if (null != request && !request.headers().isEmpty()) {
            return request.headers().names();
        }
        return new HashSet<>();
    }

    public static String headerValue(final HttpRequest request, final String name) {
        if (null != request && !request.headers().isEmpty()) {
            return request.headers().get(name);
        }
        return "";
    }

    public static QueryStringDecoder query(final HttpRequest request) {
        if (null != request) {
            return new QueryStringDecoder(request.uri());
        }
        return new QueryStringDecoder("");
    }

    public static Map<String, List<String>> queryParams(final HttpRequest request) {
        if (null != request) {
            return query(request).parameters();
        }
        return new HashMap<>();
    }

    public static List<String> queryParam(final HttpRequest request, final String paramName) {
        if (null != request) {
            return query(request).parameters().get(paramName);
        }
        return new ArrayList<>();
    }

    /**
     * HEADER: Connection = keep-alive
     */
    public boolean keepAlive(final HttpRequest request) {
        String value = headerValue(request, "Connection");
        if (!StringUtils.hasText(value)) {
            value = headerValue(request, "connection");
            if (!StringUtils.hasText(value)) {
                value = headerValue(request, "CONNECTION");
            } else {
                value = "";
            }
        }
        return value.toLowerCase().equals("keep-alive");
    }

    /**
     * HEADER: Cache-Control = max-age=0
     */
    public int cacheControl(final HttpRequest request) {
        String value = headerValue(request, "Cache-Control");
        if (!StringUtils.hasText(value)) {
            value = headerValue(request, "cache-control");
            if (!StringUtils.hasText(value)) {
                value = headerValue(request, "CACHE-CONTROL");
            } else {
                value = "";
            }
        }
        if (StringUtils.hasText(value)) {
            final String[] tokens = StringUtils.split(value, "=", true);
            if (tokens.length == 2) {
                return ConversionUtils.toInteger(tokens[1]);
            }
        }
        return 0;
    }

    /**
     * HEADER: Content-Length = 1234
     */
    public int contentLength(final HttpRequest request) {
        String value = headerValue(request, "Content-Length");
        if (!StringUtils.hasText(value)) {
            value = headerValue(request, "content-length");
            if (!StringUtils.hasText(value)) {
                value = headerValue(request, "CONTENT-LENGHT");
            } else {
                value = "";
            }
        }

        return ConversionUtils.toInteger(value);
    }

    public Map<String, String> content(final Object request, final String charEncoding) {
        final HashMap<String, String> result = new HashMap<>();
        if(request instanceof HttpContent || request instanceof String){
            final String content = request.toString();
            try{
                final String[] tokens = StringUtils.split(content, "&", true);
                for(final String token:tokens){
                    final String[] keyvalue = StringUtils.split(token, "=", true);
                    if(keyvalue.length==2){
                        final String key = URLDecoder.decode(keyvalue[0], charEncoding);
                        final String value = URLDecoder.decode(keyvalue[1], charEncoding);
                        result.put(key, value);
                    }
                }
            }catch(Throwable ignored){
            }
        }

        return result;
    }

}
