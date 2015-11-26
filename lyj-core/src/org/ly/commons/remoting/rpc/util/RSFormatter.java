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

import org.ly.commons.logging.Level;
import org.ly.commons.util.DateWrapper;
import org.ly.commons.util.LocaleUtils;
import org.ly.commons.remoting.rpc.RemoteService;

import java.util.Locale;

/**
 * system REST service
 * <p/>
 * http://localhost/rest/formatter/format?param1=rO0ABXQAOTIvQkVFaW5nLzN8YWRtaW5pc3RyYXRvcnwyMDBDRUIyNjgwN0Q2QkY5OUZENkY0RjBE%0AMUNBNTRENA%3D%3D&param2=IT&param3=19680121
 *
 * @author angelo.geminiani
 */
public class RSFormatter
        extends RemoteService {

    public static final String NAME = "formatter";

    public RSFormatter() {
        super(NAME);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Format a date passed in iso (yyyyMMdd) format.
     *
     * @param authToken
     * @param country   i.e. "IT"
     * @param isoDate   (yyyyMMdd)
     * @return Formatted Date. i.e. "21/01/1968"
     */
    public String formatDate(final String authToken,
                             final String country,
                             final String isoDate) {
        if (super.isValidToken(authToken)) {
            try {
                final Locale locale = LocaleUtils.getLocaleByCountry(country);
                final DateWrapper dt = new DateWrapper(isoDate, DateWrapper.DATEFORMAT_DEFAULT);
                return dt.toString(locale);
            } catch (Throwable t) {
                super.getLogger().log(Level.SEVERE, null, t);
            }
        }
        return "";
    }
}
