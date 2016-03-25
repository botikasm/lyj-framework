package org.lyj.desktopfences.app.controllers.archive;

import org.json.JSONObject;
import org.lyj.commons.util.JsonWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Index wrapper.
 * Usually contains a grouping key (ex. "tag") and a set of ids.
 */
public class Indexes {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Set<String>> _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Indexes() {
        _data = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject json(final boolean includeData) {
        final JSONObject result = new JSONObject();
        for (final Map.Entry<String, Set<String>> entry : _data.entrySet()) {
            if(includeData){
                result.put(entry.getKey(), JsonWrapper.toJSONArray(entry.getValue()));
            } else {
                result.put(entry.getKey(), entry.getValue().size());
            }
        }
        return result;
    }

    public void clear() {
        _data.clear();
    }

    public void add(final String key, final String value) {
        if (!_data.containsKey(key)) {
            _data.put(key, new HashSet<>());
        }
        _data.get(key).add(value);
    }

    public Set<String> get(final String key) {
        return _data.get(key);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
