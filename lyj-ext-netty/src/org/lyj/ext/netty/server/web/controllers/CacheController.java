package org.lyj.ext.netty.server.web.controllers;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.IHeaderNames;
import org.lyj.ext.netty.server.web.IHttpConstants;
import org.lyj.ext.netty.server.web.utils.ResponseUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.DATE;

/**
 * manage cache.
 */
public class CacheController {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerConfig _config;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CacheController(final HttpServerConfig config) {
        _config = config;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isModifiedSince(final HttpRequest request,
                                   final File file) {
        return this.isModifiedSince(request, file.lastModified());
    }

    public boolean isModifiedSince(final HttpRequest request,
                                   final long lastModified) {
        try {
            // Cache Validation
            final String ifModifiedSince = request.headers().get(IHeaderNames.IF_MODIFIED_SINCE);
            if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(IHttpConstants.HTTP_DATE_FORMAT, Locale.US);
                Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

                // Only compare up to the second because the datetime format we send to the client
                // does not have milliseconds
                long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
                long fileLastModifiedSeconds = lastModified / 1000;
                if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                    return false; //ResponseUtil.sendNotModified(ctx);
                }
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    public void setDateAndCacheHeaders(final HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(IHttpConstants.HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(IHttpConstants.HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(IHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, _config.cacheSeconds());
        response.headers().set(IHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(IHeaderNames.CACHE_CONTROL, "private, max-age=" + _config.cacheSeconds());
        response.headers().set(
                IHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    public void setDateHeader(final FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(IHttpConstants.HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(IHttpConstants.HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));
    }


}
