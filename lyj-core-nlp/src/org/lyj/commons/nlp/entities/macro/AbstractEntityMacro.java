package org.lyj.commons.nlp.entities.macro;


import org.lyj.commons.nlp.entities.regex.RegExHelper;

public abstract class AbstractEntityMacro {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _name;
    private final String[] _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractEntityMacro(final String name) {
        _name = name;
        _params = new String[0];
    }

    public AbstractEntityMacro(final String name, final String[] args) {
        _name = name;
        _params = args;
    }

    public abstract String[] parse(final String lang, final int start_index, final String[] phrase);

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public String name() {
        return _name;
    }

    public String[] params() {
        return _params;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected String clear(final String word) {
        return RegExHelper.instance().clear(word);
    }

}
