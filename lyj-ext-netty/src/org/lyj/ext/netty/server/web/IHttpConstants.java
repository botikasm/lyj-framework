package org.lyj.ext.netty.server.web;

import java.util.regex.Pattern;

/**
 * constants
 */
public interface IHttpConstants {


    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    // ------------------------------------------------------------------------
    //                      reg exp
    // ------------------------------------------------------------------------

    public static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    public static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

}
