package org.lyj.commons.nlp.entities.macro.withparams;

import org.lyj.commons.nlp.entities.macro.AbstractEntityMacro;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.DateWrapper;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Usage:
 * * #date#yyyyMMdd
 */
public class EntityMacroDate
        extends AbstractEntityMacro {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "date";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _pattern;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EntityMacroDate(final String[] args) {
        super(NAME, args);
        _pattern = args.length > 0 ? ConversionUtils.toString(args[0]) : "yyyy MM dd";
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public String[] parse(final String lang,
                          final int start_index,
                          final String[] phrase) {
        final Collection<String> result = new LinkedList<>();
        final Locale locale = LocaleUtils.getLocaleByLang(lang);

        for (int i = start_index; i < phrase.length; i++) {
            final String word = phrase[i];
            final String parsed_date = asDate(word, _pattern);
            if (StringUtils.hasText(parsed_date)) {
                result.add(parsed_date);
            }
        }
        return result.toArray(new String[0]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String asDate(final String text,
                                 final String pattern) {
        try {
            final DateWrapper dw = new DateWrapper(text, pattern);
            return dw.toString(pattern);
        } catch (Throwable ignored) {

        }
        return "";
    }


}
