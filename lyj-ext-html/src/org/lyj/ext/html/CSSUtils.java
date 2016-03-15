package org.lyj.ext.html;

import java.io.IOException;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lyj.commons.util.StringUtils;


/**
 * Utility
 */
public class CSSUtils {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * Parse HTML for style tag and convert to inline css style.
     * Returned HTML is good for html emails.
     * @param html HTML to transform with inline css directives.
     * @return Inline styled html.
     */
    public static String inlineStyles(final String html) {
        final Document doc = Jsoup.parse(html);
        final String style = "style";
        final Elements els = doc.select(style);// to get all the style elements
        for (final Element e : els) {
            final String styleRules = e.getAllElements().get(0).data().replaceAll("\n", "").trim(), delims =
                    "{}";
            StringTokenizer st = new StringTokenizer(styleRules, delims);
            while (st.countTokens() > 1) {
                String selector = st.nextToken(), properties = st.nextToken();
                // Process selectors such as "a:hover"
                if (selector.indexOf(":") > 0) {
                    selector = selector.substring(0, selector.indexOf(":"));
                }
                if (!StringUtils.hasText(selector)) {
                    continue;
                }
                final Elements selectedElements = doc.select(selector);
                for (Element selElem : selectedElements) {
                    final String oldProperties = selElem.attr(style);
                    selElem.attr(
                            style,
                            oldProperties.length() > 0 ? concatenateProperties(oldProperties,
                                    properties) : properties);
                }
            }
            e.remove();
        }
        return doc.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String concatenateProperties(String oldProp, String newProp) {
        oldProp = oldProp.trim();
        if (!newProp.endsWith(";")) {
            newProp += ";";
        }
        return newProp + oldProp; // The existing (old) properties should take precedence.
    }

}
