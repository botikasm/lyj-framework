package org.lyj.commons.util;

import org.json.JSONObject;
import org.lyj.commons.logging.AbstractLogEmitter;

import java.util.Map;

/**
 *  JSON wrapped object
 */
public class JsonItem
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    protected final JsonWrapper _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JsonItem(final JSONObject item){
        _data = new JsonWrapper(item);
    }

    @Override
    public String toString() {
        return _data.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JsonWrapper data() {
        return _data;
    }

    public JSONObject json() {
        return _data.getJSONObject();
    }

    public Map<String, ?> map() {
        return _data.toMap();
    }

}
