package org.ly.ose.server.application.programming;

import org.ly.ose.server.application.persistence.debugging.model.ModelLogging;
import org.ly.ose.server.application.persistence.debugging.service.ServiceLogging;
import org.lyj.commons.async.Async;
import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.program.ProgramLogger;
import org.lyj.ext.script.utils.Converter;

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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void handle(final Level level,
                          final Object... values) {
        if (null != _program) {
            final OSEProgramInfo info = _program.info();
            final String level_name = info.logLevel();
            if (StringUtils.hasText(level_name)) {
                final Level program_level = Level.getLevel(level_name);
                if (null != program_level && isLoggable(level, program_level)) {
                    final String message = StringUtils.toString(Converter.toJsonArray(values), "\t");
                    log(_program.info(), level, message);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static boolean isLoggable(final Level check_level,
                                      final Level main_level) {
        return main_level.getNumValue() <= check_level.getNumValue();
    }

    private static void log(final OSEProgramInfo info,
                            final Level level,
                            final String message) {
        Async.invoke((args)->{
            try {
                final ModelLogging entity = new ModelLogging();
                entity.clientId((String) info.data().get(OSEProgramInfo.FLD_CLIENT_ID));
                entity.sessionId((String) info.data().get(OSEProgramInfo.FLD_SESSION_ID));
                entity.programName(info.namespace().concat(".").concat(info.name()));
                entity.message(message);
                entity.level(level.name());

                ServiceLogging.instance().upsert(entity);
            } catch (Throwable t) {
                LoggingUtils.getLogger(OSEProgramLogger.class).error("log", t);
            }
        });
    }

}
