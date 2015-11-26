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
 * CSVFileWriter.java
 *
 */
package org.ly.commons.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public final class CSVFileWriter extends CSVWriter {

    private File _file;

    public CSVFileWriter() {
    }

    public CSVFileWriter(final String fileName) throws IOException {
        this(new File(fileName),
                DEFAULT_SEPARATOR,
                DEFAULT_QUOTE_CHARACTER,
                DEFAULT_LINE_END,
                DEFAULT_LOCALE);
    }

    public CSVFileWriter(final String fileName, char separator,
                         final Locale locale) throws IOException {
        this(new File(fileName),
                separator,
                DEFAULT_QUOTE_CHARACTER,
                DEFAULT_LINE_END,
                locale);
    }

    public CSVFileWriter(final File file, char separator,
                         final Locale locale) throws IOException {
        this(file,
                separator,
                DEFAULT_QUOTE_CHARACTER,
                DEFAULT_LINE_END,
                locale);
    }

    public CSVFileWriter(final File file, char separator,
                         char quoteChar, final String lineEnd,
                         final Locale locale) throws IOException {
        super.setLineEnd(lineEnd);
        super.setLocale(locale);
        super.setQuotechar(quoteChar);
        super.setSeparator(separator);
        this.setFile(file);
    }

    public File getFile() {
        return _file;
    }

    public void setFile(final File file) throws IOException {
        _file = file;
        super.setWriter(new FileWriter(_file));
    }

    @Override
    public int writeAll(List<String[]> allLines) {
        return super.writeAll(allLines);
    }

    @Override
    public int writeAll(List<Map> allLines, boolean includeColumnNames) {
        return super.writeAll(allLines, includeColumnNames);
    }

    @Override
    public int writeAll(ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        return super.writeAll(rs, includeColumnNames);
    }

    @Override
    public void writeNext(String[] nextLine) {
        super.writeNext(nextLine);
    }
}
