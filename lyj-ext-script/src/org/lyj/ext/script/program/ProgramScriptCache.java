package org.lyj.ext.script.program;

import java.util.HashMap;
import java.util.Map;

public class ProgramScriptCache {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, String> _cache_script;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ProgramScriptCache() {
        _cache_script = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean contains(final String key) {
        return _cache_script.containsKey(key);
    }

    public String get(final String key) {
        return _cache_script.get(key);
    }

    public void put(final String key, final String value) {
        _cache_script.put(key, value);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ProgramScriptCache __instance;

    public static ProgramScriptCache instance() {
        if (null == __instance) {
            __instance = new ProgramScriptCache();
        }

        return __instance;
    }

}
