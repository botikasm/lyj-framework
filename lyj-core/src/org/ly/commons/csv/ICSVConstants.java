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
 * ICSVConstants.java
 *
 */

package org.ly.commons.csv;

import org.ly.commons.lang.CharEncoding;

import java.text.DateFormat;
import java.util.Locale;

/**
 *
 */
public interface ICSVConstants {

    public static final String DEFAULT_ENCODING = CharEncoding.getDefault();

    /**
     * The character used for escaping quotes.
     */
    public static final char ESCAPE_CHARACTER = '"';

    /**
     * The default separator to use if none is supplied to the constructor.
     */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';

    /**
     * The quote constant to use when you wish to suppress all quoting.
     */
    public static final char NO_QUOTE_CHARACTER = '\u0000';

    /**
     * Default line terminator uses platform encoding.
     */
    public static final String DEFAULT_LINE_END = "\n";

    /**
     * The default line to start reading.
     */
    public static final int DEFAULT_SKIP_LINES = 0;

    /**
     * DATA FORMAT: The default date format
     */
    public static final int DEFAULT_DATE_FORMAT = DateFormat.SHORT;
    /**
     * DATA FORMAT: Default locale
     */
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    /**
     * DATA FORMAT: The default number format
     */
    public static final String DEFAULT_NUMBER_PATTERN = "#,##0.0###;-#,##0.0###";

}
