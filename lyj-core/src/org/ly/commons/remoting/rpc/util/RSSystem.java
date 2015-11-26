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
package org.ly.commons.remoting.rpc.util;

import org.json.JSONObject;
import org.ly.Smartly;
import org.ly.commons.cryptograph.GUID;
import org.ly.commons.network.shorturl.impl.TinyUrl;
import org.ly.commons.util.ConversionUtils;
import org.ly.commons.util.RandomUtils;
import org.ly.commons.util.StringUtils;
import org.ly.commons.remoting.rpc.RemoteService;

/**
 * system REST service
 * <p/>
 * http://localhost/rest/system/random?param1=rO0ABXQAOTIvQkVFaW5nLzN8YWRtaW5pc3RyYXRvcnwyMDBDRUIyNjgwN0Q2QkY5OUZENkY0RjBE%0AMUNBNTRENA%3D%3D&param2=6
 *
 * @author angelo.geminiani
 */
public class RSSystem
        extends RemoteService {

    public static final String NAME = "system";

    public RSSystem() {
        super(NAME);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Object getSettings(final String authToken) throws Exception {
        if (super.isValidToken(authToken)) {
            return Smartly.getConfiguration();
        }
        return new JSONObject();
    }

    public boolean isConnected() {
        return true;
    }

    public String shortURL(final String authToken,
                           final String url) throws Exception {
        if (super.isValidToken(authToken)) {
            final TinyUrl tiny = new TinyUrl();
            return tiny.getShortUrl(url);
        }
        return "";
    }

    /**
     * Random service. enerates random alphanumeric value <br/>
     * USAGE: <br/>
     * http://localhost/rest/system/random?param1=rO0ABXQAOTIvQkVFaW5nLzN8YWRtaW5pc3RyYXRvcnwyMDBDRUIyNjgwN0Q2QkY5OUZENkY0RjBE%0AMUNBNTRENA%3D%3D&param2=6
     *
     * @param authToken
     * @param size      (Optional) size of returned code
     * @return
     */
    public String random(final String authToken,
                         final String size) {
        if (super.isValidToken(authToken)) {
            final char[] chars = "abcdefghilmnopqrstuvzxywjk0123456789".toCharArray();
            if (StringUtils.hasText(size)) {
                return RandomUtils.random(ConversionUtils.toInteger(size),
                        RandomUtils.CHARS_LOW_NUMBERS);
            } else {
                return RandomUtils.random(6,
                        RandomUtils.CHARS_LOW_NUMBERS);
            }
        }
        return "";
    }

    /**
     * Generates and returns a GUID
     *
     * @param authToken
     * @return
     */
    public String guid(final String authToken) {
        if (super.isValidToken(authToken)) {
            return GUID.create(true);
        }
        return "";
    }


}
