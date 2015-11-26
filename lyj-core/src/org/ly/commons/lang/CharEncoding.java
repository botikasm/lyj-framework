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

package org.ly.commons.lang;


import org.ly.IConstants;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class CharEncoding {

    /**
     * <p>
     * ISO Latin Alphabet #1, also known as ISO-LATIN-1.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *      encoding names</a>
     */
    public static final String ISO_8859_1 = "ISO-8859-1";

    /**
     * <p>
     * Seven-bit ASCII, also known as ISO646-US, also known as the Basic Latin block of the Unicode character set.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *      encoding names</a>
     */
    public static final String US_ASCII = "US-ASCII";

    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, byte order specified by a mandatory initial byte-order mark (either
     * order accepted on input, big-endian used on output).
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *      encoding names</a>
     */
    public static final String UTF_16 = "UTF-16";

    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, big-endian byte order.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *      encoding names</a>
     */
    public static final String UTF_16BE = "UTF-16BE";

    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, little-endian byte order.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *      encoding names</a>
     */
    public static final String UTF_16LE = "UTF-16LE";

    /**
     * <p>
     * Eight-bit Unicode Transformation Format.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     *
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *      encoding names</a>
     */
    public static final String UTF_8 = "UTF-8";

    private CharEncoding() {
    }

    /**
     * <p>
     * Returns whether the named charset is supported.
     * </p>
     * <p>
     * This is similar to <a
     * href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html#isSupported(java.lang.String)">
     * java.nio.charset.Charset.isSupported(String)</a>
     * </p>
     *
     * @param name the name of the requested charset; may be either a canonical name or an alias
     * @return <code>true</code> if, and only if, support for the named charset is available in the current Java
     *         virtual machine
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *      encoding names</a>
     */
    public static boolean isSupported(final String name) {
        if (name == null || name.length() == 0) {
            return false;
        }
        try {
            new String(new byte[0], name);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return true;
    }

    private static String __DEFAULT = null;

    /**
     * <p>
     * Returns default charset encoding.
     * </p>
     * <p>
     * First step is looking into "charset" system property.<br/>
     * Second check whether UTF-16 charset is supported.<br/>
     * If neither system property is setted or supported, nor UTF-16, returns UTF-8 as default.
     * </p>
     *
     * @return Default charset encoding.
     */
    public static String getDefault() {
        if (null == __DEFAULT) {
            final String sys = System.getProperty(IConstants.SYSPROP_CHARSET);
            if (null != sys && sys.length() > 0 && isSupported(sys)) {
                __DEFAULT = sys;
            } else if (isSupported(CharEncoding.UTF_8)) {
                __DEFAULT = CharEncoding.UTF_8;
            } else {
                __DEFAULT = CharEncoding.ISO_8859_1;
            }
        }
        return __DEFAULT;
    }

    public static Charset getDefaultCharset(){
        try{
           final Charset result = Charset.forName(getDefault());
            if(null==result){
                throw  new UnsupportedCharsetException(getDefault());
            } else {
                return result;
            }
        } catch(Throwable ignored){
            final Charset result = Charset.defaultCharset();
            setDefault(result.name());
            return result;
        }
    }

    public static void setDefault(final String charset) {
        if (isSupported(charset)) {
            __DEFAULT = charset;
            System.setProperty(IConstants.SYSPROP_CHARSET, charset);
        }
    }
}
