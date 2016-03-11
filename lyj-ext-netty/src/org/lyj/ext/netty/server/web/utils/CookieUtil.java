package org.lyj.ext.netty.server.web.utils;


import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.lyj.commons.util.StringUtils;

import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

/**
 * Cookie helper
 */
public class CookieUtil {

    public static ServerCookieDecoder decoder(final boolean strict) {
        return strict ? ServerCookieDecoder.STRICT : ServerCookieDecoder.LAX;
    }

    public static ServerCookieEncoder encoder(final boolean strict) {
        return strict ? ServerCookieEncoder.STRICT : ServerCookieEncoder.LAX;
    }

    public static void encodeCookies(final HttpRequest request,
                                     final HttpResponse response) {
        encodeCookies(request, response, false);
    }

    public static void encodeCookies(final HttpRequest request,
                                     final HttpResponse response,
                                     final boolean strict) {
        String cookieString = request.headers().get(COOKIE);
        if (StringUtils.hasText(cookieString)) {
            encodeCookies(cookieString, response, strict);
        }
    }

    public static void encodeCookies(final String cookieString,
                                     final HttpResponse response) {
        encodeCookies(cookieString, response, false);
    }
    public static void encodeCookies(final String cookieString,
                                     final HttpResponse response,
                                     final boolean strict) {
        final Set<Cookie> cookies = decoder(strict).decode(cookieString);
        if (!cookies.isEmpty()) {
            // Reset the cookies if necessary.
            for (io.netty.handler.codec.http.cookie.Cookie cookie : cookies) {
                response.headers().add(SET_COOKIE, encoder(strict).encode(cookie));
            }
        }
    }

    public static void addCookie(final HttpResponse response, final String key, final String value){
        addCookie(response, key, value, false);
    }

    public static void addCookie(final HttpResponse response, final String key, final String value, final boolean strict){
        response.headers().add(SET_COOKIE, encoder(strict).encode(key, value));
    }

}
