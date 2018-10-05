package org.lyj.commons.nlp.entities.macro.simple;

import org.lyj.commons.nlp.entities.macro.AbstractEntityMacro;
import org.lyj.commons.util.RegExpUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Detect names of persons
 */
public class EntityMacroName
        extends AbstractEntityMacro {

    // ------------------------------------------------------------------------
    //                      c o n s t 
    // ------------------------------------------------------------------------

    public static final String NAME = "name"; // detects names of persons (first should be Uppercase)

    private static final String[] DOTS = {".", "?", "!", "¿"};

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EntityMacroName() {
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
        int last_i = -1;
        int last_ending_dot = -1;
        String last_name = "";
        for (int i = start_index; i < phrase.length; i++) {
            final String word = phrase[i];
            last_ending_dot = endsWithDot(word) ? i : last_ending_dot;
            // get uppercase (not starting word that usually is uppercase)
            if (i > 0 && last_ending_dot != i - 1 && RegExpUtils.isFirstUppercase(word)
                    && !StringUtils.contains(word, new String[]{"-", ":", "="})) {
                // check contiguous
                if (last_i > 0) {
                    if (StringUtils.hasText(last_name)) {
                        if (last_i + 1 == i) {
                            // contiguous
                            //result.add(last_name.trim() + " " + word);
                            //last_name = ""; // reset temp cache
                            last_name += " " + word;
                        } else {
                            // not contiguous
                            result.add(last_name.trim());
                            last_name = ""; // reset temp cache
                        }
                    } else {
                        // append to temp cache
                        last_name += " " + word;
                    }
                } else {
                    // append to temp cache
                    last_name += " " + word;
                }
            } else {
                if(StringUtils.hasText(last_name)){
                    result.add(last_name.trim());
                    last_name = ""; // reset temp cache
                }
            }
            last_i++;
        }
        if (StringUtils.hasText(last_name)) {
            result.add(last_name.trim());
        }
        return result.toArray(new String[0]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean endsWithDot(final String word) {
        for (final String dot : DOTS) {
            if (word.endsWith(dot)) {
                return true;
            }
        }
        return false;
    }


}
