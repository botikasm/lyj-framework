package org.lyj.ext.html;

import com.steadystate.css.parser.CSSOMParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.*;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class CSSInlineStyler
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private CSSInlineStyler() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String convert(final String html) {
        try {
            final Document document = Jsoup.parse(html);
            final String styleRules = getStyleRulesFromDocument(document);
            final CSSStyleSheet stylesheet = styleSheetFromString(styleRules);
            final CSSRuleList rules = stylesheet.getCssRules();

            final Map<Element, Map<String, String>> elementStyles = new HashMap<>();

            /*
             * For each rule in the style sheet, find all HTML elements that match
             * based on its selector and store the style attributes in the map with
             * the selected element as the key.
             */
            for (int i = 0; i < rules.getLength(); i++) {
                final CSSRule rule = rules.item(i);
                if (rule instanceof CSSStyleRule) {
                    final CSSStyleRule styleRule = (CSSStyleRule) rule;
                    final String selector = styleRule.getSelectorText();

                    // Ignore pseudo classes, as JSoup's selector cannot handle
                    // them.
                    if (!selector.contains(":")) {
                        final Elements selectedElements = document.select(selector);
                        for (final Element selected : selectedElements) {
                            if (!elementStyles.containsKey(selected)) {
                                elementStyles.put(selected, new LinkedHashMap<>());
                            }

                            final CSSStyleDeclaration styleDeclaration = styleRule.getStyle();

                            for (int j = 0; j < styleDeclaration.getLength(); j++) {
                                final String propertyName = styleDeclaration.item(j);
                                final String propertyValue = styleDeclaration.getPropertyValue(propertyName);
                                final Map<String, String> elementStyle = elementStyles.get(selected);
                                elementStyle.put(propertyName, propertyValue);
                            }

                        }
                    }
                }
            }

            /*
             * Apply the style attributes to each element and remove the "class"
             * attribute.
             */
            for (final Map.Entry<Element, Map<String, String>> elementEntry : elementStyles.entrySet()) {
                final Element element = elementEntry.getKey();
                final StringBuilder builder = new StringBuilder();
                for (final Map.Entry<String, String> styleEntry : elementEntry.getValue().entrySet()) {
                    builder.append(styleEntry.getKey()).append(":").append(styleEntry.getValue()).append(";");
                }
                builder.append(element.attr("style"));
                element.attr("style", builder.toString());
                element.removeAttr("class");
            }

            return document.html();
        } catch (Throwable t) {
            super.error("convert", t);
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String getStyleRulesFromDocument(final Document document){
        final StringBuilder sb = new StringBuilder();
        final Elements els = document.select("style");
        for (final Element e : els) {
            final String styleRules = e.getAllElements().get(0).data().replaceAll("\n", "").trim(), delims = "{}";
            sb.append(styleRules);
        }
        return sb.toString();
    }

    private static CSSStyleSheet styleSheetFromString(final String css) throws IOException {
        final CSSOMParser parser = new CSSOMParser();
        return parser.parseStyleSheet(readString(css), null, null);
    }

    private static InputSource readString(final String text) {
        return new InputSource(new StringReader(text));
    }

    private static InputSource readFile(final File in) throws FileNotFoundException {
        return new InputSource(new InputStreamReader(new BufferedInputStream(new FileInputStream(in))));
    }

    private static InputSource readFile(final String fileName) throws FileNotFoundException {
        return readFile(new File(fileName));
    }

    // ------------------------------------------------------------------------
    //                      s i n g l e t o n
    // ------------------------------------------------------------------------

    private static CSSInlineStyler __instance;

    public static CSSInlineStyler instance(){
        if(null==__instance){
            __instance = new CSSInlineStyler();
        }
        return __instance;
    }
}
