package org.lyj.ext.script.program;

import org.lyj.commons.util.PathUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProgramScriptCache {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, String> _cache_script;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ProgramScriptCache() {
        _cache_script = new ConcurrentHashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void clear() {
        synchronized (_cache_script) {
            _cache_script.clear();
        }
    }

    public void clear(final String root) {
        synchronized (_cache_script) {
            final String pattern = PathUtils.concat(root, "*");
            final Set<String> keys = _cache_script.keySet();
            for (final String key : keys) {
                if (PathUtils.pathMatch(key, pattern)) {
                    _cache_script.remove(key);
                }
            }
        }
    }

    public boolean contains(final String key) {
        synchronized (_cache_script) {
            return _cache_script.containsKey(key);
        }
    }

    public String get(final String key) {
        synchronized (_cache_script) {
            return _cache_script.get(key);
        }
    }

    public void put(final String key, final String value) {
        synchronized (_cache_script) {
            _cache_script.put(key, value);
        }
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
