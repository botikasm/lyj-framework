/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * 
 */
package org.ly.commons.remoting.rest;

import org.ly.Smartly;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.mutex.Mutex;
import org.ly.commons.mutex.MutexPool;
import org.ly.commons.remoting.Remoting;
import org.ly.commons.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Abstract proxy REST service.
 *
 * @author angelo.geminiani
 */
public abstract class RESTService {

    private final MutexPool _mutexPool;

    private String _token; // security Token

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------

    public RESTService() {
        _mutexPool = new MutexPool();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _mutexPool.close();
        } finally {
            super.finalize();
        }
    }
// ------------------------------------------------------------------------
    //                      Public
    // ------------------------------------------------------------------------

    public String getToken() {
        return _token;
    }

    public void setToken(String token) {
        this._token = token;
    }

    public boolean isValidToken(final String authToken) {
        if (StringUtils.hasText(authToken)) {
            return this.validateToken(authToken);
        } else if (StringUtils.hasText(_token)) {
            return this.validateToken(_token);
        }
        return false;
    }

    public final Mutex getMutex(final Object key) {
        return _mutexPool.get(key);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------
    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    //
    private boolean validateToken(final String token) {
        if (StringUtils.hasText(token)) {
            final String smartlyToken = getAppToken();
            if (token.equalsIgnoreCase(smartlyToken)) {
                return true;
            } else {
                final String enc1 = decodeWhile(token);
                final String enc2 = decodeWhile(smartlyToken);
                return enc1.equalsIgnoreCase(enc2);
            }
        }
        return false;
    }

    public String decode(final String s) {
        try {
            return URLDecoder.decode(s, Smartly.getCharset());
        } catch (UnsupportedEncodingException ignored) {
        }
        return s;
    }

    private String decodeWhile(final String s) {
        String result = s;
        try {
            result = URLDecoder.decode(result, Smartly.getCharset());
            while (result.contains("%")) {
                try {
                    result = URLDecoder.decode(result, Smartly.getCharset());
                } catch (Throwable ignored) {
                    break;
                }
            }
        } catch (UnsupportedEncodingException ignored) {
        }
        return result;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static String __APP_TOKEN = null;

    private static String getAppToken() {
        if (null == __APP_TOKEN) {
            __APP_TOKEN = Remoting.getAppToken();
        }
        return null != __APP_TOKEN ? __APP_TOKEN : "";
    }

    public static void setAppToken(final String token){
         __APP_TOKEN = token;
    }
}
