package org.lyj.commons.nlp.entities.regex;

import org.lyj.commons.util.RegExpUtils;

import java.util.Collection;
import java.util.LinkedList;

public class RegExHelper {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private RegExHelper() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String[] parse(final int start_index,
                          final String[] phrase,
                          final String pattern) {
        final Collection<String> result = new LinkedList<>();
        for (int i = start_index; i < phrase.length; i++) {
            final String word = phrase[i];
            if (RegExpUtils.match(word, pattern)) {
                result.add(word);
            }
        }
        return result.toArray(new String[0]);
    }

    public String clear(final String word) {
        String response = word;
        // remove ending dot
        if (response.endsWith(".") ||
                response.endsWith(",") ||
                response.endsWith(":") ||
                response.endsWith(";")) {
            response = response.substring(0, response.length() - 1);
        }

        if (!RegExpUtils.isValidEmail(response) &&
                !RegExpUtils.isValidPhoneNumber(response) &&
                !RegExpUtils.isNumeric(response) &&
                !RegExpUtils.isValidSocialSecurityNumber(response)) {
            response = RegExpUtils.replaceNotChars(response);
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static RegExHelper __instance;

    public static synchronized RegExHelper instance() {
        if (null == __instance) {
            __instance = new RegExHelper();
        }
        return __instance;
    }
}
