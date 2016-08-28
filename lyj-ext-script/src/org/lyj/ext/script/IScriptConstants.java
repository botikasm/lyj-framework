package org.lyj.ext.script;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.MapBuilder;

import java.util.Map;

/**
 * common constants
 */
public interface IScriptConstants {


    String ENCODING = CharEncoding.UTF_8;

    // ------------------------------------------------------------------------
    //                      e n g i n e s
    // ------------------------------------------------------------------------

    String ENGINE_JAVASCRIPT = "javascript";

    String[] ENGINES = new String[]{
            ENGINE_JAVASCRIPT
    };

    Map<String, String> ENGINES_EXT = MapBuilder.createSS()
            .put(ENGINE_JAVASCRIPT, ".js")
            .toMap();


}
