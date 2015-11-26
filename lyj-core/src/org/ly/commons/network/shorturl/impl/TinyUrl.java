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
package org.ly.commons.network.shorturl.impl;

import org.ly.commons.network.URLUtils;
import org.ly.commons.network.shorturl.IURLShortener;
import org.ly.commons.util.FormatUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author angelo.geminiani
 */
public class TinyUrl
        implements IURLShortener {

    private static final String API_URL = "http://tinyurl.com/api-create.php?url={url}";

    @Override
    public String getShortUrl(final String url) throws Exception {
        return this.get(url);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private String get(final String surl) throws Exception {
        final String solvedurl = this.solve(surl);
        final String response = URLUtils.getUrlContent(solvedurl);
        return response.trim();
    }

    private String solve(final String url) {
        final Map<String, String> data = new HashMap<String, String>();
        data.put("url", url);
        return FormatUtils.formatTemplate(API_URL, "{", "}", data);
    }
}
