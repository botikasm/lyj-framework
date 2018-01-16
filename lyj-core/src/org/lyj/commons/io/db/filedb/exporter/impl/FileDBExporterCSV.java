package org.lyj.commons.io.db.filedb.exporter.impl;

import org.lyj.commons.io.db.filedb.FileDBCollection;
import org.lyj.commons.io.db.filedb.FileDBEntity;
import org.lyj.commons.io.db.filedb.exporter.AbstractFileDBExporter;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.StringUtils;

import java.io.Writer;
import java.util.Locale;

/**
 * Export database collections in JSON format
 */
public class FileDBExporterCSV
        extends AbstractFileDBExporter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String EXTENSION = "csv";

    private static final String PROP_SEPARATOR = "separator";
    private static final String PROP_SEPARATOR_VALUE = ";";

    private static final String PROP_QUOTE_CHAR = "quote_char";
    private static final String PROP_QUOTE_CHAR_VALUE = "\"";

    private static final String PROP_LOCALE = "locale";
    private static final String PROP_LOCALE_VALUE = LocaleUtils.getCurrent().toString();

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileDBExporterCSV() {
        this.separator(PROP_SEPARATOR_VALUE);
        this.quoteChar(PROP_QUOTE_CHAR_VALUE);
        this.locale(LocaleUtils.getLocale(PROP_LOCALE_VALUE));
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


    public String separator() {
        return super.properties().getString(PROP_SEPARATOR);
    }

    public FileDBExporterCSV separator(final String value) {
        super.properties().put(PROP_SEPARATOR, value);
        return this;
    }

    public String quoteChar() {
        return super.properties().getString(PROP_QUOTE_CHAR);
    }

    public FileDBExporterCSV quoteChar(final String value) {
        super.properties().put(PROP_QUOTE_CHAR, value);
        return this;
    }

    public String locale() {
        return super.properties().getString(PROP_LOCALE);
    }

    public FileDBExporterCSV locale(final Locale value) {
        super.properties().put(PROP_LOCALE, value.toString());
        return this;
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    @Override
    protected String extension() {
        return EXTENSION;
    }

    @Override
    protected void export(final FileDBCollection collection,
                          final String target) throws Exception {
        final String field_sep = this.separator();
        final String row_sep = "\n";
        final String[] fields = collection.fields();

        final Writer writer = super.getWriter(target);
        try {

            // header
            int count = 0;
            for (final String field : fields) {
                if (count > 0) {
                    writer.write(field_sep);
                }
                writer.write(field);
                count++;
            }
            writer.write(row_sep);

            // rows
            collection.forEach((final FileDBEntity entity) -> {
                try {
                    if (entity.index() > 0) {
                        writer.write(row_sep);
                    }
                    // write
                    int count_fields = 0;
                    for (final String field : fields) {
                        if (count_fields > 0) {
                            writer.write(field_sep);
                        }
                        final Object value = entity.get(field);
                        writer.write(this.quote(value));

                        count_fields++;
                    }
                } catch (Throwable ignored) {
                }
                return false;
            });
        } finally {
            writer.flush();
            writer.close();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String quote(final Object value) {
        try {
            if (null == value) {
                return "";
            } else {
                final Locale locale = LocaleUtils.getLocale(this.locale());
                final String decimal_sep = new String(new char[]{LocaleUtils.getDecimalFormatSymbols(locale).getDecimalSeparator()});
                if (value instanceof String || decimal_sep.equals(this.separator())) {
                    final String quote_char = this.quoteChar();
                    final String s_value = StringUtils.toString(locale, value);
                    final String escaped = StringUtils.replace(s_value, quote_char, quote_char + quote_char);
                    return StringUtils.quote(escaped, quote_char);
                }
            }
        } catch (Throwable t) {
            // System.out.println(t);
        }
        return StringUtils.toString(value);
    }

}
