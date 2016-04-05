package org.lyj.gui.application.app.utils;

import javafx.scene.text.Font;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Font Helper
 */
public class FontRegistry {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Font> _fonts;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private FontRegistry() {
        _fonts = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean contains(final String name) {
        return _fonts.containsKey(name);
    }

    public Map<String, Font> fonts() {
        return _fonts;
    }

    public Set<String> fontNames() {
        return _fonts.keySet();
    }

    public Font font(final String name) {
        return _fonts.get(name);
    }

    public Font load(final byte[] data,
                     final String name) {
        return this.load(data, name, 12);
    }

    public Font load(final byte[] data,
                     final String name,
                     final double size) {
        final Font response = Font.loadFont(new ByteArrayInputStream(data), size);
        _fonts.put(name, response);
        return response;
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static FontRegistry __instance;

    public static FontRegistry instance() {
        if (null == __instance) {
            __instance = new FontRegistry();
        }
        return __instance;
    }

}
