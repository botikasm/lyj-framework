package org.ly.ose.server.application.programming.tools.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.MapConverter;

import java.util.Map;

/**
 *
 */
public class Tool_error
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "error";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _package_name;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_error(final OSEProgram program) {
        super(NAME, program);

        _package_name = super.info().fullName();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    public Map normalize(final Object raw_error) {
        return toMapError(raw_error);
    }

    public String name(final Object raw_error){
        final Map map = toMapError(raw_error);
        return StringUtils.toString(map.get("name"));
    }

    public String message(final Object raw_error){
        final Map map = toMapError(raw_error);
        return StringUtils.toString(map.get("message"));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static Map<String, String> getError(final String message) {
        return MapBuilder.createSS()
                .put("type", "error")
                .put("message", message)
                .toMap();
    }

    public static Map toMapError(final Object raw_error) {
        if (raw_error instanceof ScriptObjectMirror) {
            final ScriptObjectMirror som = (ScriptObjectMirror) raw_error;
            final String name = som.getClassName();
            if (name.equalsIgnoreCase("Error")) {
                return getError((String)som.get("message"));
            }
        }

        if (raw_error instanceof String) {
            return getError((String) raw_error);
        }
        return MapConverter.toMap(raw_error);
    }


}
