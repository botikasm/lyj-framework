package org.ly.ose.server.application.programming;

import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.program.ProgramLogger;

/**
 * Runtime javascript logger tool.
 * This logger is invoked from "console.error..".
 */
public class OSEProgramLogger
        extends ProgramLogger {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final OSEProgram _program;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEProgramLogger(final OSEProgram program) {
        _program = program;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void handle(final Level level,
                          final Object... values) {
        final String message = StringUtils.toString(values, "\t");
        final String _program_id = null!=_program?_program.uid():"";

        if (StringUtils.hasText(_program_id)) {
            if (Level.SEVERE.equals(level)) {
                //BotLoggingController.instance(bot_id).error("javascript", message);
            } else if (Level.WARNING.equals(level)) {
                //BotLoggingController.instance(bot_id).warn("javascript", message);
            } else if (Level.INFO.equals(level)) {
                //BotLoggingController.instance(bot_id).info("javascript", message);
            } else {
                //BotLoggingController.instance(bot_id).debug("javascript", message);
            }
            LoggingUtils.getLogger(this).log(level, "TODO(handle logging in programs): " + message);
        } else {
            LoggingUtils.getLogger(this).log(level, "THIS MESSAGE SHOULD BE LOGGED INTO LOGGER REPO: " + message);
        }
        // TODO: implement a better javascript logger to trace and debug programs workflow
    }


}
