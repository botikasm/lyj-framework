package org.lyj.ext.script;

import org.lyj.ext.script.program.Program;
import org.lyj.ext.script.program.ProgramLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for scripts.
 */
public class ScriptController {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Object> _context;
    private String _encoding;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ScriptController() {
        _context = new HashMap<>();
        _encoding = IScriptConstants.ENCODING;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Map<String, Object> context() {
        return _context;
    }

    public String encoding() {
        return _encoding;
    }

    public void encoding(final String value){
        _encoding = value;
    }

    public Program create() {
        return new Program(_encoding, _context);
    }

    public Program create(final String engineName) {
        return new Program(_encoding, _context).engineName(engineName);
    }

    public Program create(final Program.OutputCallback callback) {
        return new Program(_encoding, _context, callback);
    }

    public Program create(final Program.OutputCallback callback, final String engineName) {
        return new Program(_encoding, _context, callback).engineName(engineName);
    }

    public Program create(final ProgramLogger logger) {
        return new Program(_encoding, _context, logger);
    }

    public Program create(final ProgramLogger logger, final String engineName) {
        return new Program(_encoding, _context, logger).engineName(engineName);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ScriptController __instance;

    public static ScriptController instance() {
        if (null == __instance) {
            __instance = new ScriptController();
        }
        return __instance;
    }


}
