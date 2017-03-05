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

package org.lyj.commons.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

/**
 * <p>Escapes and unescapes <code>String</code>s for
 * Java, Java Script, and SQL.</p>
 */
public class StringEscapeUtils {

    private static final char CSV_DELIMITER = ',';
    private static final char CSV_QUOTE = '"';
    private static final String CSV_QUOTE_STR = String.valueOf(CSV_QUOTE);
    private static final char[] CSV_SEARCH_CHARS = new char[]{CSV_DELIMITER, CSV_QUOTE, StringUtils.CR, StringUtils.LF};

    /**
     * <p><code>StringEscapeUtils</code> instances should NOT be constructed in
     * standard programming.</p>
     * <p/>
     * <p>Instead, the class should be used as:
     * <pre>StringEscapeUtils.escapeJava("foo");</pre></p>
     * <p/>
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public StringEscapeUtils() {
        super();
    }

    private static final int MIN_ESCAPE = 2;
    private static final int MAX_ESCAPE = 6;
    private static final String[][] ESCAPES = {
            {"\"",     "quot"}, // " - double-quote
            {"&",      "amp"}, // & - ampersand
            {"<",      "lt"}, // < - less-than
            {">",      "gt"}, // > - greater-than

            // Mapping to escape ISO-8859-1 characters to their named HTML 3.x equivalents.
            {"\u00A0", "nbsp"}, // non-breaking space
            {"\u00A1", "iexcl"}, // inverted exclamation mark
            {"\u00A2", "cent"}, // cent sign
            {"\u00A3", "pound"}, // pound sign
            {"\u00A4", "curren"}, // currency sign
            {"\u00A5", "yen"}, // yen sign = yuan sign
            {"\u00A6", "brvbar"}, // broken bar = broken vertical bar
            {"\u00A7", "sect"}, // section sign
            {"\u00A8", "uml"}, // diaeresis = spacing diaeresis
            {"\u00A9", "copy"}, // © - copyright sign
            {"\u00AA", "ordf"}, // feminine ordinal indicator
            {"\u00AB", "laquo"}, // left-pointing double angle quotation mark = left pointing guillemet
            {"\u00AC", "not"}, // not sign
            {"\u00AD", "shy"}, // soft hyphen = discretionary hyphen
            {"\u00AE", "reg"}, // ® - registered trademark sign
            {"\u00AF", "macr"}, // macron = spacing macron = overline = APL overbar
            {"\u00B0", "deg"}, // degree sign
            {"\u00B1", "plusmn"}, // plus-minus sign = plus-or-minus sign
            {"\u00B2", "sup2"}, // superscript two = superscript digit two = squared
            {"\u00B3", "sup3"}, // superscript three = superscript digit three = cubed
            {"\u00B4", "acute"}, // acute accent = spacing acute
            {"\u00B5", "micro"}, // micro sign
            {"\u00B6", "para"}, // pilcrow sign = paragraph sign
            {"\u00B7", "middot"}, // middle dot = Georgian comma = Greek middle dot
            {"\u00B8", "cedil"}, // cedilla = spacing cedilla
            {"\u00B9", "sup1"}, // superscript one = superscript digit one
            {"\u00BA", "ordm"}, // masculine ordinal indicator
            {"\u00BB", "raquo"}, // right-pointing double angle quotation mark = right pointing guillemet
            {"\u00BC", "frac14"}, // vulgar fraction one quarter = fraction one quarter
            {"\u00BD", "frac12"}, // vulgar fraction one half = fraction one half
            {"\u00BE", "frac34"}, // vulgar fraction three quarters = fraction three quarters
            {"\u00BF", "iquest"}, // inverted question mark = turned question mark
            {"\u00C0", "Agrave"}, // А - uppercase A, grave accent
            {"\u00C1", "Aacute"}, // Б - uppercase A, acute accent
            {"\u00C2", "Acirc"}, // В - uppercase A, circumflex accent
            {"\u00C3", "Atilde"}, // Г - uppercase A, tilde
            {"\u00C4", "Auml"}, // Д - uppercase A, umlaut
            {"\u00C5", "Aring"}, // Е - uppercase A, ring
            {"\u00C6", "AElig"}, // Ж - uppercase AE
            {"\u00C7", "Ccedil"}, // З - uppercase C, cedilla
            {"\u00C8", "Egrave"}, // И - uppercase E, grave accent
            {"\u00C9", "Eacute"}, // Й - uppercase E, acute accent
            {"\u00CA", "Ecirc"}, // К - uppercase E, circumflex accent
            {"\u00CB", "Euml"}, // Л - uppercase E, umlaut
            {"\u00CC", "Igrave"}, // М - uppercase I, grave accent
            {"\u00CD", "Iacute"}, // Н - uppercase I, acute accent
            {"\u00CE", "Icirc"}, // О - uppercase I, circumflex accent
            {"\u00CF", "Iuml"}, // П - uppercase I, umlaut
            {"\u00D0", "ETH"}, // Р - uppercase Eth, Icelandic
            {"\u00D1", "Ntilde"}, // С - uppercase N, tilde
            {"\u00D2", "Ograve"}, // Т - uppercase O, grave accent
            {"\u00D3", "Oacute"}, // У - uppercase O, acute accent
            {"\u00D4", "Ocirc"}, // Ф - uppercase O, circumflex accent
            {"\u00D5", "Otilde"}, // Х - uppercase O, tilde
            {"\u00D6", "Ouml"}, // Ц - uppercase O, umlaut
            {"\u00D7", "times"}, // multiplication sign
            {"\u00D8", "Oslash"}, // Ш - uppercase O, slash
            {"\u00D9", "Ugrave"}, // Щ - uppercase U, grave accent
            {"\u00DA", "Uacute"}, // Ъ - uppercase U, acute accent
            {"\u00DB", "Ucirc"}, // Ы - uppercase U, circumflex accent
            {"\u00DC", "Uuml"}, // Ь - uppercase U, umlaut
            {"\u00DD", "Yacute"}, // Э - uppercase Y, acute accent
            {"\u00DE", "THORN"}, // Ю - uppercase THORN, Icelandic
            {"\u00DF", "szlig"}, // Я - lowercase sharps, German
            {"\u00E0", "agrave"}, // а - lowercase a, grave accent
            {"\u00E1", "aacute"}, // б - lowercase a, acute accent
            {"\u00E2", "acirc"}, // в - lowercase a, circumflex accent
            {"\u00E3", "atilde"}, // г - lowercase a, tilde
            {"\u00E4", "auml"}, // д - lowercase a, umlaut
            {"\u00E5", "aring"}, // е - lowercase a, ring
            {"\u00E6", "aelig"}, // ж - lowercase ae
            {"\u00E7", "ccedil"}, // з - lowercase c, cedilla
            {"\u00E8", "egrave"}, // и - lowercase e, grave accent
            {"\u00E9", "eacute"}, // й - lowercase e, acute accent
            {"\u00EA", "ecirc"}, // к - lowercase e, circumflex accent
            {"\u00EB", "euml"}, // л - lowercase e, umlaut
            {"\u00EC", "igrave"}, // м - lowercase i, grave accent
            {"\u00ED", "iacute"}, // н - lowercase i, acute accent
            {"\u00EE", "icirc"}, // о - lowercase i, circumflex accent
            {"\u00EF", "iuml"}, // п - lowercase i, umlaut
            {"\u00F0", "eth"}, // р - lowercase eth, Icelandic
            {"\u00F1", "ntilde"}, // с - lowercase n, tilde
            {"\u00F2", "ograve"}, // т - lowercase o, grave accent
            {"\u00F3", "oacute"}, // у - lowercase o, acute accent
            {"\u00F4", "ocirc"}, // ф - lowercase o, circumflex accent
            {"\u00F5", "otilde"}, // х - lowercase o, tilde
            {"\u00F6", "ouml"}, // ц - lowercase o, umlaut
            {"\u00F7", "divide"}, // division sign
            {"\u00F8", "oslash"}, // ш - lowercase o, slash
            {"\u00F9", "ugrave"}, // щ - lowercase u, grave accent
            {"\u00FA", "uacute"}, // ъ - lowercase u, acute accent
            {"\u00FB", "ucirc"}, // ы - lowercase u, circumflex accent
            {"\u00FC", "uuml"}, // ь - lowercase u, umlaut
            {"\u00FD", "yacute"}, // э - lowercase y, acute accent
            {"\u00FE", "thorn"}, // ю - lowercase thorn, Icelandic
            {"\u00FF", "yuml"}, // я - lowercase y, umlaut
    };

    private static final HashMap<String, CharSequence> lookupMap;
    static {
        lookupMap = new HashMap<String, CharSequence>();
        for (final CharSequence[] seq : ESCAPES)
            lookupMap.put(seq[1].toString(), seq[0]);
    }

    // Java and JavaScript
    //--------------------------------------------------------------------------

    /**
     * <p>Escapes the characters in a <code>String</code> using Java String rules.</p>
     * <p/>
     * <p>Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
     * <p/>
     * <p>So a tab becomes the characters <code>'\\'</code> and
     * <code>'t'</code>.</p>
     * <p/>
     * <p>The only difference between Java strings and JavaScript strings
     * is that in JavaScript, a single quote must be escaped.</p>
     * <p/>
     * <p>Example:
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn't say, \"Stop!\"
     * </pre>
     * </p>
     *
     * @param str String to escape values in, may be null
     * @return String with escaped values, <code>null</code> if null string input
     */
    public static String escapeJava(String str) {
        return escapeJavaStyleString(str, false);
    }

