package org.lyj.commons.nlp.entities.macro.withparams;

import org.lyj.commons.nlp.entities.macro.AbstractEntityMacro;
import org.lyj.commons.util.RegExpUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Rule:
 * - Starts with x
 * - if condition is respected, RegExp is applied
 * <p>
 * Usage:
 * integerLenG#10
 */
public class EntityMacroStartsWithExp
        extends AbstractEntityMacro {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "startsWithExp";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _start_text;
    private final String _regex_pattern;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EntityMacroStartsWithExp(final String[] args) {
        super(NAME, args);
        _start_text = args.length > 0 ? args[0] : "";
        _regex_pattern = args.length > 1 ? args[1] : "";
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
            if (StringUtils.hasText(_start_text) && word.startsWith(_start_text)) {
                if (StringUtils.hasText(_regex_pattern)) {
                    result.add(RegExpUtils.matches(_regex_pattern, word));
                } else {
                    result.add(word);
                }
            }
        }
        return result.toArray(new String[0]);
    }


}
