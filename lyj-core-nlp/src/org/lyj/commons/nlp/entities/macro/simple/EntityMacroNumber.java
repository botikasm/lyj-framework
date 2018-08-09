package org.lyj.commons.nlp.entities.macro.simple;

import org.lyj.commons.nlp.entities.data.numbers.AiNumberMatcher;
import org.lyj.commons.nlp.entities.macro.AbstractEntityMacro;
import org.lyj.commons.util.RegExpUtils;

import java.util.Collection;
import java.util.LinkedList;

public class EntityMacroNumber
        extends AbstractEntityMacro {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "number";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EntityMacroNumber() {
        super(NAME);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public String[] parse(final String lang,
                          final int start_index,
                          final String[] phrase) {
        final Collection<String> result = new LinkedList<>();
        for (int i = start_index; i < phrase.length; i++) {
            final String word = phrase[i];
            if (RegExpUtils.isNumeric(word)) {
                result.add(word);
            } else {
                final Number n = AiNumberMatcher.instance().matchOne(lang, word);
                if (null != n) {
                    result.add(n.toString());
                }
            }
        }

        return result.toArray(new String[0]);
    }


}