    /**
     * <p>Escapes the characters in a <code>String</code> using Java String rules to
     * a <code>Writer</code>.</p>
     * <p/>
     * <p>A <code>null</code> string input has no effect.</p>
     *
     * @param out Writer to write escaped string into
     * @param str String to escape values in, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException      if error occurs on underlying Writer
     * @see #escapeJava(String)
     */
    public static void escapeJava(Writer out, String str) throws IOException {
        escapeJavaStyleString(out, str, false);
    }

    /**
     * <p>Escapes the characters in a <code>String</code> using JavaScript String rules.</p>
     * <p>Escapes any values it finds into their JavaScript String form.
     * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
     * <p/>
     * <p>So a tab becomes the characters <code>'\\'</code> and
     * <code>'t'</code>.</p>
     * <p/>
     * <p>The only difference between Java strings and JavaScript strings
     * is that in JavaScript, a single quote must be escaped.</p>
     * <p/>
     * <p>Example:
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn\'t say, \"Stop!\"
     * </pre>
     * </p>
     *
     * @param str String to escape values in, may be null
     * @return String with escaped values, <code>null</code> if null string input
     */
    public static String escapeJavaScript(String str) {
        return escapeJavaStyleString(str, true);
    }

    /**
     * <p>Escapes the characters in a <code>String</code> using JavaScript String rules
     * to a <code>Writer</code>.</p>
     * <p/>
     * <p>A <code>null</code> string input has no effect.</p>
     *
     * @param out Writer to write escaped string into
     * @param str String to escape values in, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException              if error occurs on underlying Writer
     * @see #escapeJavaScript(String)
     */
    public static void escapeJavaScript(Writer out, String str) throws IOException {
        escapeJavaStyleString(out, str, true);
    }


