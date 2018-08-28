package org.ly.ose.server.application.programming.tools.utils;

import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.converters.MapConverter;

import java.util.Map;

/**
 *
 */
public class Tool_format
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "format"; // used as $format.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _package_name;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_format(final OSEProgram program) {
        super(NAME, program);

        _package_name = super.info().fullName();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    public String template(final String text,
                           final String prefix,
                           final String suffix,
                           final Object raw_map) {
        final Map<String, Object> map = MapConverter.toMap(raw_map);
        return FormatUtils.formatTemplate(text, prefix, suffix, map);
    }

    public String template(final String text,
                           final Object raw_map) {
        final Map<String, Object> map = MapConverter.toMap(raw_map);
        return FormatUtils.formatTemplate(text, "{{", "}}", map);
    }


}
