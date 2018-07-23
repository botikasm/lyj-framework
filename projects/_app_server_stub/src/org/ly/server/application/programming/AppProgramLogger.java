package org.ly.server.application.programming;

import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.program.ProgramLogger;

public class AppProgramLogger
        extends ProgramLogger {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _program_id;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AppProgramLogger(final String program_id) {
        _program_id = program_id;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void handle(final Level level,
                          final Object... values) {
        final String message = StringUtils.toString(values, "\t");

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
            LoggingUtils.getLogger(this).log(level, "TODO(hadle logging in programs): " + message);
        } else {
            LoggingUtils.getLogger(this).log(level, "THIS MESSAGE SHOULD BE LOGGED INTO LOGGER REPO: " + message);
        }
    }


}
