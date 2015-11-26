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
package org.ly.commons.network.iplocation;

import org.ly.commons.network.URLUtils;
import org.ly.commons.util.FormatUtils;

/**
 * @author angelo.geminiani
 */
public final class IPLocator {

    private static final String URL_PATTERN = "http://api.hostip.info/get_html.php?ip={0}&position=true";

    private IPLocator() {
    }

    public IPLocation locate(final String ip) throws Exception {
        final String url = FormatUtils.format(URL_PATTERN, ip);
        final String response = URLUtils.getUrlContent(url);
        return new IPLocation(response);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static IPLocator __instance;

    public static IPLocator getInstance() {
        if (null == __instance) {
            __instance = new IPLocator();
        }
        return __instance;
    }
}
