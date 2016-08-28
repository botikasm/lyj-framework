package org.lyj.ext.script.program.tools.sys;

import org.lyj.commons.logging.Level;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.ScriptLogger;
import org.lyj.ext.script.program.Program;
import org.lyj.ext.script.program.ProgramLogger;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Add "console" object to script.
 * This tool is automatically added to script context.
 */
public class ToolConsole
        extends ProgramLogger {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "console";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Program.OutputCallback _callback;
    private final ScriptLogger _logger;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ToolConsole(final Program.OutputCallback callback) {
        _callback = callback;
        _logger = new ScriptLogger();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    protected void handle(final Level level, final Object... values) {
        if (null != _callback) {
            _callback.handle(null != level ? level.name() : "", values);
        } else {
            _logger.log(null != level ? level : Level.INFO, "", StringUtils.toString(values, "\t"));
        }
    }

}
