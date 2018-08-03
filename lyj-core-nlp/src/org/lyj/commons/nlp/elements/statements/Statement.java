package org.lyj.commons.nlp.elements.statements;

import org.lyj.commons.nlp.elements.IKeywordConstants;
import org.lyj.commons.nlp.elements.Keyword;
import org.lyj.commons.util.RegExpUtils;
import org.lyj.commons.util.StringUtils;

/**
 * A unit of statement.
 * This is the simplest unity in semantic engine
 * <p>
 * ex: "l***"
 * Valid statements are: "***text", "te***xt", "text***", "text"
 */
public class Statement
        implements IKeywordConstants {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int MODE_NONE = -1;    // "text"
    private static final int MODE_START = 0;    // "***text"
    private static final int MODE_MIDDLE = 1;   // "te***xt"
    private static final int MODE_END = 2;      // "text***"


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _raw_text;
    private final int _char_count;
    private final boolean _has_wildchars;
    private final String _clean_text;
    private final String[] _text_tokens;
    private final int _mode;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Statement(final String text) {
        _char_count = this.len(text);
        _raw_text = _char_count > 0 ? text.toLowerCase() : "";
        _has_wildchars = (_char_count > 0) && text.contains(WILDCHAR);
        _clean_text = _has_wildchars ? this.cleanText(_raw_text) : _raw_text;
        _text_tokens = _has_wildchars ? this.textTokens(_raw_text) : new String[]{_raw_text};
        _mode = _has_wildchars ? this.mode(_raw_text) : MODE_NONE;
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Match statement with passed text.
     * Passed test is purged from any no-alphanumeric character
     *
     * @param text Text to check for matching. ex: "mytext?"
     * @return True if text matches.
     */
    public boolean match(final String text) {
        if (this.len(text) > 0) {
            final String clean_text = Keyword.clearKeyword(text); // remove dots, commas, etc...
            return this.matchText(clean_text);
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private int len(final String text) {
        return null != text ? text.length() : 0;
    }

    private String[] textTokens(final String text_with_wildchars) {
        final String text = StringUtils.replaceDuplicates(text_with_wildchars, WILDCHAR);
        return StringUtils.split(text, WILDCHAR, true);
    }

    private String cleanText(final String text_with_wildchars) {
        final String text = StringUtils.replaceDuplicates(text_with_wildchars, WILDCHAR);
        return StringUtils.replace(RegExpUtils.replaceNoAlphanumericChar(text), WILDCHAR, "");
    }

    private int mode(final String text_with_wildchars) {
        final int pos_first = text_with_wildchars.indexOf(WILDCHAR);
        if (pos_first == 0) {
            return MODE_START;
        } else {
            final int pos_last = text_with_wildchars.lastIndexOf(WILDCHAR);
            if (pos_last == text_with_wildchars.length() - 1) {
                return MODE_END;
            }
        }
        return MODE_MIDDLE;
    }

    private boolean matchText(final String text) {
        if (this.len(text) == _char_count) {
            if (_has_wildchars) {
                // are equals?
                if (text.equals(_clean_text)) {
                    return true;
                }
                if (_mode == MODE_START) {
                    // ***text
                    return text.endsWith(_clean_text);
                }
                if (_mode == MODE_MIDDLE && _text_tokens.length == 2) {
                    // te**xt
                    return text.startsWith(_text_tokens[0]) && text.endsWith(_text_tokens[1]);
                }
                if (_mode == MODE_END) {
                    // text***
                    return text.startsWith(_clean_text);
                }
            } else {
                return _raw_text.equals(text);
            }
        }
        return false;
    }


}
