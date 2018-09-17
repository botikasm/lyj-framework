package org.lyj.commons.nlp.entities.macro.withparams;

import org.lyj.commons.nlp.entities.macro.AbstractEntityMacro;
import org.lyj.commons.util.RegExpUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Rule:
 * - RegExp is applied
 * <p>
 * Usage:
 * #exp#([0-9a-zA-Z-])
 */
public class EntityMacroExp
        extends AbstractEntityMacro {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "exp";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _regex_pattern;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EntityMacroExp(final String[] args) {
        super(NAME, args);
        _regex_pattern = args.length > 0 ? args[0] : "";
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
            if (StringUtils.hasText(_regex_pattern)) {
                final String value = RegExpUtils.matches(_regex_pattern, word);
                if (StringUtils.hasText(value)) {
                    result.add(value);
                }
            } else {
                result.add(word);
            }
        }
        return result.toArray(new String[0]);
    }


}