    /**
     * <p>Unescapes any Java literals found in the <code>String</code>.
     * For example, it will turn a sequence of <code>'\'</code> and
     * <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.</p>
     *
     * @param str the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     */
    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * <p>Unescapes any Java literals found in the <code>String</code> to a
     * <code>Writer</code>.</p>
     * <p/>
     * <p>For example, it will turn a sequence of <code>'\'</code> and
     * <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.</p>
     * <p/>
     * <p>A <code>null</code> string input has no effect.</p>
     *
     * @param out the <code>Writer</code> used to output unescaped characters
     * @param str the <code>String</code> to unescape, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException              if error occurs on underlying Writer
     */
    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        out.write('\\');
                        break;
                    case '\'':
                        out.write('\'');
                        break;
                    case '\"':
                        out.write('"');
                        break;
                    case 'r':
                        out.write('\r');
                        break;
                    case 'f':
                        out.write('\f');
                        break;
                    case 't':
                        out.write('\t');
                        break;
                    case 'n':
                        out.write('\n');
                        break;
                    case 'b':
                        out.write('\b');
                        break;
                    case 'u': {
                        // uh-oh, we're in unicode country....
                        inUnicode = true;
                        break;
                    }
                    default:
                        out.write(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write('\\');
        }
    }

    /**
     * <p>Unescapes any JavaScript literals found in the <code>String</code>.</p>
     * <p/>
     * <p>For example, it will turn a sequence of <code>'\'</code> and <code>'n'</code>
     * into a newline character, unless the <code>'\'</code> is preceded by another
     * <code>'\'</code>.</p>
     *
     * @param str the <code>String</code> to unescape, may be null
     * @return A new unescaped <code>String</code>, <code>null</code> if null string input
     * @see #unescapeJava(String)
     */
    public static String unescapeJavaScript(String str) {
        return unescapeJava(str);
    }

    /**
     * <p>Unescapes any JavaScript literals found in the <code>String</code> to a
     * <code>Writer</code>.</p>
     * <p/>
     * <p>For example, it will turn a sequence of <code>'\'</code> and <code>'n'</code>
     * into a newline character, unless the <code>'\'</code> is preceded by another
     * <code>'\'</code>.</p>
     * <p/>
     * <p>A <code>null</code> string input has no effect.</p>
     *
     * @param out the <code>Writer</code> used to output unescaped characters
     * @param str the <code>String</code> to unescape, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException              if error occurs on underlying Writer
     * @see #unescapeJava(Writer, String)
     */
    public static void unescapeJavaScript(Writer out, String str) throws IOException {
        unescapeJava(out, str);
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Escapes the characters in a <code>String</code> to be suitable to pass to
     * an SQL query.</p>
     * <p/>
     * <p>For example,
     * <pre>statement.executeQuery("SELECT * FROM MOVIES WHERE TITLE='" +
     *   StringEscapeUtils.escapeSql("McHale's Navy") +
     *   "'");</pre>
     * </p>
     * <p/>
     * <p>At present, this method only turns single-quotes into doubled single-quotes
     * (<code>"McHale's Navy"</code> => <code>"McHale''s Navy"</code>). It does not
     * handle the cases of percent (%) or underscore (_) for use in LIKE clauses.</p>
     * <p/>
     * see http://www.jguru.com/faq/view.jsp?EID=8881
     *
     * @param str the string to escape, may be null
     * @return a new String, escaped for SQL, <code>null</code> if null string input
     */
    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.replace(str, "'", "''");
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Returns a <code>String</code> value for a CSV column enclosed in double quotes,
     * if required.</p>
     * <p/>
     * <p>If the value contains a comma, newline or double quote, then the
     * String value is returned enclosed in double quotes.</p>
     * </p>
     * <p/>
     * <p>Any double quote characters in the value are escaped with another double quote.</p>
     * <p/>
     * <p>If the value does not contain a comma, newline or double quote, then the
     * String value is returned unchanged.</p>
     * </p>
     * <p/>
     * see <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Wikipedia</a> and
     * <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     *
     * @param str the input CSV column String, may be null
     * @return the input String, enclosed in double quotes if the value contains a comma,
     *         newline or double quote, <code>null</code> if null string input
     * @since 2.4
     */
    public static String escapeCsv(String str) {
        if (StringUtils.containsNone(str, CSV_SEARCH_CHARS)) {
            return str;
        }
        try {
            StringWriter writer = new StringWriter();
            escapeCsv(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * <p>Writes a <code>String</code> value for a CSV column enclosed in double quotes,
     * if required.</p>
     * <p/>
     * <p>If the value contains a comma, newline or double quote, then the
     * String value is written enclosed in double quotes.</p>
     * </p>
     * <p/>
     * <p>Any double quote characters in the value are escaped with another double quote.</p>
     * <p/>
     * <p>If the value does not contain a comma, newline or double quote, then the
     * String value is written unchanged (null values are ignored).</p>
     * </p>
     * <p/>
     * see <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Wikipedia</a> and
     * <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     *
     * @param str the input CSV column String, may be null
     * @param out Writer to write input string to, enclosed in double quotes if it contains
     *            a comma, newline or double quote
     * @throws IOException if error occurs on underlying Writer
     * @since 2.4
     */
    public static void escapeCsv(Writer out, String str) throws IOException {
        if (StringUtils.containsNone(str, CSV_SEARCH_CHARS)) {
            if (str != null) {
                out.write(str);
            }
            return;
        }
        out.write(CSV_QUOTE);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == CSV_QUOTE) {
                out.write(CSV_QUOTE); // escape double quote
            }
            out.write(c);
        }
        out.write(CSV_QUOTE);
    }

    /**
     * <p>Returns a <code>String</code> value for an unescaped CSV column. </p>
     * <p/>
     * <p>If the value is enclosed in double quotes, and contains a comma, newline
     * or double quote, then quotes are removed.
     * </p>
     * <p/>
     * <p>Any double quote escaped characters (a pair of double quotes) are unescaped
     * to just one double quote. </p>
     * <p/>
     * <p>If the value is not enclosed in double quotes, or is and does not contain a
     * comma, newline or double quote, then the String value is returned unchanged.</p>
     * </p>
     * <p/>
     * see <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Wikipedia</a> and
     * <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     *
     * @param str the input CSV column String, may be null
     * @return the input String, with enclosing double quotes removed and embedded double
     *         quotes unescaped, <code>null</code> if null string input
     * @since 2.4
     */
    public static String unescapeCsv(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter();
            unescapeCsv(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * <p>Returns a <code>String</code> value for an unescaped CSV column. </p>
     * <p/>
     * <p>If the value is enclosed in double quotes, and contains a comma, newline
     * or double quote, then quotes are removed.
     * </p>
     * <p/>
     * <p>Any double quote escaped characters (a pair of double quotes) are unescaped
     * to just one double quote. </p>
     * <p/>
     * <p>If the value is not enclosed in double quotes, or is and does not contain a
     * comma, newline or double quote, then the String value is returned unchanged.</p>
     * </p>
     * <p/>
     * see <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Wikipedia</a> and
     * <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     *
     * @param str the input CSV column String, may be null
     * @param out Writer to write the input String to, with enclosing double quotes
     *            removed and embedded double quotes unescaped, <code>null</code> if null string input
     * @throws IOException if error occurs on underlying Writer
     * @since 2.4
     */
    public static void unescapeCsv(Writer out, String str) throws IOException {
        if (str == null) {
            return;
        }
        if (str.length() < 2) {
            out.write(str);
            return;
        }
        if (str.charAt(0) != CSV_QUOTE || str.charAt(str.length() - 1) != CSV_QUOTE) {
            out.write(str);
            return;
        }

        // strip quotes
        String quoteless = str.substring(1, str.length() - 1);

        if (StringUtils.containsAny(quoteless, CSV_SEARCH_CHARS)) {
            // deal with escaped quotes; ie) ""
            str = StringUtils.replace(quoteless, CSV_QUOTE_STR + CSV_QUOTE_STR, CSV_QUOTE_STR);
        }

        out.write(str);
    }

    public static final String unescapeHtml3(final String input) {
        StringWriter writer = null;
        int len = input.length();
        int i = 1;
        int st = 0;
        while (true) {
            // look for '&'
            while (i < len && input.charAt(i-1) != '&')
                i++;
            if (i >= len)
                break;

            // found '&', look for ';'
            int j = i;
            while (j < len && j < i + MAX_ESCAPE + 1 && input.charAt(j) != ';')
                j++;
            if (j == len || j < i + MIN_ESCAPE || j == i + MAX_ESCAPE + 1) {
                i++;
                continue;
            }

            // found escape
            if (input.charAt(i) == '#') {
                // numeric escape
                int k = i + 1;
                int radix = 10;

                final char firstChar = input.charAt(k);
                if (firstChar == 'x' || firstChar == 'X') {
                    k++;
                    radix = 16;
                }

                try {
                    int entityValue = Integer.parseInt(input.substring(k, j), radix);

                    if (writer == null)
                        writer = new StringWriter(input.length());
                    writer.append(input.substring(st, i - 1));

                    if (entityValue > 0xFFFF) {
                        final char[] chrs = Character.toChars(entityValue);
                        writer.write(chrs[0]);
                        writer.write(chrs[1]);
                    } else {
                        writer.write(entityValue);
                    }

                } catch (NumberFormatException ex) {
                    i++;
                    continue;
                }
            }
            else {
                // named escape
                CharSequence value = lookupMap.get(input.substring(i, j));
                if (value == null) {
                    i++;
                    continue;
                }

                if (writer == null)
                    writer = new StringWriter(input.length());
                writer.append(input.substring(st, i - 1));

                writer.append(value);
            }

            // skip escape
            st = j + 1;
            i = st;
        }

        if (writer != null) {
            writer.append(input.substring(st, len));
            return writer.toString();
        }
        return input;
    }


    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


    /**
     * <p>Worker method for the {@link #escapeJavaScript(String)} method.</p>
     *
     * @param str                String to escape values in, may be null
     * @param escapeSingleQuotes escapes single quotes if <code>true</code>
     * @return the escaped string
     */
    private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length() * 2);
            escapeJavaStyleString(writer, str, escapeSingleQuotes);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * <p>Worker method for the {@link #escapeJavaScript(String)} method.</p>
     *
     * @param out               write to receieve the escaped string
     * @param str               String to escape values in, may be null
     * @param escapeSingleQuote escapes single quotes if <code>true</code>
     * @throws IOException if an IOException occurs
     */
    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz;
        sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                out.write("\\u" + hex(ch));
            } else if (ch > 0xff) {
                out.write("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                out.write("\\u00" + hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b':
                        out.write('\\');
                        out.write('b');
                        break;
                    case '\n':
                        out.write('\\');
                        out.write('n');
                        break;
                    case '\t':
                        out.write('\\');
                        out.write('t');
                        break;
                    case '\f':
                        out.write('\\');
                        out.write('f');
                        break;
                    case '\r':
                        out.write('\\');
                        out.write('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            out.write("\\u00" + hex(ch));
                        } else {
                            out.write("\\u000" + hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        if (escapeSingleQuote) {
                            out.write('\\');
                        }
                        out.write('\'');
                        break;
                    case '"':
                        out.write('\\');
                        out.write('"');
                        break;
                    case '\\':
                        out.write('\\');
                        out.write('\\');
                        break;
                    case '/':
                        out.write('\\');
                        out.write('/');
                        break;
                    default:
                        out.write(ch);
                        break;
                }
            }
        }
    }

    /**
     * <p>Returns an upper case hexadecimal <code>String</code> for the given
     * character.</p>
     *
     * @param ch The character to convert.
     * @return An upper case hexadecimal <code>String</code>
     */
    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }
}
