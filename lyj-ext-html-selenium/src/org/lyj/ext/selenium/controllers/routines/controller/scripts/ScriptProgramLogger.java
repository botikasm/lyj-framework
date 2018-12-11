package org.lyj.ext.selenium.controllers.routines.controller.scripts;

import org.lyj.commons.logging.Level;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.program.ProgramLogger;
import org.lyj.ext.script.utils.Converter;
import org.lyj.ext.selenium.controllers.routines.controller.RoutineLogger;
import org.lyj.ext.selenium.controllers.routines.model.ModelPackage;

/**
 * Runtime javascript logger tool.
 * This logger is invoked from "console.error..".
 */
public class ScriptProgramLogger
        extends ProgramLogger {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final ScriptProgram _program;
    private final RoutineLogger _logger;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ScriptProgramLogger(final ScriptProgram program, final RoutineLogger logger) {
        _program = program;
        _logger = logger;
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
            final ModelPackage info = _program.info();
            final String level_name = info.logLevel();
            if (StringUtils.hasText(level_name)) {
                final Level program_level = Level.getLevel(level_name);
                if (null != program_level && isLoggable(level, program_level)) {
                    final String message = StringUtils.toString(Converter.toJsonArray(values), "\t");
                    log(_logger, _program.info(), level, message);
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

    private static void log(final RoutineLogger logger,
                            final ModelPackage info,
                            final Level level,
                            final String message) {
        logger.log(level, "", message);
    }

}
