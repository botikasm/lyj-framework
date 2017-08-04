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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lyj.commons.cryptograph;

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

/**
 * @author angelo.geminiani
 */
public class MD5 {

    private MD5() {
    }

    public static String encode(final String text,
                                final String opvalue) {
        try {
            final SecurityMessageDigester instance = new SecurityMessageDigester(
                    ICryptographConstants.AlgorithmMessageDigest.MD5);
            return instance.getEncodedText(text);
        } catch (Throwable t) {
            return opvalue;
        }
    }

    public static String encode(final String text) {
        return encode(text, "");
    }


    public static String encode(final String[] tokens) {
        return encode(tokens, false);
    }

    public static String encode(final String[] tokens, final boolean sort_items) {
        final String[] list = sort_items ? CollectionUtils.sortStringArrayCopy(tokens) : tokens;
        return encode(StringUtils.toString(list, ""));
    }

    public static String encodeIgnoreCase(final String[] tokens) {
        return encodeIgnoreCase(tokens, false);
    }

    public static String encodeIgnoreCase(final String[] tokens, final boolean sort_items) {
        final String[] list = sort_items ? CollectionUtils.sortStringArrayCopyToLowerCase(tokens) : tokens;
        return encode(StringUtils.toString(list, ""));
    }

}
